package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Historico;
import model.Rota;

/** DAO da tabela "historico": consultas feitas por usuários autenticados (RQ09). */
public class HistoricoDAO {

    private final RotaDAO rotaDAO = new RotaDAO();

    public void inserir(Historico historico) {
        String sql = "INSERT INTO historico (id_usuario, id_rota, data_consulta) VALUES (?, ?, ?)";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, historico.getIdUsuario());
            ps.setInt(2, historico.getRota().getIdRota());
            ps.setTimestamp(3, Timestamp.valueOf(historico.getDataConsulta()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    historico.setIdHistorico(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao registrar histórico.", e);
        }
    }

    /** Últimas consultas do usuário (no máximo 50), da mais recente para a mais antiga. */
    public List<Historico> listarPorUsuario(int idUsuario) {
        List<int[]> linhas = new ArrayList<>();
        List<LocalDateTime> datas = new ArrayList<>();
        String sql = "SELECT id_historico, id_rota, data_consulta FROM historico "
                + "WHERE id_usuario = ? ORDER BY data_consulta DESC, id_historico DESC LIMIT 50";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    linhas.add(new int[]{rs.getInt("id_historico"), rs.getInt("id_rota")});
                    datas.add(rs.getTimestamp("data_consulta").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar histórico.", e);
        }

        List<Historico> historicos = new ArrayList<>();
        for (int i = 0; i < linhas.size(); i++) {
            Rota rota = rotaDAO.buscarPorId(linhas.get(i)[1]);
            if (rota != null) {
                Historico h = new Historico(linhas.get(i)[0], idUsuario, rota);
                h.setDataConsulta(datas.get(i));
                historicos.add(h);
            }
        }
        return historicos;
    }
}
