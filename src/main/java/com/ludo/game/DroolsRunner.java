package com.ludo.game;

import com.ludo.model.*;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DroolsRunner {

    private final KieContainer kieContainer;

    public DroolsRunner(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    /**
     * Runs one agent turn. All 16 figures and all 4 stat objects must be provided
     * so that cross-player rules (e.g. eliminacija primljena) fire correctly.
     */
    public DroolsResult run(int igracId,
                            List<Figura> figure,
                            IshodKocke kocka,
                            StatistikaIgraca[] sveStat) {
        KieSession ks = kieContainer.newKieSession("ludoKsession");
        try {
            StanjeIgre stanje = new StanjeIgre(igracId);
            ks.insert(stanje);
            for (Figura f : figure) {
                ks.insert(f);
            }
            ks.insert(kocka);
            for (StatistikaIgraca stat : sveStat) {
                ks.insert(stat);
            }

            ks.fireAllRules();

            // Collect all Potez facts inserted by the rules
            Collection<?> potezFakti = ks.getObjects(new ClassObjectFilter(Potez.class));
            List<Potez> sviPotezi = potezFakti.stream()
                    .map(o -> (Potez) o)
                    .collect(Collectors.toList());

            // Collect all CEP event facts
            Collection<?> dogadjajFakti = ks.getObjects(new ClassObjectFilter(DogadjajIgre.class));
            List<DogadjajIgre> dogadjaji = dogadjajFakti.stream()
                    .map(o -> (DogadjajIgre) o)
                    .collect(Collectors.toList());

            // stanje is modified in-place by Drools modify() — no need to extract from session
            return new DroolsResult(stanje, sviPotezi, dogadjaji);
        } finally {
            ks.dispose();
        }
    }

    public static class DroolsResult {
        public final StanjeIgre stanje;
        public final List<Potez> sviPotezi;
        public final List<DogadjajIgre> dogadjaji;

        public DroolsResult(StanjeIgre stanje, List<Potez> sviPotezi, List<DogadjajIgre> dogadjaji) {
            this.stanje = stanje;
            this.sviPotezi = sviPotezi;
            this.dogadjaji = dogadjaji;
        }
    }
}
