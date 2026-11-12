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
import model.Favorito;
import model.Rota;

/** DAO da tabela "favorito": rotas salvas pelo usuário (RQ08). */
public class FavoritoDAO {

    private final RotaDAO rotaDAO = new RotaDAO();

    public void inserir(Favorito favorito) {
        String sql = "INSERT INTO favorito (id_usuario, id_rota, criado_em) VALUES (?, ?, ?)";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, favorito.getIdUsuario());
            ps.setInt(2, favorito.getRota().getIdRota());
            ps.setTimestamp(3, Timestamp.valueOf(favorito.getCriadoEm()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    favorito.setIdFavorito(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao salvar favorito.", e);
        }
    }

    /** Já existe esta rota nos favoritos deste usuário? (apoia a regra RN08) */
    public boolean existe(int idUsuario, int idRota) {
        String sql = "SELECT COUNT(*) FROM favorito WHERE id_usuario = ? AND id_rota = ?";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idRota);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao verificar favorito.", e);
        }
    }

    /** Lista os favoritos do usuário, do mais recente para o mais antigo. */
    public List<Favorito> listarPorUsuario(int idUsuario) {
        // Passo 1: lê as linhas de favorito (guardando só os números)
        List<int[]> linhas = new ArrayList<>();
        List<LocalDateTime> datas = new ArrayList<>();
        String sql = "SELECT id_favorito, id_rota, criado_em FROM favorito "
                + "WHERE id_usuario = ? ORDER BY criado_em DESC, id_favorito DESC";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    linhas.add(new int[]{rs.getInt("id_favorito"), rs.getInt("id_rota")});
                    datas.add(rs.getTimestamp("criado_em").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar favoritos.", e);
        }

        // Passo 2: para cada favorito, carrega a rota completa (com trechos)
        List<Favorito> favoritos = new ArrayList<>();
        for (int i = 0; i < linhas.size(); i++) {
            Rota rota = rotaDAO.buscarPorId(linhas.get(i)[1]);
            if (rota != null) {
                Favorito f = new Favorito(linhas.get(i)[0], idUsuario, rota);
                f.setCriadoEm(datas.get(i));
                favoritos.add(f);
            }
        }
        return favoritos;
    }

    public boolean remover(int idUsuario, int idRota) {
        String sql = "DELETE FROM favorito WHERE id_usuario = ? AND id_rota = ?";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idRota);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao remover favorito.", e);
        }
    }
}
