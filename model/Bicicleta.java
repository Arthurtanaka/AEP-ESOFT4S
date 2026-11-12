package model;

/** A bicicleta não emite CO2: seu impacto ambiental é sempre zero. */
public class Bicicleta extends Transporte {

    public Bicicleta(int idTransporte, String nome, double velocidadeMediaKmH) {
        super(idTransporte, nome, velocidadeMediaKmH, 0.0); // emissão fixa em zero
    }

    @Override
    public double calcularTempo(double distanciaKm) {
        double horas = distanciaKm / getVelocidadeMediaKmH();
        return horas * 60;
    }

    @Override
    public double calcularImpacto(double distanciaKm) {
        return 0.0;
    }
}
