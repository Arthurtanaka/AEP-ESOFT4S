"""
MoveCity - Teste automatizado da API (roteiro de evidências do "código funciona").

Como usar (precisa do Python 3, sem instalar nada além dele):
  1) Recrie o banco limpo:   mysql -u root -p -e "DROP DATABASE IF EXISTS movecity"
                             mysql -u root -p -e "source database/schema.sql"
                             mysql -u root -p -e "source database/dados_exemplo.sql"
  2) Suba o sistema:         ./executar.sh   (ou executar.bat)
  3) Em outro terminal:      python3 docs/testes/teste_api.py              (porta 8080)
                             python3 docs/testes/teste_api.py http://localhost:9090

O script cria usuários, rotas, transportes, favoritos e alertas de teste e confere, ponto a ponto,
os requisitos RQ01 a RQ10 e as regras de negócio RN01 a RN09. No final mostra quantos testes passaram.
"""
import json, sys, urllib.request, urllib.parse, urllib.error
B = sys.argv[1].rstrip("/") if len(sys.argv) > 1 else "http://localhost:8080"
ok = fail = 0
def req(m, p, body=None, tok=None, raw=False):
    h = {"Content-Type": "application/json"}
    if tok: h["Authorization"] = "Bearer " + tok
    r = urllib.request.Request(B + p, method=m, headers=h, data=json.dumps(body).encode() if body is not None else None)
    try:
        with urllib.request.urlopen(r) as x:
            t = x.read().decode()
            return x.status, (t if raw else json.loads(t))
    except urllib.error.HTTPError as e:
        t = e.read().decode()
        try: return e.code, json.loads(t)
        except: return e.code, t
def check(nome, cond, extra=""):
    global ok, fail
    if cond: ok += 1; print("PASS", nome)
    else: fail += 1; print("FAIL", nome, extra)

# ---------- páginas estáticas ----------
s, t = req("GET", "/", raw=True); check("GET / serve index.html", s == 200 and "MoveCity" in t)
s, t = req("GET", "/app.js", raw=True); check("GET /app.js", s == 200)
s, t = req("GET", "/../../etc/passwd", raw=True); check("path traversal bloqueado", s in (404, 400))

# ---------- cadastro / login (RQ01, RQ02, RN01, RN02) ----------
s, r = req("POST", "/api/usuarios", {"nome": "Maria Teste", "email": "maria@teste.com", "senha": "abc123", "perfil": "PASSAGEIRO", "preferencia": "MAIS_RAPIDO"})
check("cadastro passageiro 201", s == 201 and r["perfil"] == "PASSAGEIRO" and "senhaHash" not in r, r)
s, r = req("POST", "/api/usuarios", {"nome": "Maria Dois", "email": "MARIA@teste.com", "senha": "abc123", "perfil": "PASSAGEIRO"})
check("RN01 e-mail repetido bloqueado", s == 400 and "e-mail" in r["erro"], r)
s, r = req("POST", "/api/usuarios", {"nome": "X", "email": "x", "senha": "1", "perfil": "PASSAGEIRO"})
check("validação de cadastro", s == 400, r)
s, r = req("POST", "/api/usuarios", {"nome": "Falso Admin", "email": "fa@teste.com", "senha": "abc123", "perfil": "ADMINISTRADOR", "codigoAdmin": "errado"})
check("admin sem código correto bloqueado", s == 400, r)
s, r = req("POST", "/api/usuarios", {"nome": "Admin Novo", "email": "an@teste.com", "senha": "abc123", "perfil": "ADMINISTRADOR", "codigoAdmin": "movecity2026"})
check("admin com código correto 201", s == 201 and r["perfil"] == "ADMINISTRADOR", r)
s, r = req("POST", "/api/sessao", {"email": "maria@teste.com", "senha": "errada"}); check("login senha errada 400", s == 400, r)
s, r = req("POST", "/api/sessao", {"email": "maria@teste.com", "senha": "abc123"}); check("login passageiro", s == 200 and r["token"], r); TP = r["token"]
s, r = req("POST", "/api/sessao", {"email": "admin@movecity.com", "senha": "admin123"}); check("login admin demo", s == 200, r); TA = r["token"]
s, r = req("GET", "/api/sessao", tok=TP); check("GET sessao", s == 200 and r["email"] == "maria@teste.com", r)
s, r = req("GET", "/api/sessao"); check("sessao sem token 401", s == 401, r)


# ---------- transportes (RQ04, RN03) ----------
s, r = req("GET", "/api/transportes"); check("lista transportes", s == 200 and len(r) >= 3, r)
s, r = req("POST", "/api/transportes", {"tipo": "BICICLETA", "nome": "Bike", "velocidadeMediaKmH": 15}, tok=TP); check("RN03 passageiro não cria transporte (403)", s == 403, r)
s, r = req("POST", "/api/transportes", {"tipo": "BICICLETA", "nome": "Bike", "velocidadeMediaKmH": 15}); check("sem login não cria transporte (401)", s == 401, r)
s, r = req("POST", "/api/transportes", {"tipo": "ONIBUS", "nome": "Ônibus Expresso", "velocidadeMediaKmH": 30, "emissaoCo2GKm": 50, "capacidadePassageiros": 60}, tok=TA)
check("CREATE transporte ônibus", s == 201 and r["tipo"] == "ONIBUS" and r["capacidadePassageiros"] == 60, r); IDT = r["idTransporte"]
s, r = req("POST", "/api/transportes", {"tipo": "CARRO", "nome": "Carro ruim", "velocidadeMediaKmH": 0, "emissaoCo2GKm": 100, "fatorCongestionamento": 1.2}, tok=TA); check("validação velocidade", s == 400, r)
s, r = req("PUT", f"/api/transportes/{IDT}", {"nome": "Ônibus Expresso 2", "velocidadeMediaKmH": 35, "emissaoCo2GKm": 55, "capacidadePassageiros": 70}, tok=TA)
check("UPDATE transporte", s == 200 and r["nome"] == "Ônibus Expresso 2" and r["velocidadeMediaKmH"] == 35, r)
s, r = req("DELETE", f"/api/transportes/{IDT}", tok=TA); check("DELETE transporte", s == 200, r)
s, r = req("DELETE", f"/api/transportes/{IDT}", tok=TA); check("DELETE inexistente 404", s == 404, r)
carro = [t for t in req("GET", "/api/transportes")[1] if t["tipo"] == "CARRO"][0]
s, r = req("DELETE", f"/api/transportes/{carro['idTransporte']}", tok=TA); check("transporte em uso não pode ser excluído", s == 400 and "em uso" in r["erro"], r)
onibus = [t for t in req("GET", "/api/transportes")[1] if t["tipo"] == "ONIBUS"][0]
bici = [t for t in req("GET", "/api/transportes")[1] if t["tipo"] == "BICICLETA"][0]

# ---------- CRUD de Rota (RQ03, RN03, RN04) ----------
s, r = req("POST", "/api/rotas", {"origem": "Bairro A", "destino": "Bairro B", "trechos": [{"idTransporte": bici["idTransporte"], "distanciaKm": 4}]}, tok=TP); check("RN03 passageiro não cria rota", s == 403, r)
s, r = req("POST", "/api/rotas", {"origem": "Bairro A", "destino": "Bairro B", "descricao": "x", "trechos": []}, tok=TA); check("RN04 rota sem trecho bloqueada", s == 400 and "trecho" in r["erro"], r)
s, r = req("POST", "/api/rotas", {"origem": "Bairro A", "destino": "Bairro B", "descricao": "Bike direto", "trechos": [{"idTransporte": bici["idTransporte"], "distanciaKm": 4}]}, tok=TA)
check("CREATE rota", s == 201 and r["idRota"] > 0 and len(r["trechos"]) == 1 and r["trechos"][0]["transporte"]["tipo"] == "BICICLETA", r); IDR = r["idRota"]
s, r = req("POST", "/api/rotas", {"origem": "Bairro A", "destino": "Bairro B", "trechos": [{"idTransporte": 9999, "distanciaKm": 4}]}, tok=TA); check("transporte inexistente no trecho", s == 400, r)
s, r = req("GET", f"/api/rotas/{IDR}"); check("READ rota", s == 200 and r["origem"] == "Bairro A", r)
s, r = req("GET", "/api/rotas"); check("READ lista de rotas", s == 200 and len(r) >= 7, len(r))
s, r = req("PUT", f"/api/rotas/{IDR}", {"origem": "Bairro A", "destino": "Bairro C", "descricao": "Ônibus + bike", "trechos": [{"idTransporte": onibus["idTransporte"], "distanciaKm": 8}, {"idTransporte": bici["idTransporte"], "distanciaKm": 2}]}, tok=TA)
check("UPDATE rota (substitui trechos)", s == 200 and r["destino"] == "Bairro C" and len(r["trechos"]) == 2 and r["distanciaKm"] == 10, r)
s, r = req("GET", f"/api/rotas/{IDR}"); check("UPDATE persistiu no banco", r["destino"] == "Bairro C" and [t["ordem"] for t in r["trechos"]] == [1, 2], r)
s, r = req("PUT", "/api/rotas/99999", {"origem": "A1", "destino": "B1", "trechos": [{"idTransporte": bici["idTransporte"], "distanciaKm": 1}]}, tok=TA); check("UPDATE inexistente 404", s == 404, r)

# ---------- consulta, comparação, destaque (RQ05-RQ07, RN05, RN06, RN07) ----------
q = "/api/consulta?" + urllib.parse.urlencode({"origem": "centro", "destino": "zona norte"})
s, alts = req("GET", q); check("RQ05 consulta retorna 3 alternativas", s == 200 and len(alts) == 3, alts)
sus = [a for a in alts if a["maisSustentavel"]]; rap = [a for a in alts if a["maisRapida"]]
check("RQ07 exatamente 1 mais sustentável", len(sus) == 1)
check("RN06 mais sustentável = menor CO2 (ônibus+bike)", sus[0]["rota"]["descricao"] == "Ônibus + bicicleta", sus[0]["rota"]["descricao"])
# conferência numérica manual: carro 12km, vel 30, fator 1.3 -> 12/30*60*1.3 = 31.2 min ; 12*120 = 1440 g
c = [a for a in alts if a["rota"]["descricao"] == "De carro, direto"][0]
check("RN05 carro: tempo 31,2 min e 1440 g CO2", abs(c["tempoMinutos"] - 31.2) < 0.01 and abs(c["impactoGramasCo2"] - 1440) < 0.01, c["tempoMinutos"])
# ônibus 13km, vel 20 -> 39 min + 5 espera = 44 ; 13*70 = 910
o = [a for a in alts if a["rota"]["descricao"] == "De ônibus, direto"][0]
check("RN05 ônibus: 44 min (inclui espera) e 910 g", abs(o["tempoMinutos"] - 44) < 0.01 and abs(o["impactoGramasCo2"] - 910) < 0.01, o["tempoMinutos"])
# ônibus 9km: 27+5=32 ; bike 3km vel 15 -> 12 min ; total 44 ; CO2 = 9*70 = 630
m = sus[0]
check("RN05 soma de trechos: 44 min, 12 km, 630 g", abs(m["tempoMinutos"] - 44) < 0.01 and abs(m["distanciaKm"] - 12) < 0.01 and abs(m["impactoGramasCo2"] - 630) < 0.01, m)
check("mais rápida = carro", rap[0]["rota"]["descricao"] == "De carro, direto")
s, alts1 = req("GET", "/api/consulta?origem=Terminal&destino=Aeroporto"); check("1 alternativa: sem destaque (RQ07)", len(alts1) == 1 and not alts1[0]["maisSustentavel"], alts1)
s, r = req("GET", "/api/consulta?origem=&destino=x"); check("consulta sem origem 400", s == 400, r)
s, r = req("GET", "/api/consulta?origem=Narnia&destino=Mordor"); check("consulta sem resultado = lista vazia", s == 200 and r == [], r)

s, h0 = req("GET", "/api/historico", tok=TP); n0 = len(h0)
s, r = req("GET", q)  # anônimo
s, h1 = req("GET", "/api/historico", tok=TP); check("RN07 consulta anônima NÃO grava histórico", len(h1) == n0)
s, r = req("GET", q, tok=TP)
s, h2 = req("GET", "/api/historico", tok=TP); check("RQ09 consulta logada grava histórico (3 rotas)", len(h2) == n0 + 3, len(h2))
s, r = req("GET", "/api/historico"); check("histórico sem login 401", s == 401, r)

# ---------- favoritos (RQ08, RN08) ----------
rid = sus[0]["rota"]["idRota"]
s, r = req("POST", "/api/favoritos", {"idRota": rid}, tok=TP); check("RQ08 favoritar", s == 201, r)
s, r = req("POST", "/api/favoritos", {"idRota": rid}, tok=TP); check("RN08 favorito repetido bloqueado", s == 400 and "já está" in r["erro"], r)
s, r = req("POST", "/api/favoritos", {"idRota": rid}); check("favoritar sem login 401", s == 401, r)
s, r = req("GET", "/api/favoritos", tok=TP); check("lista favoritos", s == 200 and len(r) == 1 and r[0]["rota"]["idRota"] == rid, r)
s, r = req("DELETE", f"/api/favoritos/{rid}", tok=TP); check("remover favorito", s == 200, r)
s, r = req("DELETE", f"/api/favoritos/{rid}", tok=TP); check("remover favorito inexistente 404", s == 404, r)

# ---------- alertas (RQ10, RN03, RN09) ----------
s, r = req("POST", "/api/alertas", {"idRota": rid, "mensagem": "Obras na via"}, tok=TP); check("RN03 passageiro não cria alerta", s == 403, r)
s, r = req("POST", "/api/alertas", {"idRota": rid, "mensagem": "Obras na via principal"}, tok=TA); check("RQ10 admin cria alerta", s == 201 and r["status"] == "ATIVO", r); IDA = r["idAlerta"]
s, alts = req("GET", q); alt = [a for a in alts if a["rota"]["idRota"] == rid][0]
check("alerta ativo aparece na consulta", len(alt["alertas"]) == 1 and "Obras" in alt["alertas"][0]["mensagem"], alt["alertas"])
s, r = req("PUT", f"/api/alertas/{IDA}", {"status": "INATIVO"}, tok=TA); check("desativar alerta", s == 200, r)
s, alts = req("GET", q); alt = [a for a in alts if a["rota"]["idRota"] == rid][0]
check("RN09 alerta inativo NÃO aparece", alt["alertas"] == [], alt["alertas"])
s, r = req("GET", "/api/alertas", tok=TA); check("admin lista alertas", s == 200 and len(r) >= 1, r)
s, r = req("GET", "/api/alertas", tok=TP); check("passageiro não lista alertas 403", s == 403, r)
s, r = req("PUT", f"/api/alertas/{IDA}", {"status": "XPTO"}, tok=TA); check("status inválido 400", s == 400, r)

# ---------- DELETE rota + cascade ----------
s, r = req("DELETE", f"/api/rotas/{IDR}", tok=TP); check("RN03 passageiro não exclui rota", s == 403, r)
s, r = req("DELETE", f"/api/rotas/{IDR}", tok=TA); check("DELETE rota", s == 200, r)
s, r = req("GET", f"/api/rotas/{IDR}"); check("rota excluída some (404)", s == 404, r)

# ---------- erros ----------
s, r = req("POST", "/api/sessao", None); check("login sem corpo não derruba o servidor", s == 400, r)
s, t = req("POST", "/api/usuarios", raw=True); check("corpo vazio tratado", s == 400)
r0 = urllib.request.Request(B + "/api/usuarios", method="POST", data=b"{quebrado", headers={"Content-Type": "application/json"})
try: urllib.request.urlopen(r0); code = 200
except urllib.error.HTTPError as e: code = e.code
check("JSON quebrado -> 400 (sem stack trace)", code == 400, code)
s, r = req("DELETE", "/api/sessao", tok=TP); check("logout", s == 200, r)
s, r = req("GET", "/api/sessao", tok=TP); check("token inválido após logout 401", s == 401, r)

print(f"\nRESULTADO: {ok} passaram, {fail} falharam")
