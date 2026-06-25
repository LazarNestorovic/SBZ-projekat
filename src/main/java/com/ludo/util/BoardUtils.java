package com.ludo.util;

public class BoardUtils {

    // Apsolutne startne pozicije za svakog igraca (0=Crveni, 1=Plavi, 2=Zuti, 3=Zeleni)
    public static final int[] POCETNE_POZICIJE = {1, 14, 27, 40};

    // Zvjezdasta (sigurna) polja - sigurna za sve igraca
    public static final int[] ZVJEZDASTA_POLJA = {9, 22, 35, 48};

    // Relativna pozicija 1-52 = glavna tabla, 53-57 = finalna staza, 58 = cilj
    public static final int POCETNA_REL_POZICIJA = 1;
    public static final int ZADNJA_GLAVNA_REL_POZICIJA = 52;
    public static final int POCETNA_FINALNA_STAZA = 53;
    public static final int ZADNJA_FINALNA_STAZA = 57;
    public static final int CILJ = 58;

    // Pretvara relativnu poziciju figure u apsolutnu poziciju na tabli (1-52)
    // Vraca 0 ako figura nije na glavnoj tabli
    public static int relativnaUApsolutnu(int vlasnikId, int relativnaPozicija) {
        if (relativnaPozicija < 1 || relativnaPozicija > 52) return 0;
        return (POCETNE_POZICIJE[vlasnikId] + relativnaPozicija - 2) % 52 + 1;
    }

    // Vraca startnu apsolutnu poziciju za datog igraca
    public static int getPocetnaPoziciju(int igracId) {
        return POCETNE_POZICIJE[igracId];
    }

    // Provjera da li je data apsolutna pozicija zvjezdasta (sigurna za sve)
    public static boolean jeZvjezdastoPozicija(int apsolutnaPozicija) {
        for (int z : ZVJEZDASTA_POLJA) {
            if (z == apsolutnaPozicija) return true;
        }
        return false;
    }

    // Provjera da li je pozicija sigurna za datog igraca
    // (zvjezdasto polje ILI vlastita startna pozicija)
    public static boolean jeSigurnaPozicija(int igracId, int apsolutnaPozicija) {
        if (jeZvjezdastoPozicija(apsolutnaPozicija)) return true;
        return POCETNE_POZICIJE[igracId] == apsolutnaPozicija;
    }

    // Provjera da li napadac moze doseci ciljnu poziciju bacanjem kocke 1-6
    // (provjerava da li je ciljAbs u dosegu napadacaAbs za jedan potez)
    public static boolean jeUDoseguZa(int ciljAbs, int napadacAbs) {
        if (ciljAbs <= 0 || napadacAbs <= 0) return false;
        int razlika = (ciljAbs - napadacAbs + 52) % 52;
        return razlika >= 1 && razlika <= 6;
    }
}
