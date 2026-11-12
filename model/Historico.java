package model;

import java.time.LocalDateTime;

/** Registro de uma consulta de rota feita por um usuário autenticado (RQ09). */
public class Historico {

    private int idHistorico;
    private int idUsuario;
    private Rota rota;
    private LocalDateTime dataConsulta;

    public Historico(int idHistorico, int idUsuario, Rota rota) {
        this.idHistorico = idHistorico;
        this.idUsuario = idUsuario;
        this.rota = rota;
    }

    /** Prepara o registro para ser gravado: marca a data e a hora da consulta. */
    public void registrar() {
        this.dataConsulta = LocalDateTime.now();
    }

    public int getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(int idHistorico) {
        this.idHistorico = idHistorico;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public Rota getRota() {
        return rota;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDateTime dataConsulta) {
        this.dataConsulta = dataConsulta;
    }
}
