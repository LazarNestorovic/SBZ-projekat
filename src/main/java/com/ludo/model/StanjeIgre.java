package com.ludo.model;

public class StanjeIgre {

    private int aktivniIgracId;
    private boolean bonusRoll;
    private boolean preskaciPotez;
    private int odabranaFiguraId;   // -1 = jos nije odabrana
    private int odabranaPozicija;
    private String odabraniRazlog;

    public StanjeIgre(int aktivniIgracId) {
        this.aktivniIgracId = aktivniIgracId;
        this.bonusRoll = false;
        this.preskaciPotez = false;
        this.odabranaFiguraId = -1;
        this.odabranaPozicija = -1;
        this.odabraniRazlog = "";
    }

    public int getAktivniIgracId() { return aktivniIgracId; }
    public void setAktivniIgracId(int aktivniIgracId) { this.aktivniIgracId = aktivniIgracId; }

    public boolean isBonusRoll() { return bonusRoll; }
    public void setBonusRoll(boolean bonusRoll) { this.bonusRoll = bonusRoll; }

    public boolean isPreskaciPotez() { return preskaciPotez; }
    public void setPreskaciPotez(boolean preskaciPotez) { this.preskaciPotez = preskaciPotez; }

    public int getOdabranaFiguraId() { return odabranaFiguraId; }
    public void setOdabranaFiguraId(int odabranaFiguraId) { this.odabranaFiguraId = odabranaFiguraId; }

    public int getOdabranaPozicija() { return odabranaPozicija; }
    public void setOdabranaPozicija(int odabranaPozicija) { this.odabranaPozicija = odabranaPozicija; }

    public String getOdabraniRazlog() { return odabraniRazlog; }
    public void setOdabraniRazlog(String odabraniRazlog) { this.odabraniRazlog = odabraniRazlog; }

    @Override
    public String toString() {
        return "StanjeIgre{aktivniIgrac=" + aktivniIgracId +
               ", bonusRoll=" + bonusRoll + ", preskaci=" + preskaciPotez +
               ", odabranaFigura=" + odabranaFiguraId +
               ", razlog='" + odabraniRazlog + "'}";
    }
}
