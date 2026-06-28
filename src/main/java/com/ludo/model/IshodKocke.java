package com.ludo.model;

public class IshodKocke {

    private int vrijednost;
    private int uzastopneSestice;

    public IshodKocke(int vrijednost, int uzastopneSestice) {
        this.vrijednost = vrijednost;
        this.uzastopneSestice = uzastopneSestice;
    }

    public int getVrijednost() { return vrijednost; }
    public void setVrijednost(int v) { this.vrijednost = v; }

    public int getUzastopneSestice() { return uzastopneSestice; }
    public void setUzastopneSestice(int u) { this.uzastopneSestice = u; }

    @Override
    public String toString() {
        return "IshodKocke{vrijednost=" + vrijednost +
               ", uzastopneSestice=" + uzastopneSestice + "}";
    }
}
