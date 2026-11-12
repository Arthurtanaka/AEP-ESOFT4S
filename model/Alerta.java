package model;

import java.time.LocalDateTime;
import util.Formatador;

/** Aviso sobre uma rota, registrado por um administrador (RQ10). */
public class Alerta {

    public static final String STATUS_ATIVO = "ATIVO";
    public static final String STATUS_INATIVO = "INATIVO";

    private int idAlerta;
    private Rota rota;
    private int idUsuario; // administrador que registrou o alerta
    private String mensagem;
    private LocalDateTime dataAlerta;
    private String status;

    public Alerta(int idAlerta, Rota rota, int idUsuario, String mensagem,
                  LocalDateTime dataAlerta, String status) {
        this.idAlerta = idAlerta;
        this.rota = rota;
        this.idUsuario = idUsuario;
        this.mensagem = mensagem;
        this.dataAlerta = dataAlerta;
        this.status = status;
    }

    /** Monta o texto do alerta para ser mostrado na tela. */
    public String gerar() {
        return "[" + status + "] " + rota.getOrigem() + " -> " + rota.getDestino()
                + ": " + mensagem + " (" + Formatador.dataHora(dataAlerta) + ")";
    }

    public int getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }

    public Rota getRota() {
        return rota;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataAlerta() {
        return dataAlerta;
    }

    public void setDataAlerta(LocalDateTime dataAlerta) {
        this.dataAlerta = dataAlerta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
