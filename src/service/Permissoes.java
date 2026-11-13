package service;

import model.Administrador;
import model.Usuario;

/**
 * Verificações de acesso. Aplica a regra RN03: somente administradores podem cadastrar, alterar ou excluir
 * rotas, meios de transporte e alertas. Mesmo que alguém "force" uma chamada, o Service barra aqui.
 */
public class Permissoes {

    private Permissoes() {
    }

    public static void exigirAutenticado(Usuario usuario) {
        if (usuario == null) {
            throw new RegraNegocioException("Você precisa entrar no sistema para usar esta função.", 401);
        }
    }

    public static void exigirAdministrador(Usuario usuario) {
        exigirAutenticado(usuario);
        if (!(usuario instanceof Administrador)) {
            throw new RegraNegocioException("Apenas administradores podem executar esta ação.", 403);
        }
    }
}
