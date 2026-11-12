package dao;

/**
 * Erro ocorrido ao conversar com o banco de dados.
 * Estende RuntimeException para não obrigar todo método a declarar "throws" (mais simples).
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
