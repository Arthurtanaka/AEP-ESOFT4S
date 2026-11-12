package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Bicicleta;
import model.Carro;
import model.Onibus;
import model.Transporte;

/** DAO da tabela "transporte". Faz o CRUD completo dos meios de transporte (RQ04). */
public class TransporteDAO implements CrudDAO<Transporte> {

    // ---------- CREATE ----------
    @Override
    public void inserir(Transporte t) {
        String sql = "INSERT INTO transporte (nome, tipo, velocidade_media_kmh, emissao_co2_g_km, "
                + "capacidade_passageiros, fator_congestionamento) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNome());
            ps.setString(2, tipoDe(t));
            ps.setDouble(3, t.getVelocidadeMediaKmH());
            ps.setDouble(4, t.getEmissaoCo2GKm());
            preencherEspecificos(ps, t, 5);
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    t.setIdTransporte(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir transporte.", e);
        }
    }

    // ---------- READ ----------
    @Override
    public List<Transporte> listar() {
        List<Transporte> lista = new ArrayList<>();
        String sql = "SELECT * FROM transporte ORDER BY nome";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar transportes.", e);
        }
    }

    /** Retorna null se não encontrar. */
    @Override
    public Transporte buscarPorId(int id) {
        String sql = "SELECT * FROM transporte WHERE id_transporte = ?";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar transporte.", e);
        }
    }

    // ---------- UPDATE ----------
    /** Atualiza os dados (o TIPO do transporte não muda). Retorna false se o id não existe. */
    @Override
    public boolean atualizar(Transporte t) {
        String sql = "UPDATE transporte SET nome = ?, velocidade_media_kmh = ?, emissao_co2_g_km = ?, "
                + "capacidade_passageiros = ?, fator_congestionamento = ? WHERE id_transporte = ?";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getNome());
            ps.setDouble(2, t.getVelocidadeMediaKmH());
            ps.setDouble(3, t.getEmissaoCo2GKm());
            preencherEspecificos(ps, t, 4);
            ps.setInt(6, t.getIdTransporte());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar transporte.", e);
        }
    }

    // ---------- DELETE ----------
    @Override
    public boolean excluir(int id) {
        String sql = "DELETE FROM transporte WHERE id_transporte = ?";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao excluir transporte.", e);
        }
    }

    /** Quantos trechos usam este transporte (para impedir exclusão de transporte em uso). */
    public int contarTrechosUsando(int idTransporte) {
        String sql = "SELECT COUNT(*) FROM trecho WHERE id_transporte = ?";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTransporte);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao verificar uso do transporte.", e);
        }
    }

    // ---------- Apoio ----------

    /** Transforma uma linha da tabela no objeto certo (Carro, Onibus ou Bicicleta). */
    static Transporte montar(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_transporte");
        String nome = rs.getString("nome");
        String tipo = rs.getString("tipo");
        double velocidade = rs.getDouble("velocidade_media_kmh");
        double emissao = rs.getDouble("emissao_co2_g_km");

        switch (tipo) {
            case "CARRO":
                return new Carro(id, nome, velocidade, emissao, rs.getDouble("fator_congestionamento"));
            case "ONIBUS":
                return new Onibus(id, nome, velocidade, emissao, rs.getInt("capacidade_passageiros"));
            default:
                return new Bicicleta(id, nome, velocidade);
        }
    }

    /** Descobre o texto da coluna "tipo" a partir da classe do objeto. */
    public static String tipoDe(Transporte t) {
        if (t instanceof Carro) {
            return "CARRO";
        }
        if (t instanceof Onibus) {
            return "ONIBUS";
        }
        return "BICICLETA";
    }

    /** Preenche capacidade (posição inicio) e fator (posição inicio+1); o que não se aplica vira NULL. */
    private void preencherEspecificos(PreparedStatement ps, Transporte t, int inicio) throws SQLException {
        if (t instanceof Onibus) {
            ps.setInt(inicio, ((Onibus) t).getCapacidadePassageiros());
        } else {
            ps.setNull(inicio, Types.INTEGER);
        }
        if (t instanceof Carro) {
            ps.setDouble(inicio + 1, ((Carro) t).getFatorCongestionamento());
        } else {
            ps.setNull(inicio + 1, Types.DECIMAL);
        }
    }
}
