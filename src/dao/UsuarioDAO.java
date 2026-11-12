package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import model.Administrador;
import model.Passageiro;
import model.Usuario;

/**
 * DAO (Data Access Object) de usuários: só ele sabe o SQL da tabela "usuario".
 * As regras de negócio ficam no Service; aqui só gravamos e lemos dados.
 */
public class UsuarioDAO {

    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, email, senha_hash, tipo_usuario, preferencia, nivel_acesso) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        // PreparedStatement com "?" evita SQL Injection (nunca concatene texto digitado no SQL!)
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenhaHash());
            ps.setString(4, usuario.getPerfil());

            // Colunas específicas de cada perfil: a que não se aplica fica NULL
            if (usuario instanceof Passageiro) {
                ps.setString(5, ((Passageiro) usuario).getPreferencia());
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setNull(5, Types.VARCHAR);
                ps.setInt(6, ((Administrador) usuario).getNivelAcesso());
            }
            ps.executeUpdate();

            // Descobre o id que o banco gerou (AUTO_INCREMENT) e guarda no objeto
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setIdUsuario(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir usuário.", e);
        }
    }

    /** Procura pelo e-mail. Retorna null se não existir. */
    public Usuario buscarPorEmail(String email) {
        return buscarUm("SELECT * FROM usuario WHERE email = ?", email);
    }

    public Usuario buscarPorId(int idUsuario) {
        return buscarUm("SELECT * FROM usuario WHERE id_usuario = ?", String.valueOf(idUsuario));
    }

    public boolean existeEmail(String email) {
        return buscarPorEmail(email) != null;
    }

    private Usuario buscarUm(String sql, String parametro) {
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montar(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar usuário.", e);
        }
    }

    /**
     * Transforma uma linha da tabela no objeto Java certo.
     * A coluna tipo_usuario diz qual subclasse criar (herança com tabela única).
     */
    private Usuario montar(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_usuario");
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        String hash = rs.getString("senha_hash");
        String tipo = rs.getString("tipo_usuario");

        if ("ADMINISTRADOR".equals(tipo)) {
            return new Administrador(id, nome, email, hash, rs.getInt("nivel_acesso"));
        }
        return new Passageiro(id, nome, email, hash, rs.getString("preferencia"));
    }
}
