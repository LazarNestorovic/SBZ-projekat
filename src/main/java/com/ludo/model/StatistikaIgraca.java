package com.ludo.model;

public class StatistikaIgraca {

    private int igracId;
    private int ukupnoPoteza;
    private int brojSestica;
    private int uzastopneSesticeMax;
    private int eliminacijeIzvedene;
    private int eliminacijePrimljene;
    private int figureUCilju;
    private int bonusRollIskoristen;
    private int blokadeKreirane;
    private double prosjUdaljenostOdCilja;
    private int poteziNaSigurno;
    private double stilIgreSkor; // (eliminacije*2 - poteziNaSigurno) / ukupnoPoteza

    public StatistikaIgraca(int igracId) {
        this.igracId = igracId;
    }

    // Azurira stil igre skor nakon svakog poteza
    public void azurirajStilIgre() {
        if (ukupnoPoteza > 0) {
            stilIgreSkor = (double)(eliminacijeIzvedene * 2 - poteziNaSigurno) / ukupnoPoteza;
        }
    }

    // Azurira prosjecnu udaljenost (inkrementalno)
    public void azurirajProsjUdaljenost(double novaUdaljenost) {
        if (ukupnoPoteza == 0) {
            prosjUdaljenostOdCilja = novaUdaljenost;
        } else {
            prosjUdaljenostOdCilja = (prosjUdaljenostOdCilja * (ukupnoPoteza - 1) + novaUdaljenost) / ukupnoPoteza;
        }
    }

    public int getIgracId() { return igracId; }
    public void setIgracId(int igracId) { this.igracId = igracId; }

    public int getUkupnoPoteza() { return ukupnoPoteza; }
    public void setUkupnoPoteza(int u) { this.ukupnoPoteza = u; }

    public int getBrojSestica() { return brojSestica; }
    public void setBrojSestica(int b) { this.brojSestica = b; }

    public int getUzastopneSesticeMax() { return uzastopneSesticeMax; }
    public void setUzastopneSesticeMax(int u) { this.uzastopneSesticeMax = u; }

    public int getEliminacijeIzvedene() { return eliminacijeIzvedene; }
    public void setEliminacijeIzvedene(int e) { this.eliminacijeIzvedene = e; }

    public int getEliminacijePrimljene() { return eliminacijePrimljene; }
    public void setEliminacijePrimljene(int e) { this.eliminacijePrimljene = e; }

    public int getFigureUCilju() { return figureUCilju; }
    public void setFigureUCilju(int f) { this.figureUCilju = f; }

    public int getBonusRollIskoristen() { return bonusRollIskoristen; }
    public void setBonusRollIskoristen(int b) { this.bonusRollIskoristen = b; }

    public int getBlokadeKreirane() { return blokadeKreirane; }
    public void setBlokadeKreirane(int b) { this.blokadeKreirane = b; }

    public double getProsjUdaljenostOdCilja() { return prosjUdaljenostOdCilja; }
    public void setProsjUdaljenostOdCilja(double p) { this.prosjUdaljenostOdCilja = p; }

    public int getPoteziNaSigurno() { return poteziNaSigurno; }
    public void setPoteziNaSigurno(int p) { this.poteziNaSigurno = p; }

    public double getStilIgreSkor() { return stilIgreSkor; }
    public void setStilIgreSkor(double s) { this.stilIgreSkor = s; }

    @Override
    public String toString() {
        return String.format(
            "StatistikaIgraca{igrac=%d, poteza=%d, sestice=%d, " +
            "eliminacijeIzvedene=%d, eliminacijePrimljene=%d, " +
            "figureUCilju=%d, blokade=%d, stilSkor=%.2f}",
            igracId, ukupnoPoteza, brojSestica,
            eliminacijeIzvedene, eliminacijePrimljene,
            figureUCilju, blokadeKreirane, stilIgreSkor
        );
    }
}
