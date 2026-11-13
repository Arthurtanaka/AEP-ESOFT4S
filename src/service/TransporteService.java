package service;

import dao.TransporteDAO;
import java.util.List;
import model.Bicicleta;
import model.Carro;
import model.Onibus;
import model.Transporte;
import model.Usuario;

/** Regras de negócio dos meios de transporte (RQ04). Somente administradores alteram (RN03). */
public class TransporteService {

    private final TransporteDAO transporteDAO;

    public TransporteService(TransporteDAO transporteDAO) {
        this.transporteDAO = transporteDAO;
    }

    public List<Transporte> listar() {
        return transporteDAO.listar();
    }

    /**
     * Cadastra um transporte.
     *
     * @param tipo        "CARRO", "ONIBUS" ou "BICICLETA"
     * @param fator       fator de congestionamento (só para CARRO)
     * @param capacidade  capacidade de passageiros (só para ONIBUS)
     */
    public Transporte cadastrar(Usuario solicitante, String tipo, String nome, double velocidade,
                                double emissao, Double fator, Integer capacidade) {
        Permissoes.exigirAdministrador(solicitante);
        validarComuns(nome, velocidade, emissao, tipo);

        Transporte transporte;
        if ("CARRO".equals(tipo)) {
            transporte = new Carro(0, nome.trim(), velocidade, emissao, validarFator(fator));
        } else if ("ONIBUS".equals(tipo)) {
            transporte = new Onibus(0, nome.trim(), velocidade, emissao, validarCapacidade(capacidade));
        } else if ("BICICLETA".equals(tipo)) {
            transporte = new Bicicleta(0, nome.trim(), velocidade); // bicicleta sempre com emissão zero
        } else {
            throw new RegraNegocioException("Tipo de transporte inválido. Use CARRO, ONIBUS ou BICICLETA.");
        }
        transporteDAO.inserir(transporte);
        return transporte;
    }

    /** Atualiza os dados de um transporte (o tipo não pode ser mudado). */
    public Transporte atualizar(Usuario solicitante, int idTransporte, String nome, double velocidade,
                                double emissao, Double fator, Integer capacidade) {
        Permissoes.exigirAdministrador(solicitante);
        Transporte transporte = transporteDAO.buscarPorId(idTransporte);
        if (transporte == null) {
            throw new RegraNegocioException("Transporte não encontrado.", 404);
        }
        validarComuns(nome, velocidade, emissao, TransporteDAO.tipoDe(transporte));

        transporte.setNome(nome.trim());
        transporte.setVelocidadeMediaKmH(velocidade);
        if (transporte instanceof Carro) {
            transporte.setEmissaoCo2GKm(emissao);
            ((Carro) transporte).setFatorCongestionamento(validarFator(fator));
        } else if (transporte instanceof Onibus) {
            transporte.setEmissaoCo2GKm(emissao);
            ((Onibus) transporte).setCapacidadePassageiros(validarCapacidade(capacidade));
        } else {
            transporte.setEmissaoCo2GKm(0.0); // bicicleta
        }
        transporteDAO.atualizar(transporte);
        return transporte;
    }

    public void excluir(Usuario solicitante, int idTransporte) {
        Permissoes.exigirAdministrador(solicitante);
        if (transporteDAO.buscarPorId(idTransporte) == null) {
            throw new RegraNegocioException("Transporte não encontrado.", 404);
        }
        int emUso = transporteDAO.contarTrechosUsando(idTransporte);
        if (emUso > 0) {
            throw new RegraNegocioException("Este transporte está em uso por " + emUso
                    + " trecho(s) de rotas e não pode ser excluído.");
        }
        transporteDAO.excluir(idTransporte);
    }

    // ---------- validações ----------

    private void validarComuns(String nome, double velocidade, double emissao, String tipo) {
        if (nome == null || nome.trim().length() < 2) {
            throw new RegraNegocioException("Informe o nome do transporte.");
        }
        if (velocidade <= 0 || velocidade > 200) {
            throw new RegraNegocioException("A velocidade média deve estar entre 0 e 200 km/h.");
        }
        if (!"BICICLETA".equals(tipo) && (emissao < 0 || emissao > 1000)) {
            throw new RegraNegocioException("A emissão deve estar entre 0 e 1000 g de CO2 por km.");
        }
    }

    private double validarFator(Double fator) {
        if (fator == null || fator < 1.0 || fator > 5.0) {
            throw new RegraNegocioException("O fator de congestionamento deve estar entre 1,0 e 5,0.");
        }
        return fator;
    }

    private int validarCapacidade(Integer capacidade) {
        if (capacidade == null || capacidade < 1) {
            throw new RegraNegocioException("A capacidade de passageiros deve ser de pelo menos 1.");
        }
        return capacidade;
    }
}
