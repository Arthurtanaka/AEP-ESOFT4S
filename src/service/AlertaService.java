package service;

import dao.AlertaDAO;
import dao.RotaDAO;
import java.time.LocalDateTime;
import java.util.List;
import model.Alerta;
import model.Rota;
import model.Usuario;

/** Regras de negócio dos alertas (RQ10, RN03, RN09): somente administradores registram e alteram. */
public class AlertaService {

    private final AlertaDAO alertaDAO;
    private final RotaDAO rotaDAO;

    public AlertaService(AlertaDAO alertaDAO, RotaDAO rotaDAO) {
        this.alertaDAO = alertaDAO;
        this.rotaDAO = rotaDAO;
    }

    public Alerta registrar(Usuario solicitante, int idRota, String mensagem) {
        Permissoes.exigirAdministrador(solicitante);
        Rota rota = rotaDAO.buscarPorId(idRota);
        if (rota == null) {
            throw new RegraNegocioException("Rota não encontrada.", 404);
        }
        if (mensagem == null || mensagem.trim().length() < 3 || mensagem.trim().length() > 255) {
            throw new RegraNegocioException("A mensagem do alerta deve ter entre 3 e 255 caracteres.");
        }
        Alerta alerta = new Alerta(0, rota, solicitante.getIdUsuario(), mensagem.trim(),
                LocalDateTime.now(), Alerta.STATUS_ATIVO);
        alertaDAO.inserir(alerta);
        return alerta;
    }

    public List<Alerta> listarTodos(Usuario solicitante) {
        Permissoes.exigirAdministrador(solicitante);
        return alertaDAO.listarTodos();
    }

    public void alterarStatus(Usuario solicitante, int idAlerta, String novoStatus) {
        Permissoes.exigirAdministrador(solicitante);
        if (!Alerta.STATUS_ATIVO.equals(novoStatus) && !Alerta.STATUS_INATIVO.equals(novoStatus)) {
            throw new RegraNegocioException("Status inválido. Use ATIVO ou INATIVO.");
        }
        if (!alertaDAO.alterarStatus(idAlerta, novoStatus)) {
            throw new RegraNegocioException("Alerta não encontrado.", 404);
        }
    }
}
