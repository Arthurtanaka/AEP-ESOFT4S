package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Uma Rota é uma alternativa de deslocamento entre uma origem e um destino.
 * É a ENTIDADE PRINCIPAL do sistema (o CRUD completo é feito sobre ela).
 *
 * COMPOSIÇÃO 1:N: a Rota é composta por 1 ou mais Trechos. A lista de trechos "pertence"
 * à rota: se a rota for apagada, os trechos dela também são (no banco: ON DELETE CASCADE).
 */
public class Rota {

    private int idRota;
    private String origem;
    private String destino;
    private String descricao;
    private final List<Trecho> trechos = new ArrayList<>();

    public Rota(int idRota, String origem, String destino, String descricao) {
        this.idRota = idRota;
        this.origem = origem;
        this.destino = destino;
        this.descricao = descricao;
    }

    public void adicionarTrecho(Trecho trecho) {
        trechos.add(trecho);
    }

    /** Distância total = soma da distância de todos os trechos. */
    public double calcularDistancia() {
        double total = 0;
        for (Trecho t : trechos) {
            total += t.getDistanciaKm();
        }
        return total;
    }

    /** Tempo total (RN05) = soma do tempo de cada trecho, cada um calculado pelo seu transporte. */
    public double calcularTempo() {
        double total = 0;
        for (Trecho t : trechos) {
            total += t.calcularTempo();
        }
        return total;
    }

    /** Impacto total (RN05) = soma do impacto de cada trecho. */
    public double calcularImpacto() {
        double total = 0;
        for (Trecho t : trechos) {
            total += t.calcularImpacto();
        }
        return total;
    }

    public int getIdRota() {
        return idRota;
    }

    public void setIdRota(int idRota) {
        this.idRota = idRota;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /** Devolve a lista só para leitura: para incluir trechos, use adicionarTrecho(). */
    public List<Trecho> getTrechos() {
        return Collections.unmodifiableList(trechos);
    }
}
