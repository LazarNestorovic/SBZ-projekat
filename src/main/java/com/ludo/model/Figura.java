package com.ludo.model;

import com.ludo.util.BoardUtils;

public class Figura {

    private int id;
    private int vlasnikId;
    private int relativnaPozicija; // 0=baza, 1-52=tabla, 53-57=finalna staza, 58=cilj
    private int apsolutnaPozicija; // 1-52 ako je na glavnoj tabli, inace 0
    private StatusFigure status;
    private boolean sigurna;       // precomputed: da li je figura zasticena od eliminacije

    public Figura(int id, int vlasnikId) {
        this.id = id;
        this.vlasnikId = vlasnikId;
        this.relativnaPozicija = 0;
        this.apsolutnaPozicija = 0;
        this.status = StatusFigure.BAZA;
        this.sigurna = true;
    }

    // Azurira poziciju i preracunava sve izvedene vrijednosti
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
