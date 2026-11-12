package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Rota;
import model.Trecho;

/**
 * DAO da tabela "rota" (e dos seus "trecho"). É o CRUD da ENTIDADE PRINCIPAL do sistema (RQ03).
 *
 * Como a Rota é COMPOSTA por Trechos, gravar uma rota significa gravar a linha da rota E as linhas
 * dos trechos. Isso é feito dentro de uma TRANSAÇÃO: ou grava tudo, ou não grava nada
 * (se um trecho falhar, a rota também é desfeita com rollback).
 */
public class RotaDAO implements CrudDAO<Rota> {

    // ---------- CREATE ----------
    @Override
    public void inserir(Rota rota) {
        String sqlRota = "INSERT INTO rota (origem, destino, descricao) VALUES (?, ?, ?)";
        try (Connection con = ConexaoBanco.getConexao()) {
            con.setAutoCommit(false); // começa a transação: nada é definitivo até o commit
            try (PreparedStatement ps = con.prepareStatement(sqlRota, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, rota.getOrigem());
                ps.setString(2, rota.getDestino());
                ps.setString(3, rota.getDescricao());
                ps.executeUpdate();
                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    if (chaves.next()) {
                        rota.setIdRota(chaves.getInt(1));
                    }
                }
                inserirTrechos(con, rota);
                con.commit(); // tudo certo: confirma
            } catch (SQLException e) {
                con.rollback(); // deu erro: desfaz tudo
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao inserir rota.", e);
        }
    }

    // ---------- READ ----------
    @Override
    public List<Rota> listar() {
        String sql = "SELECT * FROM rota ORDER BY origem, destino, id_rota";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return montarLista(con, rs);
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar rotas.", e);
        }
    }

    @Override
    public Rota buscarPorId(int id) {
        String sql = "SELECT * FROM rota WHERE id_rota = ?";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                List<Rota> rotas = montarLista(con, rs);
                return rotas.isEmpty() ? null : rotas.get(0);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar rota.", e);
        }
    }

    /**
     * Busca as alternativas entre uma origem e um destino (RQ05).
     * O texto digitado pode ser só um pedaço do nome ("centro" encontra "Centro de Maringá").
     */
    public List<Rota> buscarPorOrigemDestino(String origem, String destino) {
        String sql = "SELECT * FROM rota WHERE origem LIKE ? AND destino LIKE ? ORDER BY id_rota";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + origem + "%");
            ps.setString(2, "%" + destino + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return montarLista(con, rs);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar rotas por origem e destino.", e);
        }
    }

    // ---------- UPDATE ----------
    /**
     * Atualiza a rota e SUBSTITUI todos os seus trechos pelos trechos do objeto recebido.
     * Também é uma transação. Retorna false se a rota não existir.
     */
    @Override
    public boolean atualizar(Rota rota) {
        String sqlRota = "UPDATE rota SET origem = ?, destino = ?, descricao = ? WHERE id_rota = ?";
        try (Connection con = ConexaoBanco.getConexao()) {
            con.setAutoCommit(false);
            try {
                int linhas;
                try (PreparedStatement ps = con.prepareStatement(sqlRota)) {
                    ps.setString(1, rota.getOrigem());
                    ps.setString(2, rota.getDestino());
                    ps.setString(3, rota.getDescricao());
                    ps.setInt(4, rota.getIdRota());
                    linhas = ps.executeUpdate();
                }
                if (linhas == 0) {
                    con.rollback();
                    return false;
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM trecho WHERE id_rota = ?")) {
                    ps.setInt(1, rota.getIdRota());
                    ps.executeUpdate();
                }
                inserirTrechos(con, rota);
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar rota.", e);
        }
    }

    // ---------- DELETE ----------
    /** Apaga a rota. Os trechos (e favoritos, histórico e alertas dela) saem junto: ON DELETE CASCADE. */
    @Override
    public boolean excluir(int id) {
        String sql = "DELETE FROM rota WHERE id_rota = ?";
        try (Connection con = ConexaoBanco.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao excluir rota.", e);
        }
    }

    // ---------- Apoio ----------

    /** Grava, usando a MESMA conexão (mesma transação), todos os trechos da rota. */
    private void inserirTrechos(Connection con, Rota rota) throws SQLException {
        String sql = "INSERT INTO trecho (id_rota, id_transporte, ordem, distancia_km) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Trecho trecho : rota.getTrechos()) {
                ps.setInt(1, rota.getIdRota());
                ps.setInt(2, trecho.getTransporte().getIdTransporte());
                ps.setInt(3, trecho.getOrdem());
                ps.setDouble(4, trecho.getDistanciaKm());
                ps.executeUpdate();
                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    if (chaves.next()) {
                        trecho.setIdTrecho(chaves.getInt(1));
                    }
                }
            }
        }
    }

    /** Lê as linhas de "rota" do ResultSet e, para cada uma, carrega seus trechos. */
    private List<Rota> montarLista(Connection con, ResultSet rs) throws SQLException {
        List<Rota> rotas = new ArrayList<>();
        while (rs.next()) {
            Rota rota = new Rota(
                    rs.getInt("id_rota"),
                    rs.getString("origem"),
                    rs.getString("destino"),
                    rs.getString("descricao"));
            rotas.add(rota);
        }
        // Os trechos são carregados depois de ler todas as rotas (um ResultSet por vez na conexão)
        for (Rota rota : rotas) {
            carregarTrechos(con, rota);
        }
        return rotas;
    }

    /** JOIN entre trecho e transporte: cada trecho já volta com o seu objeto Transporte. */
    private void carregarTrechos(Connection con, Rota rota) throws SQLException {
        String sql = "SELECT tr.id_trecho, tr.ordem, tr.distancia_km, t.* "
                + "FROM trecho tr JOIN transporte t ON t.id_transporte = tr.id_transporte "
                + "WHERE tr.id_rota = ? ORDER BY tr.ordem";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rota.getIdRota());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rota.adicionarTrecho(new Trecho(
                            rs.getInt("id_trecho"),
                            rs.getInt("ordem"),
                            rs.getDouble("distancia_km"),
                            TransporteDAO.montar(rs))); // polimorfismo: devolve Carro, Onibus ou Bicicleta
                }
            }
        }
    }
}
