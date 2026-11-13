package model;

/**
 * CLASSE ABSTRATA: representa um meio de transporte genérico.
 * As subclasses (Carro, Onibus e Bicicleta) herdam os atributos e são OBRIGADAS a escrever
 * os dois métodos abstratos, cada uma com a sua regra de cálculo (POLIMORFISMO).
 */
public abstract class Transporte {

    private int idTransporte;
    private String nome;
    private double velocidadeMediaKmH; // velocidade média em km/h
    private double emissaoCo2GKm;      // gramas de CO2 emitidas por km

    public Transporte(int idTransporte, String nome, double velocidadeMediaKmH, double emissaoCo2GKm) {
        this.idTransporte = idTransporte;
        this.nome = nome;
        this.velocidadeMediaKmH = velocidadeMediaKmH;
        this.emissaoCo2GKm = emissaoCo2GKm;
    }

    /** Tempo estimado, em MINUTOS, para percorrer a distância informada. */
    public abstract double calcularTempo(double distanciaKm);

    /** Impacto ambiental estimado, em GRAMAS de CO2, para percorrer a distância informada. */
    public abstract double calcularImpacto(double distanciaKm);

    public int getIdTransporte() {
        return idTransporte;
    }

    public void setIdTransporte(int idTransporte) {
        this.idTransporte = idTransporte;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getVelocidadeMediaKmH() {
        return velocidadeMediaKmH;
    }

    public void setVelocidadeMediaKmH(double velocidadeMediaKmH) {
        this.velocidadeMediaKmH = velocidadeMediaKmH;
    }

    public double getEmissaoCo2GKm() {
        return emissaoCo2GKm;
    }

    public void setEmissaoCo2GKm(double emissaoCo2GKm) {
        this.emissaoCo2GKm = emissaoCo2GKm;
    }
}
