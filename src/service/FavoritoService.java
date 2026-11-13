package service;

import dao.FavoritoDAO;
import dao.RotaDAO;
import java.util.List;
import model.Favorito;
import model.Rota;
import model.Usuario;

/** Regras de negócio dos favoritos (RQ08, RN08): somente usuário autenticado, sem repetir a mesma rota. */
public class FavoritoService {

    private final FavoritoDAO favoritoDAO;
    private final RotaDAO rotaDAO;

    public FavoritoService(FavoritoDAO favoritoDAO, RotaDAO rotaDAO) {
        this.favoritoDAO = favoritoDAO;
        this.rotaDAO = rotaDAO;
    }

    public Favorito favoritar(Usuario usuario, int idRota) {
        Permissoes.exigirAutenticado(usuario);
        Rota rota = rotaDAO.buscarPorId(idRota);
        if (rota == null) {
            throw new RegraNegocioException("Rota não encontrada.", 404);
        }
        // RN08: o mesmo usuário não pode favoritar a mesma rota duas vezes
        if (favoritoDAO.existe(usuario.getIdUsuario(), idRota)) {
            throw new RegraNegocioException("Esta rota já está nos seus favoritos.");
        }
        Favorito favorito = new Favorito(0, usuario.getIdUsuario(), rota);
        favorito.salvar(); // marca a data e a hora
        favoritoDAO.inserir(favorito);
        return favorito;
    }

    public List<Favorito> listar(Usuario usuario) {
        Permissoes.exigirAutenticado(usuario);
        return favoritoDAO.listarPorUsuario(usuario.getIdUsuario());
    }

    public void remover(Usuario usuario, int idRota) {
        Permissoes.exigirAutenticado(usuario);
        if (!favoritoDAO.remover(usuario.getIdUsuario(), idRota)) {
            throw new RegraNegocioException("Esta rota não está nos seus favoritos.", 404);
        }
    }
}
