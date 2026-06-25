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
    }

    // -------------------------------------------------------
    // Scenarijo 1: Moguca eliminacija ima najvisi prioritet
    // Igrac 0 (Crveni) moze eliminisati igraca 1 (Plavog)
    // -------------------------------------------------------
    static void scenarijoEliminacija(KieContainer kc) {
        System.out.println("--- SCENARIJO 1: Eliminacija protivnika (Prioritet 1) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0); // Igrac 0 (Crveni) je na potezu

            // Igrac 0: figura na relPos=18 (absPos=19), figura u bazi
            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(18); // absPos = (1+18-2)%52+1 = 18

            Figura f0b = new Figura(1, 0);
            // ostaje u bazi (relPos=0)

            // Igrac 1: figura na relPos=11 (absPos=(14+11-2)%52+1 = 24)
            // Igrac 0 sa dice=6: f0a ide na relPos=24, absPos=(1+24-2)%52+1 = 24 -> eliminacija!
            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(11); // absPos = 24

            // Igrac 1: druga figura na relPos=30
            Figura f1b = new Figura(3, 1);
            f1b.updatePozicija(30);

            IshodKocke kocka = new IshodKocke(6, 1); // dice=6, 1. uzastopna sestica

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

    // -------------------------------------------------------
    // Scenarijo 2: Kocka=6, figura u bazi, ali nema eliminacije
    // Treba uvesti novu figuru (Prioritet 2)
    // -------------------------------------------------------
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

    // -------------------------------------------------------
    // Scenarijo 3: Kocka != 6, sve figure u bazi -> preskaci potez
    // -------------------------------------------------------
    static void scenarijoSveUBazi(KieContainer kc) {
        System.out.println("--- SCENARIJO 3: Sve figure u bazi, kocka!=6 ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(2); // Igrac 2 (Zuti)

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

    // -------------------------------------------------------
    // Scenarijo 4: Tri uzastopne sestice -> gubi potez
    // -------------------------------------------------------
    static void scenarijoTriSestice(KieContainer kc) {
        System.out.println("--- SCENARIJO 4: Tri uzastopne sestice ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(1);

            Figura f1a = new Figura(0, 1);
            f1a.updatePozicija(10);

            IshodKocke kocka = new IshodKocke(6, 3);

            StatistikaIgraca stat1 = new StatistikaIgraca(1);
            stat1.setUzastopneSesticeMax(2); // vec imao 2 uzastopne

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

    // -------------------------------------------------------
    // Scenarijo 5: Figura u finalnoj stazi, pomjeri najblizu cilju
    // -------------------------------------------------------
    static void scenarijoFinalnaStaza(KieContainer kc) {
        System.out.println("--- SCENARIJO 5: Figura u finalnoj stazi (Prioritet 3) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(3); // Igrac 3 (Zeleni)

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

    // -------------------------------------------------------
    // Scenarijo 6: Statistike nakon vise poteza
    // Simulira 3 uzastopna poteza istog igraca da bi se statistike akumulirale
    // -------------------------------------------------------
    static void scenarijoStatistike(KieContainer kc) {
        System.out.println("--- SCENARIJO 6: Akumulacija statistika kroz vise poteza ---");

        StatistikaIgraca stat0 = new StatistikaIgraca(0);
        StatistikaIgraca stat1 = new StatistikaIgraca(1); // needs to be in session so eliminacije_primljene fires

        // Potez 1: eliminacija (dice=6)
        {
            KieSession ks = kc.newKieSession("ludoKsession");
            try {
                StanjeIgre stanje = new StanjeIgre(0);
                Figura f0a = new Figura(0, 0);
                f0a.updatePozicija(18);
                Figura f1a = new Figura(2, 1);
                f1a.updatePozicija(11); // absPos=24, igrac 0 ide na 24 -> eliminacija
                IshodKocke kocka = new IshodKocke(6, 1);
                ks.insert(stanje); ks.insert(f0a); ks.insert(f1a); ks.insert(kocka);
                ks.insert(stat0); ks.insert(stat1);
                ks.fireAllRules();
                System.out.println("Potez 1 - Rezultat: " + stanje);
                System.out.println("         stat igrac 1 (primio eliminaciju): " + stat1);
            } finally { ks.dispose(); }
        }

        // Potez 2: pomjeri na sigurno polje (dice=4, figura na relPos=5 ide na relPos=9 -> absPos=9 sigurno)
        {
            KieSession ks = kc.newKieSession("ludoKsession");
            try {
                StanjeIgre stanje = new StanjeIgre(0);
                Figura f0a = new Figura(0, 0);
                f0a.updatePozicija(5); // absPos=5
                IshodKocke kocka = new IshodKocke(4, 0);
                ks.insert(stanje); ks.insert(f0a); ks.insert(kocka); ks.insert(stat0);
                ks.fireAllRules();
                System.out.println("Potez 2 - Rezultat: " + stanje);
            } finally { ks.dispose(); }
        }

        // Potez 3: figura u finalnoj stazi (dice=3, figura na relPos=55 ide na 58=cilj)
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

    // -------------------------------------------------------
    // Scenarijo 7: Stil igre utica na prioritete (Sekcija 5.3)
    // Igrac sa agresivnim stilom bira eliminaciju nad uvođenjem figure
    // -------------------------------------------------------
    static void scenarijoStilIgre(KieContainer kc) {
        System.out.println("--- SCENARIJO 7: Stil igre utica na prioritete (5.3) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            // Igrac 0: figura moze eliminisati (relPos=18, dice=6 -> relPos=24)
            // i figura u bazi (normalno bi uvođenje bila prioritet 2, eliminacija prioritet 1)
            // Sa agresivnim stilom, eliminacija ostaje 1 - test pokazuje da 5.3 pravilo pokriva i edge case
            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(18);
            Figura f0b = new Figura(1, 0); // u bazi

            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(11); // absPos=24 -> eliminacija

            IshodKocke kocka = new IshodKocke(6, 1);

            // Igrac sa agresivnim stilom (visok stil skor)
            StatistikaIgraca stat0 = new StatistikaIgraca(0);
            stat0.setUkupnoPoteza(10);
            stat0.setEliminacijeIzvedene(5);
            stat0.setPoteziNaSigurno(1);
            stat0.azurirajStilIgre(); // stilSkor = (5*2-1)/10 = 0.9

            // Igrac sa puno primljenih eliminacija (defanzivni trigger)
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

    // -------------------------------------------------------
    // Scenarijo 8: Detekcija AGRESIVNOG modusa
    // Protivnik ima 3 figure na tabli, jedna od njih je < 10 polja od cilja.
    // Ocekujemo: AGRESIVNI modus, bijeg iz opasne zone demotiran na prioritet 5.
    // -------------------------------------------------------
    static void scenarijoModusAgresivni(KieContainer kc) {
        System.out.println("--- SCENARIJO 8: Modus Agresivni (4.3) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            // Igrac 0: jedna figura na tabli, u opasnoj zoni (protivnik je u dosegu)
            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(10);  // absPos = (1+10-2)%52+1 = 10
            // f0a je u opasnoj zoni jer ce je protivnicka figura moci dostici

            // Igrac 1: 3 figure na tabli, jedna u blizini cilja (relPos=50, absPos=(14+50-2)%52+1)
            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(5);   // absPos = (14+5-2)%52+1 = 18

            Figura f1b = new Figura(3, 1);
            f1b.updatePozicija(30);  // absPos = (14+30-2)%52+1 = 43

            Figura f1c = new Figura(4, 1);
            f1c.updatePozicija(50);  // relPos=50 >= 49 → blizu cilja! absPos = (14+50-2)%52+1 = 63%52+1=12 (wraparound)

            // Dice = 3; f0a moze ici na relPos=13 (van opasne zone) → bijeg iz opasne zone
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

    // -------------------------------------------------------
    // Scenarijo 9: Detekcija DEFANZIVNOG modusa
    // Nas igrac ima samo 1 figuru na tabli — izlozena pozicija.
    // Ocekujemo: DEFANZIVNI modus, potez na sigurno polje dobi prioritet 2.
    // -------------------------------------------------------
    static void scenarijoModusDefanzivni(KieContainer kc) {
        System.out.println("--- SCENARIJO 9: Modus Defanzivni (4.3) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(2);

            // Igrac 2: samo jedna figura na tabli, ostale u bazi
            Figura f2a = new Figura(0, 2);
            f2a.updatePozicija(3);  // absPos = (27+3-2)%52+1 = 29

            Figura f2b = new Figura(1, 2); // ostaje u bazi

            // Igrac 0: figura u blizini nase
            Figura f0a = new Figura(4, 0);
            f0a.updatePozicija(25);  // absPos = (1+25-2)%52+1 = 25

            // Kocka = 9 → zvjezdasto polje (apsolutnaPozicija 9 je sigurno) — ali dice max 6
            // Kocka = 6: f2a ide na relPos=9, absPos=(27+9-2)%52+1 = 35 (zvijezda — sigurno!)
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

    // -------------------------------------------------------
    // Scenarijo 10: Backward Chaining — pobjeda na dohvat ruke
    // Sve figure su unutar 3 poteza od cilja.
    // Ocekujemo: BC pravilo demotira poteze figura koje NISU na finalnoj stazi.
    // -------------------------------------------------------
    static void scenarijoBackwardChaining(KieContainer kc) {
        System.out.println("--- SCENARIJO 10: Backward Chaining - pobjeda na dohvat ruke (6.1) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            // Igrac 0: sva 4 potencijalna poteza blizu cilja
            // relPos 55 → (58-55+5)/6 = 8/6 = 1 potez (min)
            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(55);  // finalna staza

            // relPos 52 → (58-52+5)/6 = 11/6 = 1 potez
            Figura f0b = new Figura(1, 0);
            f0b.updatePozicija(52);  // aktivna, ali jednim korakom ulazi u finalnu

            // relPos 56 → (58-56+5)/6 = 7/6 = 1 potez
            Figura f0c = new Figura(2, 0);
            f0c.updatePozicija(56);  // finalna staza

            // Dice = 2: f0b (relPos=52) ide na relPos=54 (finalna staza, sigurna)
            //           f0a (relPos=55) ide na relPos=57 (finalija)
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

    // -------------------------------------------------------
    // Scenarijo 11: Complex Event Processing — detekcija dogadjaja
    // Ugrozena figura + protivnik blizu pobjede → DogadjajIgre CEP eventi
    // -------------------------------------------------------
    static void scenarijoCEP(KieContainer kc) {
        System.out.println("--- SCENARIJO 11: CEP - detekcija dogadjaja (6.2) ---");

        KieSession ks = kc.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(0);

            // Igrac 0: figura ugrozena (nezasticena, protivnik je u dosegu)
            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(10);  // absPos=10

            // Igrac 1: figura koja prijeti eliminacijom igraca 0
            // jeUDoseguZa(10, prot): razlika = (10 - prot + 52) % 52, >= 1 && <= 6
            // Npr. prot=7: (10-7+52)%52 = 3 → u dosegu!
            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(6);   // abs=(14+6-2)%52+1 = 19 — nije u dosegu za abs=10
            // Idemo direktno: igrac 1 relPos=9 → absPos=(14+9-2)%52+1 = 22 — ni to
            // Direktno postavimo: trebamo protivnika na absPos=7 da bude u dosegu za absPos=10
            // BoardUtils: jeUDoseguZa(10, 7) = (10-7+52)%52=3, 3>=1 && 3<=6 → true
            // relPos za igraca 1 koji daje absPos=7: (7 - 14 + 52) % 52 = 45... ne radi lijepo
            // Koristimo igraca 2 (startuje na 27): relPos da da absPos=7:
            //   (27 + relPos - 2) % 52 + 1 = 7 → (27+relPos-2)%52 = 6 → 25+relPos = 6 (mod 52) → relPos = 33
            // Za provjeru: (27+33-2)%52+1 = 58%52+1 = 6+1 = 7 ✓

            // Igrac 1 ima 3 figure u finalnoj stazi → PROTIVNIK_BLIZU_POBJEDE
            Figura f1b = new Figura(3, 1);
            f1b.updatePozicija(53); // finalna staza

            Figura f1c = new Figura(4, 1);
            f1c.updatePozicija(55); // finalna staza

            Figura f1d = new Figura(5, 1);
            f1d.updatePozicija(57); // finalna staza

            // Igrac 2: postavimo na absPos=7 da prijeti igracu 0 na absPos=10
            Figura f2a = new Figura(6, 2);
            f2a.updatePozicija(33); // absPos=7, u dosegu za absPos=10

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
