package model;

/**
 * HERANÇA: o Administrador "é um" Usuario, com um nível de acesso a mais.
 * É quem pode cadastrar rotas, meios de transporte e alertas (RN03).
 */
public class Administrador extends Usuario {

    private int nivelAcesso; // 1 = acesso administrativo padrão

    public Administrador(int idUsuario, String nome, String email, String senhaHash, int nivelAcesso) {
        super(idUsuario, nome, email, senhaHash);
        this.nivelAcesso = nivelAcesso;
    }

    /**
     * POLIMORFISMO: a regra do administrador é DIFERENTE da do passageiro.
     * Além de a senha estar correta, ele precisa ter nível de acesso maior que zero.
     */
    @Override
    public boolean autenticar(String senhaDigitada) {
        return nivelAcesso > 0 && senhaConfere(senhaDigitada);
    }

    @Override
    public String getPerfil() {
        return "ADMINISTRADOR";
    }

    public int getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(int nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }
}
