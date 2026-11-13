package service;

import dao.UsuarioDAO;
import model.Administrador;
import model.Passageiro;
import model.Usuario;
import util.Configuracao;
import util.SegurancaUtil;

/** Regras de negócio de usuários: cadastro (RQ01, RN01, RN02) e login (RQ02). */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Cadastra um novo usuário.
     *
     * @param perfil       "PASSAGEIRO" ou "ADMINISTRADOR"
     * @param preferencia  só para passageiro: "MAIS_RAPIDO" ou "MAIS_SUSTENTAVEL" (vazio = mais sustentável)
     * @param codigoAdmin  só para administrador: código secreto configurado no sistema
     */
    public Usuario cadastrar(String nome, String email, String senha, String perfil,
                             String preferencia, String codigoAdmin) {
        nome = texto(nome);
        email = texto(email).toLowerCase();
        senha = senha == null ? "" : senha;

        if (nome.length() < 3) {
            throw new RegraNegocioException("Informe o nome completo (mínimo de 3 caracteres).");
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new RegraNegocioException("Informe um e-mail válido.");
        }
        if (senha.length() < 6) {
            throw new RegraNegocioException("A senha deve ter pelo menos 6 caracteres.");
        }
        // RN01: cada e-mail só pode ser usado em um cadastro
        if (usuarioDAO.existeEmail(email)) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        // RN02: guardamos somente o HASH da senha
        String hash = SegurancaUtil.gerarHash(senha);

        Usuario usuario;
        if ("ADMINISTRADOR".equals(perfil)) {
            // Para ninguém virar administrador sozinho, exigimos o código configurado no sistema.
            if (codigoAdmin == null || !codigoAdmin.equals(Configuracao.getCodigoAdmin())) {
                throw new RegraNegocioException("Código de administrador inválido.");
            }
            usuario = new Administrador(0, nome, email, hash, 1);
        } else if ("PASSAGEIRO".equals(perfil)) {
            String pref = texto(preferencia);
            if (pref.isEmpty()) {
                pref = Passageiro.PREF_MAIS_SUSTENTAVEL;
            }
            if (!pref.equals(Passageiro.PREF_MAIS_RAPIDO) && !pref.equals(Passageiro.PREF_MAIS_SUSTENTAVEL)) {
                throw new RegraNegocioException("Preferência inválida. Use MAIS_RAPIDO ou MAIS_SUSTENTAVEL.");
            }
            usuario = new Passageiro(0, nome, email, hash, pref);
        } else {
            throw new RegraNegocioException("Perfil inválido. Escolha passageiro ou administrador.");
        }

        usuarioDAO.inserir(usuario);
        return usuario;
    }

    /** Login (RQ02). A mensagem de erro é a mesma para e-mail ou senha errados (não revela qual foi). */
    public Usuario login(String email, String senha) {
        Usuario usuario = usuarioDAO.buscarPorEmail(texto(email).toLowerCase());
        // autenticar() é POLIMÓRFICO: cada perfil aplica a sua regra
        if (usuario == null || senha == null || !usuario.autenticar(senha)) {
            throw new RegraNegocioException("E-mail ou senha inválidos.");
        }
        return usuario;
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
