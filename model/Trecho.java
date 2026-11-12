package model;

/**
 * Um Trecho é um pedaço de uma Rota feito com UM meio de transporte.
 * Exemplo: a rota "Centro -> Zona Norte" pode ter o trecho 1 de ônibus e o trecho 2 de bicicleta.
 *
 * Repare no POLIMORFISMO: o Trecho não sabe se o transporte é carro, ônibus ou bicicleta.
 * Ele apenas chama calcularTempo() e cada tipo de transporte responde do seu jeito.
 */
public class Trecho {

    private int idTrecho;
    private int ordem;          // posição do trecho dentro da rota (1, 2, 3...)
    private double distanciaKm;
    private Transporte transporte;

    public Trecho(int idTrecho, int ordem, double distanciaKm, Transporte transporte) {
        this.idTrecho = idTrecho;
        this.ordem = ordem;
        this.distanciaKm = distanciaKm;
        this.transporte = transporte;
    }

    public double calcularTempo() {
        return transporte.calcularTempo(distanciaKm);
    }

    public double calcularImpacto() {
        return transporte.calcularImpacto(distanciaKm);
    }

    public int getIdTrecho() {
        return idTrecho;
    }

    public void setIdTrecho(int idTrecho) {
        this.idTrecho = idTrecho;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public Transporte getTransporte() {
        return transporte;
    }

    public void setTransporte(Transporte transporte) {
        this.transporte = transporte;
    }
}
