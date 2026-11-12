package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import util.Configuracao;

/**
 * Cria conexões com o banco MySQL usando JDBC.
 *
 * JDBC é a "ponte" do Java com o banco de dados. Para ela funcionar é preciso o DRIVER do MySQL
 * (um arquivo .jar chamado MySQL Connector/J) na pasta lib/.
 * Os dados de acesso (URL, usuário e senha) vêm da classe Configuracao.
 */
public class ConexaoBanco {

    private ConexaoBanco() {
    }

    /** Abre uma nova conexão. Quem pedir é responsável por fechar (usamos try-with-resources). */
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(
                Configuracao.getDbUrl(),
                Configuracao.getDbUsuario(),
                Configuracao.getDbSenha());
    }

    /** Testa se dá para conectar. Usado na inicialização para avisar o usuário de forma amigável. */
    public static void testarConexao() {
        try (Connection con = getConexao()) {
            if (!con.isValid(3)) { // pergunta ao banco se a conexão está viva (até 3 segundos)
                throw new SQLException("A conexão foi aberta, mas não respondeu.");
            }
        } catch (SQLException e) {
            throw new DaoException("Não foi possível conectar ao banco de dados: " + e.getMessage(), e);
        }
    }
}
