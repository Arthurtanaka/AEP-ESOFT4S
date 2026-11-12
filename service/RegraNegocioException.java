package service;

/**
 * Exceção PRÓPRIA do sistema: lançada quando o usuário tenta fazer algo que viola uma regra de negócio
 * (por exemplo: cadastrar um e-mail que já existe). A mensagem é pensada para ser mostrada ao usuário.
 *
 * Estende RuntimeException (exceção "não verificada"), então não precisamos declarar "throws" em todo método.
 * Guarda também o código HTTP que a camada web deve devolver (400 = dado inválido, 401 = precisa entrar,
 * 403 = sem permissão, 404 = não encontrado).
 */
public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int status;

    /** Erro comum de regra de negócio (código HTTP 400). */
    public RegraNegocioException(String mensagem) {
        this(mensagem, 400);
    }

    public RegraNegocioException(String mensagem, int status) {
        super(mensagem);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
