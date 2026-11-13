package model;

/**
 * No ônibus, a emissão cadastrada é POR PASSAGEIRO (o ônibus divide a emissão entre várias pessoas),
 * e o tempo inclui uma espera média no ponto.
 */
public class Onibus extends Transporte {

    // Espera média no ponto de ônibus, em minutos (valor fixo de simplificação).
    public static final double ESPERA_MEDIA_MINUTOS = 5.0;

    private int capacidadePassageiros;

    public Onibus(int idTransporte, String nome, double velocidadeMediaKmH,
                  double emissaoCo2GKm, int capacidadePassageiros) {
        super(idTransporte, nome, velocidadeMediaKmH, emissaoCo2GKm);
        this.capacidadePassageiros = capacidadePassageiros;
    }

    /** POLIMORFISMO: tempo de viagem + espera no ponto. */
    @Override
    public double calcularTempo(double distanciaKm) {
        double horas = distanciaKm / getVelocidadeMediaKmH();
        return horas * 60 + ESPERA_MEDIA_MINUTOS;
    }

    /** Emissão por passageiro vezes a distância. */
    @Override
    public double calcularImpacto(double distanciaKm) {
        return distanciaKm * getEmissaoCo2GKm();
    }

    public int getCapacidadePassageiros() {
        return capacidadePassageiros;
    }

    public void setCapacidadePassageiros(int capacidadePassageiros) {
        this.capacidadePassageiros = capacidadePassageiros;
    }
}
