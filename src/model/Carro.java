package model;

/**
 * O carro anda mais devagar quando há trânsito. O "fator de congestionamento" representa isso:
 * 1.30 significa que a viagem demora 30% mais do que demoraria com a pista livre.
 */
public class Carro extends Transporte {

    private double fatorCongestionamento;

    public Carro(int idTransporte, String nome, double velocidadeMediaKmH,
                 double emissaoCo2GKm, double fatorCongestionamento) {
        super(idTransporte, nome, velocidadeMediaKmH, emissaoCo2GKm);
        this.fatorCongestionamento = fatorCongestionamento;
    }

    /** POLIMORFISMO: tempo = (distância / velocidade) em minutos, multiplicado pelo congestionamento. */
    @Override
    public double calcularTempo(double distanciaKm) {
        double horas = distanciaKm / getVelocidadeMediaKmH();
        return horas * 60 * fatorCongestionamento;
    }

    /** O carro emite a quantidade de CO2 por km multiplicada pela distância. */
    @Override
    public double calcularImpacto(double distanciaKm) {
        return distanciaKm * getEmissaoCo2GKm();
    }

    public double getFatorCongestionamento() {
        return fatorCongestionamento;
    }

    public void setFatorCongestionamento(double fatorCongestionamento) {
        this.fatorCongestionamento = fatorCongestionamento;
    }
}
