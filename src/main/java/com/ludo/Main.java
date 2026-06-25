package com.ludo;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.ludo.model.*;
import com.ludo.util.BoardUtils;

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

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
            System.out.println("Bonus roll: " + stanje.isBonusRoll());
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

            // Igrac 0: jedna figura aktivna (relPos=5), jedna u bazi
            Figura f0a = new Figura(0, 0);
            f0a.updatePozicija(5); // absPos=5

            Figura f0b = new Figura(1, 0);
            // u bazi

            // Igrac 1: figura na poziciji gdje igrac 0 ne moze doseci (relPos=40 = absPos 40)
            Figura f1a = new Figura(2, 1);
            f1a.updatePozicija(30); // absPos=(14+30-2)%52+1 = 43

            IshodKocke kocka = new IshodKocke(6, 1);

            System.out.println("Igrac 0 figure: " + f0a + ", " + f0b);
            System.out.println("Igrac 1 figura: " + f1a);
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f0a);
            ks.insert(f0b);
            ks.insert(f1a);
            ks.insert(kocka);

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

            Figura f2a = new Figura(0, 2); // u bazi
            Figura f2b = new Figura(1, 2); // u bazi
            Figura f2c = new Figura(2, 2); // u bazi
            Figura f2d = new Figura(3, 2); // u bazi

            IshodKocke kocka = new IshodKocke(4, 0);

            System.out.println("Sve figure igraca 2 su u bazi.");
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f2a);
            ks.insert(f2b);
            ks.insert(f2c);
            ks.insert(f2d);
            ks.insert(kocka);

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

            // uzastopneSestice=3 → gubi potez
            IshodKocke kocka = new IshodKocke(6, 3);

            System.out.println("Igrac 1 je bacio 3 uzastopne sestice.");
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f1a);
            ks.insert(kocka);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
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

            // Figura u finalnoj stazi (relPos=55)
            Figura f3a = new Figura(0, 3);
            f3a.updatePozicija(55); // finalna staza, pozicija 3 od 5

            // Figura na glavnoj tabli (relPos=20)
            Figura f3b = new Figura(1, 3);
            f3b.updatePozicija(20);

            IshodKocke kocka = new IshodKocke(2, 0); // dice=2, f3a ide na relPos=57

            System.out.println("Igrac 3 figure: " + f3a + ", " + f3b);
            System.out.println("Kocka: " + kocka);
            System.out.println();

            ks.insert(stanje);
            ks.insert(f3a);
            ks.insert(f3b);
            ks.insert(kocka);

            ks.fireAllRules();

            System.out.println("Rezultat: " + stanje);
        } finally {
            ks.dispose();
        }
        System.out.println();
    }
}
