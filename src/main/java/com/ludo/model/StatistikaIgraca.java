package com.ludo.model;

public class StatistikaIgraca {

    private int igracId;

    // Sekcija 5.1 — statistike unutar jedne partije (akumuliraju se potez po potez)
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

    // Sekcija 5.2 — medjupartijske statistike (akumuliraju se partija po partija)
    private int totalPartija;
    private int totalPobjeda;
    private int totalPotezaSvihPartija;
    private int potezaUTrenutnojPartiji;   // resetuje se pozivom zavrsiPartiju()
    private int najduzaPartija;
    private int najkracaPartija;

    public StatistikaIgraca(int igracId) {
        this.igracId = igracId;
    }

    // Sekcija 5.2 — poziva se na kraju svake partije (pobjeda ili poraz)
    public void zavrsiPartiju(boolean pobjeda) {
        totalPartija++;
        if (pobjeda) totalPobjeda++;
        totalPotezaSvihPartija += potezaUTrenutnojPartiji;
        if (najkracaPartija == 0 || potezaUTrenutnojPartiji < najkracaPartija)
            najkracaPartija = potezaUTrenutnojPartiji;
        if (potezaUTrenutnojPartiji > najduzaPartija)
            najduzaPartija = potezaUTrenutnojPartiji;
        potezaUTrenutnojPartiji = 0;
    }

    public double getWinRate() {
        return totalPartija == 0 ? 0.0 : (double) totalPobjeda / totalPartija * 100;
    }

    public double getProsjecnoTrajanje() {
        return totalPartija == 0 ? 0.0 : (double) totalPotezaSvihPartija / totalPartija;
    }

    public double getProsjecnoEliminacija() {
        return totalPartija == 0 ? 0.0 : (double) eliminacijeIzvedene / totalPartija;
    }

    public String getOmiljeniStil() {
        if (stilIgreSkor > 0.6)  return "agresivan";
        if (stilIgreSkor < -0.3) return "defanzivan";
        return "balansiran";
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

    public int getTotalPartija() { return totalPartija; }
    public void setTotalPartija(int t) { this.totalPartija = t; }

    public int getTotalPobjeda() { return totalPobjeda; }
    public void setTotalPobjeda(int t) { this.totalPobjeda = t; }

    public int getTotalPotezaSvihPartija() { return totalPotezaSvihPartija; }
    public void setTotalPotezaSvihPartija(int t) { this.totalPotezaSvihPartija = t; }

    public int getPotezaUTrenutnojPartiji() { return potezaUTrenutnojPartiji; }
    public void setPotezaUTrenutnojPartiji(int p) { this.potezaUTrenutnojPartiji = p; }

    public int getNajduzaPartija() { return najduzaPartija; }
    public void setNajduzaPartija(int n) { this.najduzaPartija = n; }

    public int getNajkracaPartija() { return najkracaPartija; }
    public void setNajkracaPartija(int n) { this.najkracaPartija = n; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
            "StatistikaIgraca{igrac=%d, poteza=%d, sestice=%d, " +
            "eliminacijeIzvedene=%d, eliminacijePrimljene=%d, " +
            "figureUCilju=%d, blokade=%d, stilSkor=%.2f",
            igracId, ukupnoPoteza, brojSestica,
            eliminacijeIzvedene, eliminacijePrimljene,
            figureUCilju, blokadeKreirane, stilIgreSkor
        ));
        if (totalPartija > 0) {
            sb.append(String.format(
                ", [5.2] partija=%d, pobjeda=%d(%.0f%%), prosjecnoTrajanje=%.1f, " +
                "najduza=%d, najkraca=%d, prosjecnoEliminacija=%.2f, omiljeniStil=%s",
                totalPartija, totalPobjeda, getWinRate(),
                getProsjecnoTrajanje(), najduzaPartija, najkracaPartija,
                getProsjecnoEliminacija(), getOmiljeniStil()
            ));
        }
        sb.append("}");
        return sb.toString();
    }
}
