# Ne ljuti se čovječe — Inteligentni agent (Drools)

**Predmet:** Sistemi bazirani na znanju  
**Tim:** Luka Keselj, Lazar Nestorovic, Marko Mihajlovic

---

## Pokretanje projekta

### Preduslovi

- Java 11+ (testirano na Java 25)
- Internet konekcija za prvi build (Maven preuzima zavisnosti)

---

### Korak 1 — Postavi Maven (jednokratno)

```bash
curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz -o /tmp/mvn.tar.gz
tar -xzf /tmp/mvn.tar.gz -C /tmp/
export PATH="/tmp/apache-maven-3.9.5/bin:$PATH"
mvn -version
```

Očekivani izlaz: `Apache Maven 3.9.5`

---

### Korak 2 — Kreiraj Java stub (jednokratno)

Drools 7.x koristi MVEL2 koji interno referencira `java.lang.Compiler`, klasu uklonjenu u Javi 17+.
Ovaj korak kreira stub koji to popravlja:

```bash
mkdir -p /tmp/stub/java/lang
cat > /tmp/stub/java/lang/Compiler.java << 'EOF'
package java.lang;
public final class Compiler {
    private Compiler() {}
    public static boolean compileClass(Class<?> c) { return false; }
    public static boolean compileClasses(String s) { return false; }
    public static Object command(Object o) { return null; }
    public static void enable() {}
    public static void disable() {}
}
EOF
javac --patch-module java.base=/tmp/stub -d /tmp/stub-compiled /tmp/stub/java/lang/Compiler.java
jar cf /tmp/compiler-stub.jar -C /tmp/stub-compiled .
```

---

### Korak 3 — Build

```bash
cd /home/lazar/Documents/SBNZ/SBZ-projekat
export PATH="/tmp/apache-maven-3.9.5/bin:$PATH"
mvn clean package -q
```

Nakon uspješnog builda pojavljuje se: `target/ne-ljuti-se-1.0-SNAPSHOT.jar`

---

### Korak 4 — Pokretanje

```bash
java --patch-module java.base=/tmp/compiler-stub.jar -jar target/ne-ljuti-se-1.0-SNAPSHOT.jar
```

---

### Svaki sljedeći put (koraci 1 i 2 su već urađeni)

```bash
cd /home/lazar/Documents/SBNZ/SBZ-projekat
export PATH="/tmp/apache-maven-3.9.5/bin:$PATH"
mvn clean package -q && java --patch-module java.base=/tmp/compiler-stub.jar -jar target/ne-ljuti-se-1.0-SNAPSHOT.jar
```

> **Napomena:** `/tmp` se briše pri restartu sistema. U tom slučaju ponoviti korake 1 i 2.

---

## Struktura projekta

```
SBZ-projekat/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/ludo/
    │   ├── Main.java                     # Test scenariji
    │   ├── model/
    │   │   ├── Figura.java               # Figura igrača na tabli
    │   │   ├── StatusFigure.java         # Enum: BAZA, AKTIVNA, FINALNA_STAZA, ZAVRSENA
    │   │   ├── IshodKocke.java           # Vrijednost kocke + broj uzastopnih sestica
    │   │   ├── Potez.java                # Kandidat potez sa prioritetom i flagovima
    │   │   ├── StanjeIgre.java           # Stanje trenutnog poteza (aktivni igrač, bonus roll...)
    │   │   └── StatistikaIgraca.java     # Statistike igrača tokom partije
    │   └── util/
    │       └── BoardUtils.java           # Konstante table i helper metode
    └── resources/
        ├── META-INF/kmodule.xml
        └── rules/ludo.drl                # Sva Drools pravila
```

---

## Šta je implementirano

### Model podataka

- **`Figura`** — pozicija figure modelovana kao `relativnaPozicija` (0=baza, 1–52=glavna tabla, 53–57=finalna staza, 58=cilj) i `apsolutnaPozicija` (1–52 na tabli, radi detekcije kolizija). Polje `sigurna` automatski se ažurira.
- **`BoardUtils`** — startne pozicije igrača `{1, 14, 27, 40}`, zvjezdasta (sigurna) polja `{9, 22, 35, 48}`, konverzija relativna→apsolutna pozicija, provjera dosega protivnika.

### Pravila (ludo.drl) — Forward Chaining u 3 nivoa

**Nivo 1 — Inicijacija** (provjera kocke i dostupnih akcija):
- Tri uzastopne sestice → igrač gubi potez, ne pomjera nijednu figuru
- Kocka ≠ 6 i sve figure u bazi → preskači potez (nema dostupnih poteza)
- Kocka = 6 → igrač dobija bonus roll (Tabela 1 i 4)

**Nivo 2 — Generisanje i validacija poteza** (provjera dostupnosti polja):
- Kocka = 6 + figura u bazi → generiše se kandidat potez za uvođenje figure
- Aktivna figura + kocka ne prelazi cilj → generiše se kandidat potez za pomjeranje
- Dvije protivničke figure na ciljnom polju → potez nevažeći (blokada, Tabela 2/3)
- Figura u finalnoj stazi bi prešla cilj → potez nevažeći (Tabela 2)

**Nivo 3 — Evaluacija konsekvenci** (provjera rezultata poteza):
- Tačno jedna nezaštićena protivnička figura na ciljnom polju → detekcija eliminacije
- Ciljno polje je zvjezdasto ili startno polje igrača → označava se kao sigurno polje
- Protivnik može dosegnuti trenutnu poziciju bacanjem 1–6 → detekcija opasne zone
- Jedna sopstvena figura već na ciljnom polju → detekcija kreiranja blokade (Tabela 3)

**Sekcija 4.2 — Prioritetna pravila odabira figure:**

| Prioritet | Pravilo |
|-----------|---------|
| 1 (najviši) | Eliminacija protivničke figure |
| 2 | Uvođenje nove figure (kocka=6, baza) |
| 3 | Figura najbliža cilju (finalna staza ili najveći relPos) |
| 4 | Figura u opasnoj zoni (protivnik može dosegnuti) |
| 5 | Pomjeri figuru na sigurno polje (zvjezdasto polje) |
| 6 | Najdalja figura od cilja (najmanji relPos) |
| 7 (najniži) | Nasumičan validan potez |

Finalni potez je onaj sa najmanjim brojem prioriteta.

---

## Šta još treba implementirati

### Sekcija 4.3 — Defanzivni i agresivni modus (CEP modus)
Sistem treba da automatski prebacuje između modusa na osnovu stanja igre:

| Modus | Uslov aktivacije |
|-------|-----------------|
| Agresivni | Protivnik ima ≥ 3 figure na tabli i ≥ 1 figuru blizu cilja (< 10 polja) |
| Defanzivni | Igrač ima ≤ 1 figuru na tabli ili figura je ugrožena od eliminacije |
| Neutralni | Nijedan od gornjih uslova nije ispunjen |

### Sekcija 5 — Praćenje statistika igrača
Tokom partije akumulirati (klasa `StatistikaIgraca` je pripremljena, pravila nedostaju):
- `ukupno_poteza`, `broj_sestica`, `uzastopne_sestice_max`
- `eliminacije_izvedene`, `eliminacije_primljene`
- `figure_u_cilju`, `bonus_roll_iskoristen`, `blokade_kreirane`
- `prosj_udaljenost_od_cilja` (inkrementalna srednja vrijednost)
- `potezi_na_sigurno`, `stil_igre_skor = (eliminacije*2 - potezi_na_sigurno) / ukupno_poteza`

### Sekcija 5.3 — Preporuka poteza zasnovana na statistikama
Prilagoditi prioritete na osnovu historijskih podataka igrača:
- `stil_igre_skor > 0.6` → povećati prioritet eliminacije
- `eliminacije_primljene > 5` → aktivirati defanzivni modus ranije
- `prosj_udaljenost_od_cilja < 8` → fokusirati se isključivo na cilj
- `blokade_kreirane > 2` → preporučiti blokade i bez direktnih prijetnji

### Sekcija 6 — Napredna specifikacija (ocjena 9–10)

**6.1 Backward Chaining — Strateški ciljevi:**
Sistem postavlja strateški cilj (npr. "Dovedi sve 4 figure u cilj u N poteza") i unazad provjerava dostižnost.

**6.2 Complex Event Processing (CEP):**
Detekcija složenih događaja u realnom vremenu:
- Prijetnja eliminacijom — protivnička figura može dosegnuti našu u jednom potezu
- Višestruke sestice — igrač je bacio sesticu dva puta uzastopno
- Protivnik blizu pobjede — protivnik ima 3+ figure u finalnoj stazi
- Blokada protivnika aktivna na ključnom polju

**6.3 Napredna akumulacija:**
- Gustina protivničkih figura u kritičnim zonama (polja 40–52)
- Rizik-skor za svaki potez: (vjerovatnoća eliminacije naše figure) − (vjerovatnoća eliminacije protivnika)
- Historijska analiza obrazaca: koji tip poteza je dovodio do pobjede
