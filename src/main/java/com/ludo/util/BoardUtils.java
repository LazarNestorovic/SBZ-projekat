package com.ludo.util;

public class BoardUtils {

    public static final int[] POCETNE_POZICIJE = {1, 14, 27, 40};

    public static final int[] ZVJEZDASTA_POLJA = {9, 22, 35, 48};

    public static final int POCETNA_REL_POZICIJA = 1;
    public static final int ZADNJA_GLAVNA_REL_POZICIJA = 51;
    public static final int POCETNA_FINALNA_STAZA = 52;
    public static final int ZADNJA_FINALNA_STAZA = 56;
    public static final int CILJ = 57;

    public static int relativnaUApsolutnu(int vlasnikId, int relativnaPozicija) {
        if (relativnaPozicija < 1 || relativnaPozicija > ZADNJA_GLAVNA_REL_POZICIJA) return 0;
        return (POCETNE_POZICIJE[vlasnikId] + relativnaPozicija - 2) % 52 + 1;
    }

    public static int getPocetnaPoziciju(int igracId) {
        return POCETNE_POZICIJE[igracId];
    }

    public static boolean jeZvjezdastoPozicija(int apsolutnaPozicija) {
        for (int z : ZVJEZDASTA_POLJA) {
            if (z == apsolutnaPozicija) return true;
        }
        return false;
    }

    public static boolean jeSigurnaPozicija(int igracId, int apsolutnaPozicija) {
        if (jeZvjezdastoPozicija(apsolutnaPozicija)) return true;
        // Svaka startna pozicija je sigurno polje za sve igrace, ne samo za vlasnika te pozicije.
        for (int start : POCETNE_POZICIJE) {
            if (start == apsolutnaPozicija) return true;
        }
        return false;
    }

    public static boolean jeUDoseguZa(int ciljAbs, int napadacAbs) {
        if (ciljAbs <= 0 || napadacAbs <= 0) return false;
        int razlika = (ciljAbs - napadacAbs + 52) % 52;
        return razlika >= 1 && razlika <= 6;
    }
}
