package com.ludo.web;

import com.ludo.game.DroolsRunner;
import com.ludo.game.DroolsRunner.DroolsResult;
import com.ludo.game.GameManager;
import com.ludo.game.GameSession;
import com.ludo.model.IshodKocke;
import com.ludo.model.Potez;
import com.ludo.model.StanjeIgre;
import com.ludo.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")   // allow Person B's dev server to call the API
public class GameController {

    private final GameManager gameManager;
    private final DroolsRunner droolsRunner;

    public GameController(GameManager gameManager, DroolsRunner droolsRunner) {
        this.gameManager = gameManager;
        this.droolsRunner = droolsRunner;
    }

    // ----------------------------------------------------------------
    // POST /api/game/new
    // Creates a fresh game and returns its ID and initial board state.
    // ----------------------------------------------------------------
    @PostMapping("/new")
    public Map<String, Object> newGame() {
        String gameId = gameManager.createGame();
        GameSession session = gameManager.getSession(gameId);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("gameId", gameId);
        resp.put("currentPlayer", session.getCurrentPlayer());
        resp.put("figure", toFiguraDTOs(session));
        resp.put("statistike", toStatistikeDTOs(session));
        return resp;
    }

    // ----------------------------------------------------------------
    // GET /api/game/{id}
    // Returns the current board state (used by Person B to re-sync).
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    public Map<String, Object> getState(@PathVariable String id) {
        GameSession session = requireSession(id);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("gameId", id);
        resp.put("currentPlayer", session.getCurrentPlayer());
        resp.put("gameOver", session.isGameOver());
        resp.put("winnerId", session.getWinnerId());
        resp.put("figure", toFiguraDTOs(session));
        resp.put("statistike", toStatistikeDTOs(session));
        return resp;
    }

    // ----------------------------------------------------------------
    // POST /api/game/{id}/potez
    // Main endpoint: receives dice roll, runs Drools, applies move.
    // ----------------------------------------------------------------
    @PostMapping("/{id}/potez")
    public AgentResponseDTO potez(@PathVariable String id,
                                  @RequestBody PotezRequestDTO req) {
        GameSession session = requireSession(id);

        if (session.isGameOver()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Igra je zavrsena — winner: " + session.getWinnerId());
        }

        int igracId = session.getCurrentPlayer();
        int dice = req.getDice();
        if (dice < 1 || dice > 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dice mora biti 1–6");
        }

        // --- Update consecutive-sixes counter BEFORE running Drools ---
        if (dice == 6) {
            session.incrementSestice();
        } else {
            session.resetSestice();
        }
        int uzastopneSestice = session.getUzastopneSestice();

        // --- Run the rule engine ---
        IshodKocke kocka = new IshodKocke(dice, uzastopneSestice);
        DroolsResult result = droolsRunner.run(igracId, session.getFigureList(), kocka, session.getAllStat());
        StanjeIgre stanje = result.stanje;

        // --- Apply the chosen move to the live game state ---
        if (!stanje.isPreskaciPotez() && stanje.getOdabranaFiguraId() != -1) {
            session.applyMove(stanje.getOdabranaFiguraId(), stanje.getOdabranaPozicija());
        }

        // --- Finalize stats when game ends (losers' zavrsiPartiju not called in checkWin) ---
        if (session.isGameOver()) {
            session.finalizeGame();
            gameManager.saveStats(session.getAllStat());
        }

        // --- Advance turn ---
        // Three sixes: penalty — pass turn, reset counter
        if (stanje.isPreskaciPotez()) {
            session.resetSestice();
            if (!session.isGameOver()) session.advancePlayer();
        } else if (stanje.isBonusRoll()) {
            // Same player rolls again — do NOT advance, counter stays
        } else {
            // Normal end of turn
            if (!session.isGameOver()) session.advancePlayer();
        }

        // --- Build response ---
        return buildResponse(igracId, dice, stanje, result, session);
    }

    // ----------------------------------------------------------------
    // POST /api/game/{id}/reset
    // Replaces the session with a fresh game (same ID).
    // ----------------------------------------------------------------
    @PostMapping("/{id}/reset")
    public Map<String, Object> reset(@PathVariable String id) {
        requireSession(id);                   // 404 if it never existed
        gameManager.removeSession(id);
        gameManager.getSession(id);           // can't reuse — create via /new
        // Return a redirect hint to the client
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("status", "reset");
        resp.put("hint", "Call POST /api/game/new to start a fresh game");
        return resp;
    }

    // ================================================================
    // Private helpers
    // ================================================================

    private GameSession requireSession(String id) {
        GameSession s = gameManager.getSession(id);
        if (s == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Igra '" + id + "' ne postoji");
        return s;
    }

    private AgentResponseDTO buildResponse(int igracId, int dice,
                                           StanjeIgre stanje,
                                           DroolsResult result,
                                           GameSession session) {
        AgentResponseDTO r = new AgentResponseDTO();
        r.activePlayer = igracId;
        r.dice = dice;
        r.odabranaFiguraId = stanje.getOdabranaFiguraId();
        r.novaRelativnaPozicija = stanje.getOdabranaPozicija();
        r.razlog = stanje.getOdabraniRazlog();
        r.modus = stanje.getModus().name();
        r.bonusRoll = stanje.isBonusRoll();
        r.preskaciPotez = stanje.isPreskaciPotez();

        // Find the selected Potez to get its priority
        r.prioritet = result.sviPotezi.stream()
                .filter(p -> p.getFiguraId() == stanje.getOdabranaFiguraId() && p.isValidan())
                .mapToInt(Potez::getPrioritet)
                .findFirst()
                .orElse(0);

        // All candidate moves (Person B uses this to highlight valid squares)
        r.kandidatPotezi = result.sviPotezi.stream()
                .filter(p -> p.getVlasnikId() == igracId)
                .map(PotezDTO::from)
                .collect(Collectors.toList());

        // CEP events detected this turn
        r.cepEventi = result.dogadjaji.stream()
                .map(DogadjajDTO::from)
                .collect(Collectors.toList());

        // Updated board (all 16 pieces with new positions)
        r.figure = toFiguraDTOs(session);

        // Stats for all 4 players
        r.statistike = toStatistikeDTOs(session);

        // Next-turn info
        r.nextPlayer = session.getCurrentPlayer();
        r.gameOver = session.isGameOver();
        r.winnerId = session.getWinnerId();

        return r;
    }

    private List<FiguraDTO> toFiguraDTOs(GameSession session) {
        return session.getFigureList().stream()
                .map(FiguraDTO::from)
                .collect(Collectors.toList());
    }

    private List<StatistikaDTO> toStatistikeDTOs(GameSession session) {
        List<StatistikaDTO> list = new ArrayList<>();
        for (int p = 0; p < 4; p++) {
            list.add(StatistikaDTO.from(session.getStat(p)));
        }
        return list;
    }
}
