package model;

import java.time.LocalDateTime;

/** Uma rota que o usuário salvou como favorita (RQ08). */
public class Favorito {

    private int idFavorito;
    private int idUsuario;
    private Rota rota;
    private LocalDateTime criadoEm;

    public Favorito(int idFavorito, int idUsuario, Rota rota) {
        this.idFavorito = idFavorito;
        this.idUsuario = idUsuario;
        this.rota = rota;
    }

    /** Prepara o favorito para ser gravado: marca a data e a hora em que foi salvo. */
    public void salvar() {
        this.criadoEm = LocalDateTime.now();
    }

    public int getIdFavorito() {
        return idFavorito;
    }

    public void setIdFavorito(int idFavorito) {
        this.idFavorito = idFavorito;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public Rota getRota() {
        return rota;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
