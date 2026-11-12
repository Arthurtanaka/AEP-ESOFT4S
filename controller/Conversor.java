package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Administrador;
import model.Alerta;
import model.Bicicleta;
import model.Carro;
import model.Favorito;
import model.Historico;
import model.Onibus;
import model.Passageiro;
import model.Rota;
import model.Transporte;
import model.Trecho;
import model.Usuario;
import service.AlternativaComparada;
import util.Formatador;

/**
 * Transforma os objetos do sistema em Map/List, que a classe Json escreve como texto JSON para o navegador.
 * Nunca enviamos o hash da senha ao navegador.
 */
public class Conversor {

    private Conversor() {
    }

    public static Map<String, Object> usuario(Usuario u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idUsuario", u.getIdUsuario());
        m.put("nome", u.getNome());
        m.put("email", u.getEmail());
        m.put("perfil", u.getPerfil());
        if (u instanceof Passageiro) {
            m.put("preferencia", ((Passageiro) u).getPreferencia());
        }
        if (u instanceof Administrador) {
            m.put("nivelAcesso", ((Administrador) u).getNivelAcesso());
        }
        return m;
    }

    public static String tipoDe(Transporte t) {
        if (t instanceof Carro) {
            return "CARRO";
        }
        if (t instanceof Onibus) {
            return "ONIBUS";
        }
        if (t instanceof Bicicleta) {
            return "BICICLETA";
        }
        return "OUTRO";
    }

    public static Map<String, Object> transporte(Transporte t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idTransporte", t.getIdTransporte());
        m.put("nome", t.getNome());
        m.put("tipo", tipoDe(t));
        m.put("velocidadeMediaKmH", t.getVelocidadeMediaKmH());
        m.put("emissaoCo2GKm", t.getEmissaoCo2GKm());
        if (t instanceof Carro) {
            m.put("fatorCongestionamento", ((Carro) t).getFatorCongestionamento());
        }
        if (t instanceof Onibus) {
            m.put("capacidadePassageiros", ((Onibus) t).getCapacidadePassageiros());
        }
        return m;
    }

    public static Map<String, Object> rota(Rota r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idRota", r.getIdRota());
        m.put("origem", r.getOrigem());
        m.put("destino", r.getDestino());
        m.put("descricao", r.getDescricao() == null ? "" : r.getDescricao());
        m.put("distanciaKm", r.calcularDistancia());
        m.put("tempoMinutos", r.calcularTempo());
        m.put("impactoGramasCo2", r.calcularImpacto());

        List<Object> trechos = new ArrayList<>();
        for (Trecho t : r.getTrechos()) {
            Map<String, Object> tm = new LinkedHashMap<>();
            tm.put("idTrecho", t.getIdTrecho());
            tm.put("ordem", t.getOrdem());
            tm.put("distanciaKm", t.getDistanciaKm());
            tm.put("tempoMinutos", t.calcularTempo());
            tm.put("impactoGramasCo2", t.calcularImpacto());
            tm.put("transporte", transporte(t.getTransporte()));
            trechos.add(tm);
        }
        m.put("trechos", trechos);
        return m;
    }

    public static Map<String, Object> alerta(Alerta a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idAlerta", a.getIdAlerta());
        m.put("idRota", a.getRota().getIdRota());
        m.put("origem", a.getRota().getOrigem());
        m.put("destino", a.getRota().getDestino());
        m.put("mensagem", a.getMensagem());
        m.put("dataAlerta", Formatador.dataHora(a.getDataAlerta()));
        m.put("status", a.getStatus());
        return m;
    }

    public static Map<String, Object> alternativa(AlternativaComparada alt) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("rota", rota(alt.getRota()));
        m.put("tempoMinutos", alt.getTempoMinutos());
        m.put("distanciaKm", alt.getDistanciaKm());
        m.put("impactoGramasCo2", alt.getImpactoGramasCo2());
        m.put("maisSustentavel", alt.isMaisSustentavel());
        m.put("maisRapida", alt.isMaisRapida());
        List<Object> alertas = new ArrayList<>();
        for (Alerta a : alt.getAlertasAtivos()) {
            alertas.add(alerta(a));
        }
        m.put("alertas", alertas);
        return m;
    }

    public static Map<String, Object> favorito(Favorito f) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idFavorito", f.getIdFavorito());
        m.put("criadoEm", Formatador.dataHora(f.getCriadoEm()));
        m.put("rota", rota(f.getRota()));
        return m;
    }

    public static Map<String, Object> historico(Historico h) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idHistorico", h.getIdHistorico());
        m.put("dataConsulta", Formatador.dataHora(h.getDataConsulta()));
        m.put("rota", rota(h.getRota()));
        return m;
    }
}
