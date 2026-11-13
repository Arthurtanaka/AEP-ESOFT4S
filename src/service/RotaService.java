package service;

import dao.AlertaDAO;
import dao.HistoricoDAO;
import dao.RotaDAO;
import dao.TransporteDAO;
import java.util.ArrayList;
import java.util.List;
import model.Historico;
import model.Rota;
import model.Trecho;
import model.Transporte;
import model.Usuario;

/**
 * Regras de negócio das rotas:
 *  - CRUD de rotas pelo administrador (RQ03, RN03, RN04);
 *  - consulta de alternativas entre origem e destino (RQ05);
 *  - comparação de tempo, distância e impacto (RQ06, RN05);
 *  - destaque da alternativa mais sustentável (RQ07, RN06);
 *  - exibição dos alertas ativos (RQ10, RN09);
 *  - histórico das consultas de usuários autenticados (RQ09, RN07).
 */
public class RotaService {

    /** Dados de um trecho que chegam da tela: qual transporte e quantos km. */
    public static class TrechoEntrada {
        private final int idTransporte;
        private final double distanciaKm;

        public TrechoEntrada(int idTransporte, double distanciaKm) {
            this.idTransporte = idTransporte;
            this.distanciaKm = distanciaKm;
        }
    }

    private final RotaDAO rotaDAO;
    private final TransporteDAO transporteDAO;
    private final AlertaDAO alertaDAO;
    private final HistoricoDAO historicoDAO;

    public RotaService(RotaDAO rotaDAO, TransporteDAO transporteDAO,
                       AlertaDAO alertaDAO, HistoricoDAO historicoDAO) {
        this.rotaDAO = rotaDAO;
        this.transporteDAO = transporteDAO;
        this.alertaDAO = alertaDAO;
        this.historicoDAO = historicoDAO;
    }

    // ===================== CRUD (somente administrador) =====================

    /** CREATE */
    public Rota cadastrar(Usuario solicitante, String origem, String destino, String descricao,
                          List<TrechoEntrada> trechos) {
        Permissoes.exigirAdministrador(solicitante);
        Rota rota = construirRota(0, origem, destino, descricao, trechos);
        rotaDAO.inserir(rota);
        return rota;
    }

    /** READ (todas as rotas) */
    public List<Rota> listar() {
        return rotaDAO.listar();
    }

    /** READ (uma rota) */
    public Rota buscarPorId(int idRota) {
        Rota rota = rotaDAO.buscarPorId(idRota);
        if (rota == null) {
            throw new RegraNegocioException("Rota não encontrada.", 404);
        }
        return rota;
    }

    /** UPDATE: substitui os dados da rota e todos os seus trechos. */
    public Rota atualizar(Usuario solicitante, int idRota, String origem, String destino,
                          String descricao, List<TrechoEntrada> trechos) {
        Permissoes.exigirAdministrador(solicitante);
        buscarPorId(idRota); // garante que existe
        Rota rota = construirRota(idRota, origem, destino, descricao, trechos);
        rotaDAO.atualizar(rota);
        return rota;
    }

    /** DELETE: os trechos da rota são apagados junto (cascade no banco). */
    public void excluir(Usuario solicitante, int idRota) {
        Permissoes.exigirAdministrador(solicitante);
        buscarPorId(idRota);
        rotaDAO.excluir(idRota);
    }

    // ===================== Consulta e comparação =====================

    /**
     * Consulta as alternativas entre origem e destino, compara e destaca.
     * A consulta é livre (RN07): se o usuário NÃO estiver logado, passe null em "usuario" e nada é gravado
     * no histórico. Se estiver logado, cada alternativa consultada é registrada no histórico.
     */
    public List<AlternativaComparada> consultarAlternativas(Usuario usuario, String origem, String destino) {
        if (origem == null || origem.trim().isEmpty() || destino == null || destino.trim().isEmpty()) {
            throw new RegraNegocioException("Informe a origem e o destino da consulta.");
        }

        List<Rota> rotas = rotaDAO.buscarPorOrigemDestino(origem.trim(), destino.trim());

        List<AlternativaComparada> alternativas = new ArrayList<>();
        for (Rota rota : rotas) {
            // RN09: apenas alertas ATIVOS aparecem para o usuário
            alternativas.add(new AlternativaComparada(rota, alertaDAO.listarAtivosPorRota(rota.getIdRota())));
        }

        destacar(alternativas);

        // RN07: somente usuários autenticados têm histórico
        if (usuario != null) {
            for (AlternativaComparada alt : alternativas) {
                Historico historico = new Historico(0, usuario.getIdUsuario(), alt.getRota());
                historico.registrar(); // marca a data e a hora
                historicoDAO.inserir(historico);
            }
        }
        return alternativas;
    }

    /**
     * RN06: a alternativa mais sustentável é a de MENOR impacto ambiental total;
     * em caso de empate, vence a de menor tempo. O destaque só existe quando há mais de uma alternativa (RQ07).
     * Também marcamos a mais rápida, como informação extra para o usuário.
     */
    private void destacar(List<AlternativaComparada> alternativas) {
        if (alternativas.size() < 2) {
            return;
        }
        AlternativaComparada sustentavel = alternativas.get(0);
        AlternativaComparada rapida = alternativas.get(0);

        for (AlternativaComparada alt : alternativas) {
            boolean menorImpacto = alt.getImpactoGramasCo2() < sustentavel.getImpactoGramasCo2();
            boolean empateImpactoMenorTempo = alt.getImpactoGramasCo2() == sustentavel.getImpactoGramasCo2()
                    && alt.getTempoMinutos() < sustentavel.getTempoMinutos();
            if (menorImpacto || empateImpactoMenorTempo) {
                sustentavel = alt;
            }
            if (alt.getTempoMinutos() < rapida.getTempoMinutos()) {
                rapida = alt;
            }
        }
        sustentavel.setMaisSustentavel(true);
        rapida.setMaisRapida(true);
    }

    // ===================== Validação e montagem =====================

    /** Valida os dados e monta o objeto Rota com seus Trechos numerados em ordem (1, 2, 3...). */
    private Rota construirRota(int idRota, String origem, String destino, String descricao,
                               List<TrechoEntrada> entradas) {
        if (origem == null || origem.trim().length() < 2) {
            throw new RegraNegocioException("Informe a origem da rota.");
        }
        if (destino == null || destino.trim().length() < 2) {
            throw new RegraNegocioException("Informe o destino da rota.");
        }
        // RN04: toda rota precisa ter pelo menos um trecho
        if (entradas == null || entradas.isEmpty()) {
            throw new RegraNegocioException("A rota precisa ter pelo menos um trecho.");
        }

        String desc = descricao == null ? "" : descricao.trim();
        Rota rota = new Rota(idRota, origem.trim(), destino.trim(), desc.isEmpty() ? null : desc);

        int ordem = 1;
        for (TrechoEntrada entrada : entradas) {
            if (entrada.distanciaKm <= 0 || entrada.distanciaKm > 5000) {
                throw new RegraNegocioException("A distância de cada trecho deve ser maior que 0 km.");
            }
            Transporte transporte = transporteDAO.buscarPorId(entrada.idTransporte);
            if (transporte == null) {
                throw new RegraNegocioException("Transporte " + entrada.idTransporte + " não encontrado.");
            }
            rota.adicionarTrecho(new Trecho(0, ordem++, entrada.distanciaKm, transporte));
        }
        return rota;
    }
}
