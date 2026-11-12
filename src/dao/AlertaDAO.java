package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Alerta;
import model.Rota;

/** DAO da tabela "alerta": avisos sobre rotas, registrados por administradores (RQ10). */
public class AlertaDAO {

    private final RotaDAO rotaDAO = new RotaDAO();

    public void inserir(Alerta alerta) {
        String sql = "INSERT INTO alerta (id_rota, id_usuario, mensagem, data_alerta, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, alerta.getRota().getIdRota());
            ps.setInt(2, alerta.getIdUsuario());
            ps.setString(3, alerta.getMensagem());
            ps.setTimestamp(4, Timestamp.valueOf(alerta.getDataAlerta()));
            ps.setString(5, alerta.getStatus());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    alerta.setIdAlerta(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir alerta.", e);
        }
    }

    /** Todos os alertas (ativos e inativos), usado pelo administrador. */
    public List<Alerta> listarTodos() {
        return consultar("SELECT * FROM alerta ORDER BY data_alerta DESC, id_alerta DESC", null);
    }

    /** Somente os alertas ATIVOS de uma rota (apoia a regra RN09). */
    public List<Alerta> listarAtivosPorRota(int idRota) {
        return consultar("SELECT * FROM alerta WHERE id_rota = ? AND status = 'ATIVO' "
                + "ORDER BY data_alerta DESC, id_alerta DESC", idRota);
    }

    public boolean alterarStatus(int idAlerta, String novoStatus) {
        String sql = "UPDATE alerta SET status = ? WHERE id_alerta = ?";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, novoStatus);
            ps.setInt(2, idAlerta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao alterar status do alerta.", e);
        }
    }

    /** Executa um SELECT de alertas (com ou sem o parâmetro idRota) e monta os objetos. */
    private List<Alerta> consultar(String sql, Integer idRota) {
        List<Object[]> linhas = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (idRota != null) {
                ps.setInt(1, idRota);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    linhas.add(new Object[]{
                            rs.getInt("id_alerta"), rs.getInt("id_rota"), rs.getInt("id_usuario"),
                            rs.getString("mensagem"), rs.getTimestamp("data_alerta").toLocalDateTime(),
                            rs.getString("status")});
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar alertas.", e);
        }

        List<Alerta> alertas = new ArrayList<>();
        for (Object[] l : linhas) {
            Rota rota = rotaDAO.buscarPorId((Integer) l[1]);
            if (rota != null) {
                alertas.add(new Alerta((Integer) l[0], rota, (Integer) l[2], (String) l[3],
                        (java.time.LocalDateTime) l[4], (String) l[5]));
            }
        }
        return alertas;
    }
}
