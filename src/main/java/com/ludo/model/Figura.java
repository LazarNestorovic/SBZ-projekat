package com.ludo.model;

import com.ludo.util.BoardUtils;

public class Figura {

    private int id;
    private int vlasnikId;
    private int relativnaPozicija;
    private int apsolutnaPozicija;
    private StatusFigure status;
    private boolean sigurna;

    public Figura(int id, int vlasnikId) {
        this.id = id;
        this.vlasnikId = vlasnikId;
        this.relativnaPozicija = 0;
        this.apsolutnaPozicija = 0;
        this.status = StatusFigure.BAZA;
        this.sigurna = true;
    }

    public void updatePozicija(int novaPozicija) {
        this.relativnaPozicija = novaPozicija;
        if (novaPozicija == 0) {
            this.status = StatusFigure.BAZA;
            this.apsolutnaPozicija = 0;
            this.sigurna = true;
        } else if (novaPozicija >= 1 && novaPozicija <= 52) {
            this.status = StatusFigure.AKTIVNA;
            this.apsolutnaPozicija = BoardUtils.relativnaUApsolutnu(vlasnikId, novaPozicija);
            this.sigurna = BoardUtils.jeSigurnaPozicija(vlasnikId, this.apsolutnaPozicija);
        } else if (novaPozicija >= 53 && novaPozicija <= 57) {
            this.status = StatusFigure.FINALNA_STAZA;
            this.apsolutnaPozicija = 0;
            this.sigurna = true;
        } else if (novaPozicija == 58) {
            this.status = StatusFigure.ZAVRSENA;
            this.apsolutnaPozicija = 0;
            this.sigurna = true;
        }
    }

    public int getId() { return id; }
    public int getVlasnikId() { return vlasnikId; }
    public int getRelativnaPozicija() { return relativnaPozicija; }
    public int getApsolutnaPozicija() { return apsolutnaPozicija; }
    public StatusFigure getStatus() { return status; }
    public boolean isSigurna() { return sigurna; }

    public void setId(int id) { this.id = id; }
    public void setVlasnikId(int vlasnikId) { this.vlasnikId = vlasnikId; }
    public void setRelativnaPozicija(int p) { updatePozicija(p); }
    public void setApsolutnaPozicija(int p) { this.apsolutnaPozicija = p; }
    public void setStatus(StatusFigure s) { this.status = s; }
    public void setSigurna(boolean s) { this.sigurna = s; }

    @Override
    public String toString() {
        return "Figura{id=" + id + ", vlasnik=" + vlasnikId +
               ", relPos=" + relativnaPozicija + ", absPos=" + apsolutnaPozicija +
               ", status=" + status + ", sigurna=" + sigurna + "}";
    }
}
