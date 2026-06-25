package com.ludo.model;

public class StanjeIgre {

    private int aktivniIgracId;
    private boolean bonusRoll;
    private boolean preskaciPotez;
    private int odabranaFiguraId;   // -1 = jos nije odabrana
    private int odabranaPozicija;
    private String odabraniRazlog;
    private ModusIgre modus;        // automatski detektovan modus igre za ovaj potez

    public StanjeIgre(int aktivniIgracId) {
        this.aktivniIgracId = aktivniIgracId;
        this.bonusRoll = false;
        this.preskaciPotez = false;
        this.odabranaFiguraId = -1;
        this.odabranaPozicija = -1;
        this.odabraniRazlog = "";
        this.modus = ModusIgre.NEUTRALNI;
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

    public ModusIgre getModus() { return modus; }
    public void setModus(ModusIgre modus) { this.modus = modus; }

    @Override
    public String toString() {
        return "StanjeIgre{aktivniIgrac=" + aktivniIgracId +
               ", bonusRoll=" + bonusRoll + ", preskaci=" + preskaciPotez +
               ", modus=" + modus +
               ", odabranaFigura=" + odabranaFiguraId +
               ", razlog='" + odabraniRazlog + "'}";
    }
}
