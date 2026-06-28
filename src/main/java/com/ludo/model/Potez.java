package com.ludo.model;

public class Potez {

    private int figuraId;
    private int vlasnikId;
    private int novaRelativnaPozicija;
    private int novaApsolutnaPozicija;
    private int prioritet;
    private boolean validan;
    private boolean novaFigura;
    private boolean mozeEliminisati;
    private boolean naSignurnoPlje;
    private boolean izOpasneZone;
    private double rizikSkor;
    private String razlog;

    public Potez() {
        this.validan = true;
        this.prioritet = 7;
        this.razlog = "";
    }

    public int getFiguraId() { return figuraId; }
    public void setFiguraId(int figuraId) { this.figuraId = figuraId; }

    public int getVlasnikId() { return vlasnikId; }
    public void setVlasnikId(int vlasnikId) { this.vlasnikId = vlasnikId; }

    public int getNovaRelativnaPozicija() { return novaRelativnaPozicija; }
    public void setNovaRelativnaPozicija(int p) { this.novaRelativnaPozicija = p; }

    public int getNovaApsolutnaPozicija() { return novaApsolutnaPozicija; }
    public void setNovaApsolutnaPozicija(int p) { this.novaApsolutnaPozicija = p; }

    public int getPrioritet() { return prioritet; }
    public void setPrioritet(int prioritet) { this.prioritet = prioritet; }

    public boolean isValidan() { return validan; }
    public void setValidan(boolean validan) { this.validan = validan; }

    public boolean isNovaFigura() { return novaFigura; }
    public void setNovaFigura(boolean novaFigura) { this.novaFigura = novaFigura; }

    public boolean isMozeEliminisati() { return mozeEliminisati; }
    public void setMozeEliminisati(boolean mozeEliminisati) { this.mozeEliminisati = mozeEliminisati; }

    public boolean isNaSignurnoPlje() { return naSignurnoPlje; }
    public void setNaSignurnoPlje(boolean naSignurnoPlje) { this.naSignurnoPlje = naSignurnoPlje; }

    public boolean isIzOpasneZone() { return izOpasneZone; }
    public void setIzOpasneZone(boolean izOpasneZone) { this.izOpasneZone = izOpasneZone; }

    public double getRizikSkor() { return rizikSkor; }
    public void setRizikSkor(double rizikSkor) { this.rizikSkor = rizikSkor; }

    public String getRazlog() { return razlog; }
    public void setRazlog(String razlog) { this.razlog = razlog; }

    @Override
    public String toString() {
        return "Potez{figuraId=" + figuraId + ", vlasnik=" + vlasnikId +
               ", novaRelPos=" + novaRelativnaPozicija + ", novaAbsPos=" + novaApsolutnaPozicija +
               ", prioritet=" + prioritet + ", validan=" + validan +
               ", eliminira=" + mozeEliminisati + ", razlog='" + razlog + "'}";
    }
}
