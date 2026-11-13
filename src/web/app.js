/* MoveCity - interface web (HTML5 + CSS3 + JavaScript puro).
 * Conversa com o back-end Java por HTTP, trocando dados em JSON (veja os controllers em src/controller).
 * Por segurança, todo texto vindo do servidor é inserido com textContent (nunca innerHTML),
 * o que impede ataques de injeção de código (XSS). */

"use strict";

// ===================== Estado da sessão =====================
let token = sessionStorage.getItem("token");
let usuario = JSON.parse(sessionStorage.getItem("usuario") || "null");
let transportesCache = [];

// ===================== Funções de apoio =====================

/** Atalho para pegar um elemento pelo id. */
const $ = (id) => document.getElementById(id);

/** Cria um elemento HTML: el("p", {class: "x"}, "texto", outroElemento). */
function el(tag, atributos, ...filhos) {
  const e = document.createElement(tag);
  for (const [chave, valor] of Object.entries(atributos || {})) {
    if (valor === false || valor === null || valor === undefined) continue;
    if (chave === "onclick") e.addEventListener("click", valor);
    else e.setAttribute(chave, valor === true ? "" : valor);
  }
  for (const filho of filhos.flat()) {
    if (filho === null || filho === undefined || filho === false) continue;
    e.append(filho.nodeType ? filho : document.createTextNode(String(filho)));
  }
  return e;
}

const nf = (casas) => new Intl.NumberFormat("pt-BR", { minimumFractionDigits: casas, maximumFractionDigits: casas });
const fmtKm = (v) => nf(2).format(v) + " km";
const fmtCo2 = (g) => (g >= 1000 ? nf(2).format(g / 1000) + " kg CO₂" : nf(0).format(g) + " g CO₂");
function fmtTempo(min) {
  const total = Math.round(min);
  const h = Math.floor(total / 60);
  const m = total % 60;
  return h === 0 ? m + " min" : h + "h " + String(m).padStart(2, "0") + "min";
}

/** Mostra uma mensagem de sucesso ("ok") ou de erro ("erro") no topo da página. */
let temporizadorAviso = null;
function avisar(mensagem, tipo) {
  const caixa = $("aviso");
  caixa.textContent = mensagem;
  caixa.className = "aviso " + (tipo || "ok");
  caixa.hidden = false;
  clearTimeout(temporizadorAviso);
  temporizadorAviso = setTimeout(() => { caixa.hidden = true; }, 6000);
}

/** Chama a API. Se o servidor responder com erro, lança um Error com a mensagem para o usuário. */
async function api(metodo, caminho, corpo) {
  const cabecalhos = { "Content-Type": "application/json" };
  if (token) cabecalhos["Authorization"] = "Bearer " + token;
  let resposta;
  try {
    resposta = await fetch("/api" + caminho, {
      method: metodo,
      headers: cabecalhos,
      body: corpo ? JSON.stringify(corpo) : undefined,
    });
  } catch (e) {
    throw new Error("Não foi possível falar com o servidor. Ele está em execução?");
  }
  const dados = await resposta.json().catch(() => ({}));
  if (!resposta.ok) {
    if (resposta.status === 401 && token) sair(false); // sessão expirou
    throw new Error(dados.erro || "Erro inesperado (código " + resposta.status + ").");
  }
  return dados;
}

/** Executa uma ação e mostra o erro (se houver) em vez de quebrar a página. */
async function tentar(acao) {
  try {
    await acao();
  } catch (e) {
    avisar(e.message, "erro");
  }
}

// ===================== Navegação =====================

function montarMenu() {
  const menu = $("menu");
  menu.replaceChildren();
  const botao = (texto, tela, extra) => el("button", { class: "secundario pequeno", onclick: () => abrirTela(tela) }, texto);

  menu.append(botao("Consultar", "consulta"));
  if (usuario) {
    menu.append(botao("Favoritos", "favoritos"), botao("Histórico", "historico"));
    if (usuario.perfil === "ADMINISTRADOR") menu.append(botao("Administração", "admin"));
    menu.append(el("span", { class: "usuario" }, usuario.nome + " (" + usuario.perfil.toLowerCase() + ")"));
    menu.append(el("button", { class: "pequeno", onclick: () => sair(true) }, "Sair"));
  } else {
    menu.append(botao("Entrar", "entrar"), el("button", { class: "primario pequeno", onclick: () => abrirTela("cadastro") }, "Criar conta"));
  }
}

const TELAS = ["consulta", "entrar", "cadastro", "favoritos", "historico", "admin"];

function abrirTela(nome) {
  TELAS.forEach((t) => { $("tela-" + t).hidden = (t !== nome); });
  $("aviso").hidden = true;
  if (nome === "favoritos") tentar(carregarFavoritos);
  if (nome === "historico") tentar(carregarHistorico);
  if (nome === "admin") tentar(abrirAdmin);
}

function sair(chamarServidor) {
  if (chamarServidor && token) api("DELETE", "/sessao").catch(() => {});
  token = null;
  usuario = null;
  sessionStorage.removeItem("token");
  sessionStorage.removeItem("usuario");
  montarMenu();
  abrirTela("consulta");
  $("resultado-consulta").replaceChildren();
}

// ===================== Login e cadastro =====================

$("form-login").addEventListener("submit", (ev) => {
  ev.preventDefault();
  tentar(async () => {
    const r = await api("POST", "/sessao", { email: $("login-email").value, senha: $("login-senha").value });
    token = r.token;
    usuario = r.usuario;
    sessionStorage.setItem("token", token);
    sessionStorage.setItem("usuario", JSON.stringify(usuario));
    $("form-login").reset();
    montarMenu();
    abrirTela("consulta");
    avisar("Bem-vindo(a), " + usuario.nome + "!", "ok");
  });
});

$("cad-perfil").addEventListener("change", () => {
  const admin = $("cad-perfil").value === "ADMINISTRADOR";
  $("cad-codigo-box").hidden = !admin;
  $("cad-pref-box").hidden = admin;
});

$("form-cadastro").addEventListener("submit", (ev) => {
  ev.preventDefault();
  tentar(async () => {
    await api("POST", "/usuarios", {
      nome: $("cad-nome").value,
      email: $("cad-email").value,
      senha: $("cad-senha").value,
      perfil: $("cad-perfil").value,
      preferencia: $("cad-pref").value,
      codigoAdmin: $("cad-codigo").value,
    });
    $("form-cadastro").reset();
    $("cad-codigo-box").hidden = true;
    $("cad-pref-box").hidden = false;
    abrirTela("entrar");
    avisar("Cadastro realizado! Agora é só entrar.", "ok");
  });
});

// ===================== Consulta e comparação =====================

/** Desenha o cartão de uma rota (usado na consulta, nos favoritos e no histórico). */
function cartaoRota(rota, opcoes) {
  const o = opcoes || {};
  const titulo = rota.descricao ? rota.descricao : "Rota #" + rota.idRota;
  const cartao = el("article", { class: "cartao alternativa" + (o.maisSustentavel ? " destaque" : "") });

  cartao.append(el("h3", {}, titulo,
    o.maisSustentavel ? el("span", { class: "etiqueta verde" }, "Mais sustentável") : null,
    o.maisRapida ? el("span", { class: "etiqueta azul" }, "Mais rápida") : null));
  cartao.append(el("div", { class: "meta" }, rota.origem + " → " + rota.destino + " · rota #" + rota.idRota));

  const lista = el("ul", { class: "trechos" });
  rota.trechos.forEach((t) => {
    lista.append(el("li", {}, t.ordem + ") " + t.transporte.nome + " — " + fmtKm(t.distanciaKm)
      + " · " + fmtTempo(t.tempoMinutos) + " · " + fmtCo2(t.impactoGramasCo2)));
  });
  cartao.append(lista);

  cartao.append(el("div", { class: "totais" },
    el("div", {}, el("small", {}, "Tempo estimado"), el("strong", {}, fmtTempo(rota.tempoMinutos))),
    el("div", {}, el("small", {}, "Distância total"), el("strong", {}, fmtKm(rota.distanciaKm))),
    el("div", {}, el("small", {}, "Impacto ambiental"), el("strong", {}, fmtCo2(rota.impactoGramasCo2)))));

  (o.alertas || []).forEach((a) => cartao.append(el("div", { class: "alerta-aviso" }, "⚠ " + a.mensagem + " (" + a.dataAlerta + ")")));
  if (o.extra) cartao.append(o.extra);
  return cartao;
}

$("form-consulta").addEventListener("submit", (ev) => {
  ev.preventDefault();
  tentar(async () => {
    const origem = encodeURIComponent($("consulta-origem").value);
    const destino = encodeURIComponent($("consulta-destino").value);
    const alternativas = await api("GET", "/consulta?origem=" + origem + "&destino=" + destino);
    const area = $("resultado-consulta");
    area.replaceChildren();

    if (alternativas.length === 0) {
      area.append(el("p", { class: "vazio" }, "Nenhuma rota cadastrada para esse trajeto. Tente outros nomes de origem e destino."));
      return;
    }
    area.append(el("p", { class: "meta" }, alternativas.length + " alternativa(s) encontrada(s)."
      + (usuario ? " Esta consulta foi registrada no seu histórico." : " Entre para salvar favoritos e histórico.")));

    alternativas.forEach((alt) => {
      let extra = null;
      if (usuario) {
        extra = el("button", { class: "secundario pequeno", onclick: () => tentar(async () => {
          await api("POST", "/favoritos", { idRota: alt.rota.idRota });
          avisar("Rota salva nos favoritos.", "ok");
        }) }, "☆ Salvar como favorita");
      }
      area.append(cartaoRota(alt.rota, { maisSustentavel: alt.maisSustentavel, maisRapida: alt.maisRapida, alertas: alt.alertas, extra }));
    });
  });
});

// ===================== Favoritos e histórico =====================

async function carregarFavoritos() {
  const favoritos = await api("GET", "/favoritos");
  const area = $("lista-favoritos");
  area.replaceChildren();
  if (favoritos.length === 0) {
    area.append(el("p", { class: "vazio" }, "Você ainda não salvou nenhuma rota. Faça uma consulta e use “Salvar como favorita”."));
    return;
  }
  favoritos.forEach((f) => {
    const remover = el("button", { class: "perigo pequeno", onclick: () => tentar(async () => {
      await api("DELETE", "/favoritos/" + f.rota.idRota);
      avisar("Favorito removido.", "ok");
      await carregarFavoritos();
    }) }, "Remover dos favoritos");
    area.append(cartaoRota(f.rota, { extra: el("div", { class: "acoes" }, el("span", { class: "meta" }, "Salva em " + f.criadoEm), remover) }));
  });
}

async function carregarHistorico() {
  const itens = await api("GET", "/historico");
  const area = $("lista-historico");
  area.replaceChildren();
  if (itens.length === 0) {
    area.append(el("p", { class: "vazio" }, "Seu histórico está vazio. As consultas feitas com você logado aparecem aqui."));
    return;
  }
  const corpo = el("tbody");
  itens.forEach((h) => corpo.append(el("tr", {},
    el("td", {}, h.dataConsulta),
    el("td", {}, h.rota.origem + " → " + h.rota.destino),
    el("td", {}, h.rota.descricao || "Rota #" + h.rota.idRota),
    el("td", {}, fmtTempo(h.rota.tempoMinutos)),
    el("td", {}, fmtCo2(h.rota.impactoGramasCo2)))));
  area.append(el("div", { class: "cartao tabela-rolavel" }, el("table", {},
    el("thead", {}, el("tr", {}, el("th", {}, "Data"), el("th", {}, "Trajeto"), el("th", {}, "Alternativa"), el("th", {}, "Tempo"), el("th", {}, "Impacto"))),
    corpo)));
}

// ===================== Administração =====================

document.querySelectorAll(".aba").forEach((botao) => botao.addEventListener("click", () => {
  document.querySelectorAll(".aba").forEach((b) => b.classList.toggle("ativa", b === botao));
  ["rotas", "transportes", "alertas"].forEach((nome) => { $("aba-" + nome).hidden = (nome !== botao.dataset.aba); });
  tentar(abrirAdmin);
}));

async function abrirAdmin() {
  transportesCache = await api("GET", "/transportes");
  const aba = document.querySelector(".aba.ativa").dataset.aba;
  if (aba === "rotas") { prepararFormRota(); await listarRotas(); }
  if (aba === "transportes") { prepararFormTransporte(); listarTransportes(); }
  if (aba === "alertas") await carregarAlertas();
}

// ---------- Rotas (CRUD da entidade principal) ----------

function linhaTrecho(idTransporte, km) {
  const select = el("select", { required: true, "aria-label": "Transporte do trecho" });
  transportesCache.forEach((t) => select.append(el("option", { value: t.idTransporte }, t.nome + " (" + t.tipo.toLowerCase() + ")")));
  if (idTransporte) select.value = idTransporte;
  const distancia = el("input", { type: "number", step: "0.01", min: "0.01", required: true, placeholder: "km", "aria-label": "Distância em km", value: km || "" });
  const linha = el("div", { class: "linha-trecho" }, select, distancia,
    el("button", { type: "button", class: "perigo pequeno", onclick: () => linha.remove() }, "Remover"));
  return linha;
}

function prepararFormRota(rota) {
  $("form-rota").reset();
  $("rota-id").value = rota ? rota.idRota : "";
  $("titulo-form-rota").textContent = rota ? "Editar rota #" + rota.idRota : "Nova rota";
  $("btn-cancelar-rota").hidden = !rota;
  $("rota-trechos").replaceChildren();
  if (rota) {
    $("rota-origem").value = rota.origem;
    $("rota-destino").value = rota.destino;
    $("rota-descricao").value = rota.descricao;
    rota.trechos.forEach((t) => $("rota-trechos").append(linhaTrecho(t.transporte.idTransporte, t.distanciaKm)));
  } else {
    $("rota-trechos").append(linhaTrecho());
  }
}

$("btn-add-trecho").addEventListener("click", () => $("rota-trechos").append(linhaTrecho()));
$("btn-cancelar-rota").addEventListener("click", () => prepararFormRota());

$("form-rota").addEventListener("submit", (ev) => {
  ev.preventDefault();
  tentar(async () => {
    const trechos = [...$("rota-trechos").children].map((linha) => ({
      idTransporte: Number(linha.querySelector("select").value),
      distanciaKm: Number(linha.querySelector("input").value),
    }));
    const corpo = { origem: $("rota-origem").value, destino: $("rota-destino").value, descricao: $("rota-descricao").value, trechos };
    const id = $("rota-id").value;
    if (id) await api("PUT", "/rotas/" + id, corpo);
    else await api("POST", "/rotas", corpo);
    avisar(id ? "Rota atualizada." : "Rota cadastrada.", "ok");
    prepararFormRota();
    await listarRotas();
  });
});

async function listarRotas() {
  const rotas = await api("GET", "/rotas");
  const area = $("lista-rotas");
  area.replaceChildren();
  if (rotas.length === 0) { area.append(el("p", { class: "vazio" }, "Nenhuma rota cadastrada.")); return; }
  const corpo = el("tbody");
  rotas.forEach((r) => corpo.append(el("tr", {},
    el("td", {}, r.idRota),
    el("td", {}, r.origem + " → " + r.destino, el("div", { class: "meta" }, r.descricao)),
    el("td", {}, r.trechos.map((t) => t.transporte.nome + " " + fmtKm(t.distanciaKm)).join(" + ")),
    el("td", {}, fmtTempo(r.tempoMinutos) + " · " + fmtCo2(r.impactoGramasCo2)),
    el("td", { class: "acoes" },
      el("button", { class: "secundario pequeno", onclick: () => { prepararFormRota(r); window.scrollTo({ top: 0, behavior: "smooth" }); } }, "Editar"),
      el("button", { class: "perigo pequeno", onclick: () => tentar(async () => {
        if (!confirm("Excluir a rota #" + r.idRota + " e todos os seus trechos?")) return;
        await api("DELETE", "/rotas/" + r.idRota);
        avisar("Rota excluída.", "ok");
        await listarRotas();
      }) }, "Excluir")))));
  area.append(el("div", { class: "cartao tabela-rolavel" }, el("table", {},
    el("thead", {}, el("tr", {}, el("th", {}, "ID"), el("th", {}, "Trajeto"), el("th", {}, "Trechos"), el("th", {}, "Totais"), el("th", {}, "Ações"))),
    corpo)));
}

// ---------- Transportes ----------

function ajustarCamposTransporte() {
  const tipo = $("transp-tipo").value;
  $("transp-fator-box").hidden = tipo !== "CARRO";
  $("transp-cap-box").hidden = tipo !== "ONIBUS";
  $("transp-emissao-box").hidden = tipo === "BICICLETA";
}
$("transp-tipo").addEventListener("change", ajustarCamposTransporte);

function prepararFormTransporte(t) {
  $("form-transporte").reset();
  $("transp-id").value = t ? t.idTransporte : "";
  $("titulo-form-transporte").textContent = t ? "Editar transporte #" + t.idTransporte : "Novo meio de transporte";
  $("btn-cancelar-transp").hidden = !t;
  $("transp-tipo").disabled = !!t; // o tipo não muda depois de criado
  if (t) {
    $("transp-tipo").value = t.tipo;
    $("transp-nome").value = t.nome;
    $("transp-vel").value = t.velocidadeMediaKmH;
    $("transp-emissao").value = t.emissaoCo2GKm;
    if (t.tipo === "CARRO") $("transp-fator").value = t.fatorCongestionamento;
    if (t.tipo === "ONIBUS") $("transp-cap").value = t.capacidadePassageiros;
  }
  ajustarCamposTransporte();
}
$("btn-cancelar-transp").addEventListener("click", () => prepararFormTransporte());

$("form-transporte").addEventListener("submit", (ev) => {
  ev.preventDefault();
  tentar(async () => {
    const tipo = $("transp-tipo").value;
    const corpo = {
      tipo,
      nome: $("transp-nome").value,
      velocidadeMediaKmH: Number($("transp-vel").value),
      emissaoCo2GKm: tipo === "BICICLETA" ? 0 : Number($("transp-emissao").value || 0),
      fatorCongestionamento: tipo === "CARRO" ? Number($("transp-fator").value) : null,
      capacidadePassageiros: tipo === "ONIBUS" ? Number($("transp-cap").value) : null,
    };
    const id = $("transp-id").value;
    if (id) await api("PUT", "/transportes/" + id, corpo);
    else await api("POST", "/transportes", corpo);
    avisar(id ? "Transporte atualizado." : "Transporte cadastrado.", "ok");
    transportesCache = await api("GET", "/transportes");
    prepararFormTransporte();
    listarTransportes();
  });
});

function listarTransportes() {
  const area = $("lista-transportes");
  area.replaceChildren();
  const corpo = el("tbody");
  transportesCache.forEach((t) => corpo.append(el("tr", {},
    el("td", {}, t.idTransporte),
    el("td", {}, t.nome),
    el("td", {}, t.tipo.toLowerCase()),
    el("td", {}, nf(1).format(t.velocidadeMediaKmH) + " km/h"),
    el("td", {}, nf(1).format(t.emissaoCo2GKm) + " g/km"),
    el("td", { class: "acoes" },
      el("button", { class: "secundario pequeno", onclick: () => { prepararFormTransporte(t); window.scrollTo({ top: 0, behavior: "smooth" }); } }, "Editar"),
      el("button", { class: "perigo pequeno", onclick: () => tentar(async () => {
        if (!confirm("Excluir o transporte “" + t.nome + "”?")) return;
        await api("DELETE", "/transportes/" + t.idTransporte);
        avisar("Transporte excluído.", "ok");
        transportesCache = await api("GET", "/transportes");
        listarTransportes();
      }) }, "Excluir")))));
  area.append(el("div", { class: "cartao tabela-rolavel" }, el("table", {},
    el("thead", {}, el("tr", {}, el("th", {}, "ID"), el("th", {}, "Nome"), el("th", {}, "Tipo"), el("th", {}, "Velocidade"), el("th", {}, "Emissão"), el("th", {}, "Ações"))),
    corpo)));
}

// ---------- Alertas ----------

async function carregarAlertas() {
  const [rotas, alertas] = await Promise.all([api("GET", "/rotas"), api("GET", "/alertas")]);
  const select = $("alerta-rota");
  select.replaceChildren();
  rotas.forEach((r) => select.append(el("option", { value: r.idRota }, "#" + r.idRota + " — " + r.origem + " → " + r.destino + (r.descricao ? " (" + r.descricao + ")" : ""))));

  const area = $("lista-alertas");
  area.replaceChildren();
  if (alertas.length === 0) { area.append(el("p", { class: "vazio" }, "Nenhum alerta registrado.")); return; }
  const corpo = el("tbody");
  alertas.forEach((a) => corpo.append(el("tr", {},
    el("td", {}, a.dataAlerta),
    el("td", {}, a.origem + " → " + a.destino),
    el("td", {}, a.mensagem),
    el("td", {}, a.status === "ATIVO" ? "Ativo" : "Inativo"),
    el("td", {}, el("button", { class: "secundario pequeno", onclick: () => tentar(async () => {
      await api("PUT", "/alertas/" + a.idAlerta, { status: a.status === "ATIVO" ? "INATIVO" : "ATIVO" });
      await carregarAlertas();
    }) }, a.status === "ATIVO" ? "Desativar" : "Ativar")))));
  area.append(el("div", { class: "cartao tabela-rolavel" }, el("table", {},
    el("thead", {}, el("tr", {}, el("th", {}, "Data"), el("th", {}, "Rota"), el("th", {}, "Mensagem"), el("th", {}, "Status"), el("th", {}, "Ação"))),
    corpo)));
}

$("form-alerta").addEventListener("submit", (ev) => {
  ev.preventDefault();
  tentar(async () => {
    await api("POST", "/alertas", { idRota: Number($("alerta-rota").value), mensagem: $("alerta-mensagem").value });
    $("alerta-mensagem").value = "";
    avisar("Alerta registrado.", "ok");
    await carregarAlertas();
  });
});

// ===================== Início =====================
montarMenu();
abrirTela("consulta");
if (token) { // confere se a sessão guardada ainda vale no servidor
  api("GET", "/sessao").catch(() => sair(false));
}
