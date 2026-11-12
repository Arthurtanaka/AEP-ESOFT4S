package service;

import java.util.List;
import model.Alerta;
import model.Rota;

/**
 * Resultado da comparação de UMA alternativa de rota (RQ06 e RQ07):
 * guarda a rota, os totais calculados, os destaques e os alertas ativos.
 * É uma classe simples só para carregar dados (não vai para o banco).
 */
public class AlternativaComparada {

    private final Rota rota;
    private final double tempoMinutos;
    private final double distanciaKm;
    private final double impactoGramasCo2;
    private boolean maisSustentavel;
    private boolean maisRapida;
    private final List<Alerta> alertasAtivos;

    public AlternativaComparada(Rota rota, List<Alerta> alertasAtivos) {
        this.rota = rota;
        this.tempoMinutos = rota.calcularTempo();
        this.distanciaKm = rota.calcularDistancia();
        this.impactoGramasCo2 = rota.calcularImpacto();
        this.alertasAtivos = alertasAtivos;
    }

    public Rota getRota() {
        return rota;
    }

    public double getTempoMinutos() {
        return tempoMinutos;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public double getImpactoGramasCo2() {
        return impactoGramasCo2;
    }

    public boolean isMaisSustentavel() {
        return maisSustentavel;
    }

    public void setMaisSustentavel(boolean maisSustentavel) {
        this.maisSustentavel = maisSustentavel;
    }

    public boolean isMaisRapida() {
        return maisRapida;
    }

    public void setMaisRapida(boolean maisRapida) {
        this.maisRapida = maisRapida;
    }

    public List<Alerta> getAlertasAtivos() {
        return alertasAtivos;
    }
}
