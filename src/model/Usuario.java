package model;

import util.SegurancaUtil;

/**
 * CLASSE ABSTRATA: representa um usuário genérico do sistema.
 *
 * Como é abstrata, NÃO dá para fazer "new Usuario(...)". Só dá para criar um Passageiro
 * ou um Administrador, que HERDAM (extends) tudo o que está aqui.
 *
 * Os métodos abstratos (sem corpo) obrigam cada subclasse a escrever a sua própria versão
 * usando @Override. Isso é o POLIMORFISMO: o mesmo método se comporta de forma diferente
 * conforme o tipo real do objeto.
 */
public abstract class Usuario {

    // ENCAPSULAMENTO: atributos private, acessados só por getters e setters.
    private int idUsuario;
    private String nome;
    private String email;
    private String senhaHash; // guardamos só o hash da senha, nunca a senha (RN02)

    public Usuario(int idUsuario, String nome, String email, String senhaHash) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
    }

    /** Confere se a senha digitada permite entrar no sistema. Cada perfil tem sua regra. */
    public abstract boolean autenticar(String senhaDigitada);

    /** Devolve o nome do perfil: "PASSAGEIRO" ou "ADMINISTRADOR" (igual à coluna tipo_usuario). */
    public abstract String getPerfil();

    /** Método comum às subclasses: compara o hash da senha digitada com o hash guardado. */
    protected boolean senhaConfere(String senhaDigitada) {
        return SegurancaUtil.gerarHash(senhaDigitada).equals(senhaHash);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }
}
