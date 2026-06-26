package com.ludo.model;

public class DogadjajIgre {

    public enum TipDogadjaja {
        PRIJETNJA_ELIMINACIJOM,
        PRILIKE_ZA_ELIMINACIJU,
        VISESTRUKE_SESTICE,
        PROTIVNIK_BLIZU_POBJEDE,
        FIGURA_U_OPASNOJ_ZONI,
        BLOKADA_NA_KLJUCNOM_POLJU
    }

    private TipDogadjaja tip;
    private int igracId;    // affected player (our player)
    private int figuraId;   // relevant piece, -1 if n/a
    private int pozicija;   // relevant field,  -1 if n/a
    private long timestamp;

    public DogadjajIgre(TipDogadjaja tip, int igracId, int figuraId, int pozicija) {
        this.tip = tip;
        this.igracId = igracId;
        this.figuraId = figuraId;
        this.pozicija = pozicija;
        this.timestamp = System.currentTimeMillis();
    }

    public TipDogadjaja getTip() { return tip; }
    public int getIgracId() { return igracId; }
    public int getFiguraId() { return figuraId; }
    public int getPozicija() { return pozicija; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "DogadjajIgre{tip=" + tip + ", igrac=" + igracId +
               ", figura=" + figuraId + ", pozicija=" + pozicija + "}";
    }
}
