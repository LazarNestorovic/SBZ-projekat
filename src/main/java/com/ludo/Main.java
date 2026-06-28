package com.ludo;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.ludo.model.*;

public class Main {

    public static void main(String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();

        System.out.println("========================================");
        System.out.println("NE LJUTI SE COVJEČE - Inteligentni agent");
        System.out.println("========================================\n");

        scenarijoEliminacija(kc);
        scenarijoUvođenjeFigure(kc);
        scenarijoSveUBazi(kc);
        scenarijoTriSestice(kc);
        scenarijoFinalnaStaza(kc);
        scenarijoStatistike(kc);
        scenarijoStilIgre(kc);
        scenarijoModusAgresivni(kc);
        scenarijoModusDefanzivni(kc);
        scenarijoBackwardChaining(kc);
        scenarijoCEP(kc);
        scenarijoBCFallback(kc);
        scenarijoMedjupartijskeStatistike(kc);
    }

    static void scenarijoEliminacija(KieContainer kc) {
        System.out.println("--- SCENARIJO 1: Eliminacija protivnika (Prioritet 1) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(18);

            Figura f0b = new Figura(1, 0);

            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(11);

            Figura f1b = new Figura(3, 1);
            f1b.updatePozicija(30);

            IshodKocke kocka = new IshodKocke(6, 1);

            StatistikaIgraca stat0 = new StatistikaIgraca(0);
            StatistikaIgraca stat1 = new StatistikaIgraca(1);

            System.out.println("Igrac 0 figuke: " + f0a + ", " + f0b);
            System.out.println("Igrac 1 figure: " + f1a + ", " + f1b);
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f0a);
            ks.insert(f0b);
            ks.insert(f1a);
            ks.insert(f1b);
            ks.insert(kocka);
            ks.insert(stat0);
            ks.insert(stat1);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
            System.out.println("Bonus roll: " + stanje.isBonusRoll());
            System.out.println("Statistike igraca 0: " + stat0);
        } finally {
            ks.dispose();
        }
        System.out.println();
    }

    static void scenarijoUvođenjeFigure(KieContainer kc) {
        System.out.println("--- SCENARIJO 2: Uvođenje figure iz baze (Prioritet 2) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(5);

            Figura f0b = new Figura(1, 0);

            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(30);

            IshodKocke kocka = new IshodKocke(6, 1);

            StatistikaIgraca stat0 = new StatistikaIgraca(0);
            StatistikaIgraca stat1 = new StatistikaIgraca(1);

            System.out.println("Igrac 0 figure: " + f0a + ", " + f0b);
            System.out.println("Igrac 1 figura: " + f1a);
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f0a);
            ks.insert(f0b);
            ks.insert(f1a);
            ks.insert(kocka);
            ks.insert(stat0);
            ks.insert(stat1);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
        } finally {
            ks.dispose();
        }
        System.out.println();
    }

    static void scenarijoSveUBazi(KieContainer kc) {
        System.out.println("--- SCENARIJO 3: Sve figure u bazi, kocka!=6 ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(2);

            Figura f2a = new Figura(0, 2);
            Figura f2b = new Figura(1, 2);
            Figura f2c = new Figura(2, 2);
            Figura f2d = new Figura(3, 2);

            IshodKocke kocka = new IshodKocke(4, 0);

            StatistikaIgraca stat2 = new StatistikaIgraca(2);

            System.out.println("Sve figure igraca 2 su u bazi.");
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f2a);
            ks.insert(f2b);
            ks.insert(f2c);
            ks.insert(f2d);
            ks.insert(kocka);
            ks.insert(stat2);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
        } finally {
            ks.dispose();
        }
        System.out.println();
    }

    static void scenarijoTriSestice(KieContainer kc) {
        System.out.println("--- SCENARIJO 4: Tri uzastopne sestice ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(1);

            Figura f1a = new Figura(0, 1);
            f1a.updatePozicija(10);

            IshodKocke kocka = new IshodKocke(6, 3);

            StatistikaIgraca stat1 = new StatistikaIgraca(1);
            stat1.setUzastopneSesticeMax(2);

            System.out.println("Igrac 1 je bacio 3 uzastopne sestice.");
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f1a);
            ks.insert(kocka);
            ks.insert(stat1);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
            System.out.println("Statistike igraca 1: " + stat1);
        } finally {
            ks.dispose();
        }
        System.out.println();
    }

    static void scenarijoFinalnaStaza(KieContainer kc) {
        System.out.println("--- SCENARIJO 5: Figura u finalnoj stazi (Prioritet 3) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(3);

            Figura f3a = new Figura(0, 3);
            f3a.updatePozicija(55);

            Figura f3b = new Figura(1, 3);
            f3b.updatePozicija(20);

            IshodKocke kocka = new IshodKocke(2, 0);

            StatistikaIgraca stat3 = new StatistikaIgraca(3);

            System.out.println("Igrac 3 figure: " + f3a + ", " + f3b);
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f3a);
            ks.insert(f3b);
            ks.insert(kocka);
            ks.insert(stat3);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
        } finally {
            ks.dispose();
        }
        System.out.println();
    }

    static void scenarijoStatistike(KieContainer kc) {
        System.out.println("--- SCENARIJO 6: Akumulacija statistika kroz vise poteza ---");

        StatistikaIgraca stat0 = new StatistikaIgraca(0);
        StatistikaIgraca stat1 = new StatistikaIgraca(1);

        {
            KieSession ks = kc.newKieSession("ludoKsession");
            try {
                StanjeIgre stanje = new StanjeIgre(0);
                Figura f0a = new Figura(0, 0);
                f0a.updatePozicija(18);
                Figura f1a = new Figura(2, 1);
                f1a.updatePozicija(11);
                IshodKocke kocka = new IshodKocke(6, 1);
                ks.insert(stanje); ks.insert(f0a); ks.insert(f1a); ks.insert(kocka);
                ks.insert(stat0); ks.insert(stat1);
                ks.fireAllRules();
                System.out.println("Potez 1 - Rezultat: " + stanje);
                System.out.println("         stat igrac 1 (primio eliminaciju): " + stat1);
            } finally { ks.dispose(); }
        }

        {
            KieSession ks = kc.newKieSession("ludoKsession");
            try {
                StanjeIgre stanje = new StanjeIgre(0);
                Figura f0a = new Figura(0, 0);
                f0a.updatePozicija(5);
                IshodKocke kocka = new IshodKocke(4, 0);
                ks.insert(stanje); ks.insert(f0a); ks.insert(kocka); ks.insert(stat0);
                ks.fireAllRules();
                System.out.println("Potez 2 - Rezultat: " + stanje);
            } finally { ks.dispose(); }
        }

        {
            KieSession ks = kc.newKieSession("ludoKsession");
            try {
                StanjeIgre stanje = new StanjeIgre(0);
                Figura f0a = new Figura(0, 0);
                f0a.updatePozicija(55);
                IshodKocke kocka = new IshodKocke(3, 0);
                ks.insert(stanje); ks.insert(f0a); ks.insert(kocka); ks.insert(stat0);
                ks.fireAllRules();
                System.out.println("Potez 3 - Rezultat: " + stanje);
            } finally { ks.dispose(); }
        }

        System.out.println("\nFinalne statistike igraca 0: " + stat0);
        System.out.println();
    }

    static void scenarijoStilIgre(KieContainer kc) {
        System.out.println("--- SCENARIJO 7: Stil igre utica na prioritete (5.3) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(18);
            Figura f0b = new Figura(1, 0);

            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(11);

            IshodKocke kocka = new IshodKocke(6, 1);

            StatistikaIgraca stat0 = new StatistikaIgraca(0);
            stat0.setUkupnoPoteza(10);
            stat0.setEliminacijeIzvedene(5);
            stat0.setPoteziNaSigurno(1);
            stat0.azurirajStilIgre();

            StatistikaIgraca stat1 = new StatistikaIgraca(1);
            stat1.setEliminacijePrimljene(6);

            System.out.println("Igrac 0 stil skor: " + stat0.getStilIgreSkor() +
                " (agresivan > 0.6)");
            System.out.println("Igrac 1 primljene eliminacije: " + stat1.getEliminacijePrimljene() +
                " (> 5, aktivira defanzivu)");
            System.out.println();

            ks.insert(stanje);
            ks.insert(f0a); ks.insert(f0b);
            ks.insert(f1a);
            ks.insert(kocka);
            ks.insert(stat0); ks.insert(stat1);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
            System.out.println("Statistike igraca 0: " + stat0);
        } finally {
            ks.dispose();
        }
        System.out.println();
    }

    static void scenarijoModusAgresivni(KieContainer kc) {
        System.out.println("--- SCENARIJO 8: Modus Agresivni (4.3) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(10);

            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(5);

            Figura f1b = new Figura(3, 1);
            f1b.updatePozicija(30);

            Figura f1c = new Figura(4, 1);
            f1c.updatePozicija(50);

            IshodKocke kocka = new IshodKocke(3, 0);

            StatistikaIgraca stat0 = new StatistikaIgraca(0);
            StatistikaIgraca stat1 = new StatistikaIgraca(1);

            System.out.println("Igrac 0 figura: " + f0a);
            System.out.println("Igrac 1 figure: " + f1a + ", " + f1b + ", " + f1c);
            System.out.println("f1c relPos=50 >= 49 → agresivni modus trebao biti aktiviran");
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f0a);
            ks.insert(f1a); ks.insert(f1b); ks.insert(f1c);
            ks.insert(kocka);
            ks.insert(stat0); ks.insert(stat1);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
            System.out.println("Detektovani modus: " + stanje.getModus());
        } finally {
            ks.dispose();
        }
        System.out.println();
    }

    static void scenarijoModusDefanzivni(KieContainer kc) {
        System.out.println("--- SCENARIJO 9: Modus Defanzivni (4.3) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(2);

            Figura f2a = new Figura(0, 2);
            f2a.updatePozicija(3);

            Figura f2b = new Figura(1, 2);

            Figura f0a = new Figura(4, 0);
            f0a.updatePozicija(25);

            IshodKocke kocka = new IshodKocke(6, 0);

            StatistikaIgraca stat2 = new StatistikaIgraca(2);
            StatistikaIgraca stat0 = new StatistikaIgraca(0);

            System.out.println("Igrac 2 figure: " + f2a + " (jedina na tabli), " + f2b + " (baza)");
            System.out.println("Kocka: " + kocka);
            System.out.println("Ocekujemo DEFANZIVNI modus jer igrac ima <= 1 figuru na tabli");
            System.out.println();

            ks.insert(stanje);
            ks.insert(f2a); ks.insert(f2b);
            ks.insert(f0a);
            ks.insert(kocka);
            ks.insert(stat2); ks.insert(stat0);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
            System.out.println("Detektovani modus: " + stanje.getModus());
        } finally {
            ks.dispose();
        }
        System.out.println();
    }

    static void scenarijoBackwardChaining(KieContainer kc) {
        System.out.println("--- SCENARIJO 10: Backward Chaining - pobjeda na dohvat ruke (6.1) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(55);

            Figura f0b = new Figura(1, 0);
            f0b.updatePozicija(52);

            Figura f0c = new Figura(2, 0);
            f0c.updatePozicija(56);

            IshodKocke kocka = new IshodKocke(2, 0);

            StatistikaIgraca stat0 = new StatistikaIgraca(0);

            System.out.println("Igrac 0 figure: relPos=" + f0a.getRelativnaPozicija() +
                " (finalna), relPos=" + f0b.getRelativnaPozicija() +
                " (aktivna), relPos=" + f0c.getRelativnaPozicija() + " (finalna)");
            System.out.println("Sve figure su unutar 3 poteza od cilja (BC query)");
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f0a); ks.insert(f0b); ks.insert(f0c);
            ks.insert(kocka);
            ks.insert(stat0);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
            System.out.println("BC strateski izvjestaj ispisuje se gore ako su sve figure dostizne za 10 poteza.");
        } finally {
            ks.dispose();
        }
        System.out.println();
    }

    static void scenarijoBCFallback(KieContainer kc) {
        System.out.println("--- SCENARIJO 12: BC Fallback - eliminisi jer cilj nije blizu (6.1) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(10);

            Figura f0b = new Figura(1, 0);
            f0b.updatePozicija(18);

            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(11);

            IshodKocke kocka = new IshodKocke(6, 1);

            StatistikaIgraca stat0 = new StatistikaIgraca(0);
            StatistikaIgraca stat1 = new StatistikaIgraca(1);

            System.out.println("Igrac 0: f0a relPos=10 (daleko, 8 poteza do cilja > prag 5)");
            System.out.println("         f0b relPos=18 moze eliminisati f1a na absPos=24");
            System.out.println("BC Fallback treba promovirati eliminaciju na prioritet 1.");
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f0a); ks.insert(f0b);
            ks.insert(f1a);
            ks.insert(kocka);
            ks.insert(stat0); ks.insert(stat1);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
            System.out.println("Ocekivano: figura 1 (f0b) selektovana sa eliminacijom (BC Fallback prioritet 1)");
        } finally {
            ks.dispose();
        }
        System.out.println();
    }

    static void scenarijoMedjupartijskeStatistike(KieContainer kc) {
        System.out.println("--- SCENARIJO 13: Medjupartijske statistike (5.2) ---");

        StatistikaIgraca stat0 = new StatistikaIgraca(0);

        System.out.println("  -- Partija 1 --");

        {
            KieSession ks = kc.newKieSession("ludoKsession");
            try {
                StanjeIgre stanje = new StanjeIgre(0);
                Figura fA = new Figura(0, 0); fA.updatePozicija(58);
                Figura fB = new Figura(1, 0); fB.updatePozicija(30);
                Figura fC = new Figura(2, 0); fC.updatePozicija(20);
                Figura fD = new Figura(3, 0); fD.updatePozicija(55);
                IshodKocke kocka = new IshodKocke(3, 0);
                ks.insert(stanje); ks.insert(fA); ks.insert(fB); ks.insert(fC); ks.insert(fD);
                ks.insert(kocka); ks.insert(stat0);
                ks.fireAllRules();
                System.out.println("  Potez 1 zavrsio: potezaUTrenutnojPartiji=" + stat0.getPotezaUTrenutnojPartiji());
            } finally { ks.dispose(); }
        }

        {
            KieSession ks = kc.newKieSession("ludoKsession");
            try {
                StanjeIgre stanje = new StanjeIgre(0);
                Figura fA = new Figura(0, 0); fA.updatePozicija(58);
                Figura fB = new Figura(1, 0); fB.updatePozicija(58);
                Figura fC = new Figura(2, 0); fC.updatePozicija(20);
                Figura fD = new Figura(3, 0); fD.updatePozicija(56);
                IshodKocke kocka = new IshodKocke(2, 0);
                ks.insert(stanje); ks.insert(fA); ks.insert(fB); ks.insert(fC); ks.insert(fD);
                ks.insert(kocka); ks.insert(stat0);
                ks.fireAllRules();
                System.out.println("  Potez 2 zavrsio: potezaUTrenutnojPartiji=" + stat0.getPotezaUTrenutnojPartiji());
            } finally { ks.dispose(); }
        }

        {
            KieSession ks = kc.newKieSession("ludoKsession");
            try {
                StanjeIgre stanje = new StanjeIgre(0);
                Figura fA = new Figura(0, 0); fA.updatePozicija(58);
                Figura fB = new Figura(1, 0); fB.updatePozicija(58);
                Figura fC = new Figura(2, 0); fC.updatePozicija(58);
                Figura fD = new Figura(3, 0); fD.updatePozicija(56);
                IshodKocke kocka = new IshodKocke(2, 0);
                ks.insert(stanje); ks.insert(fA); ks.insert(fB); ks.insert(fC); ks.insert(fD);
                ks.insert(kocka); ks.insert(stat0);
                ks.fireAllRules();
                System.out.println("  Potez 3 (pobjednicki) rezultat: " + stanje);
            } finally { ks.dispose(); }
        }

        System.out.println();
        System.out.println("  -- Partija 2 (nova partija sa istim stat objektom) --");

        {
            KieSession ks = kc.newKieSession("ludoKsession");
            try {
                StanjeIgre stanje = new StanjeIgre(0);
                Figura fA = new Figura(0, 0); fA.updatePozicija(10);
                Figura fB = new Figura(1, 0); fB.updatePozicija(20);
                IshodKocke kocka = new IshodKocke(4, 0);
                ks.insert(stanje); ks.insert(fA); ks.insert(fB);
                ks.insert(kocka); ks.insert(stat0);
                ks.fireAllRules();
                System.out.println("  Potez partije 2: potezaUTrenutnojPartiji=" + stat0.getPotezaUTrenutnojPartiji());
            } finally { ks.dispose(); }
        }
        stat0.zavrsiPartiju(false);
        System.out.println("  Partija 2 zavrsena (poraz). Pozvano zavrsiPartiju(false).");

        System.out.println();
        System.out.println("=== Finalne medjupartijske statistike igraca 0 ===");
        System.out.println(stat0);
        System.out.println("  totalPartija  : " + stat0.getTotalPartija() + " (ocekivano: 2)");
        System.out.println("  totalPobjeda  : " + stat0.getTotalPobjeda() + " (ocekivano: 1)");
        System.out.println("  winRate       : " + String.format("%.0f", stat0.getWinRate()) + "% (ocekivano: 50%)");
        System.out.println("  najduzaPartija: " + stat0.getNajduzaPartija() + " poteza");
        System.out.println("  najkracaPartija: " + stat0.getNajkracaPartija() + " poteza");
        System.out.println("  prosjecnoTrajanje: " + String.format("%.1f", stat0.getProsjecnoTrajanje()));
        System.out.println("  omiljeniStil  : " + stat0.getOmiljeniStil());
        System.out.println();
    }

    static void scenarijoCEP(KieContainer kc) {
        System.out.println("--- SCENARIJO 11: CEP - detekcija dogadjaja (6.2) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(10);

            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(6);

            Figura f1b = new Figura(3, 1);
            f1b.updatePozicija(53);

            Figura f1c = new Figura(4, 1);
            f1c.updatePozicija(55);

            Figura f1d = new Figura(5, 1);
            f1d.updatePozicija(57);

            Figura f2a = new Figura(6, 2);
            f2a.updatePozicija(33);

            IshodKocke kocka = new IshodKocke(4, 0);

            StatistikaIgraca stat0 = new StatistikaIgraca(0);
            StatistikaIgraca stat1 = new StatistikaIgraca(1);
            StatistikaIgraca stat2 = new StatistikaIgraca(2);

            System.out.println("Igrac 0 figura: absPos=10 (ugrozena od igraca 2 na absPos=7)");
            System.out.println("Igrac 1: 3 figure u finalnoj stazi → PROTIVNIK_BLIZU_POBJEDE");
            System.out.println("Igrac 2: figura na absPos=7, u dosegu za absPos=10 → PRIJETNJA_ELIMINACIJOM");
            System.out.println("Ocekujemo CEP evente: PRIJETNJA_ELIMINACIJOM, PROTIVNIK_BLIZU_POBJEDE");
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f0a);
            ks.insert(f1a); ks.insert(f1b); ks.insert(f1c); ks.insert(f1d);
            ks.insert(f2a);
            ks.insert(kocka);
            ks.insert(stat0); ks.insert(stat1); ks.insert(stat2);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
            System.out.println("CEP eventi su insertovani u radnu memoriju (vidi [CEP] ispise gore).");
        } finally {
            ks.dispose();
        }
        System.out.println();
    }
}
