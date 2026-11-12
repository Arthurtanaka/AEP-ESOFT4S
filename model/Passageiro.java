package model;

/**
 * HERANÇA: o Passageiro "é um" Usuario. Ele ganha nome, e-mail e senha da classe mãe
 * e acrescenta o que é só dele: a preferência de deslocamento.
 */
public class Passageiro extends Usuario {

    // Valores aceitos para a preferência (guardados como texto no banco)
    public static final String PREF_MAIS_RAPIDO = "MAIS_RAPIDO";
    public static final String PREF_MAIS_SUSTENTAVEL = "MAIS_SUSTENTAVEL";

    private String preferencia;

    public Passageiro(int idUsuario, String nome, String email, String senhaHash, String preferencia) {
        super(idUsuario, nome, email, senhaHash); // chama o construtor da classe mãe
        this.preferencia = preferencia;
    }

    /** POLIMORFISMO: o passageiro entra se a senha estiver correta. */
    @Override
    public boolean autenticar(String senhaDigitada) {
        return senhaConfere(senhaDigitada);
    }

    @Override
    public String getPerfil() {
        return "PASSAGEIRO";
    }

    public String getPreferencia() {
        return preferencia;
    }

    public void setPreferencia(String preferencia) {
        this.preferencia = preferencia;
    }
}
