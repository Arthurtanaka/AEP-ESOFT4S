package service;

import dao.HistoricoDAO;
import java.util.List;
import model.Historico;
import model.Usuario;

/** Consulta do histórico de rotas do usuário autenticado (RQ09). */
public class HistoricoService {

    private final HistoricoDAO historicoDAO;

    public HistoricoService(HistoricoDAO historicoDAO) {
        this.historicoDAO = historicoDAO;
    }

    public List<Historico> listar(Usuario usuario) {
        Permissoes.exigirAutenticado(usuario);
        return historicoDAO.listarPorUsuario(usuario.getIdUsuario());
    }
}
