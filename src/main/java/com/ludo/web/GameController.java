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
@CrossOrigin(origins = "*")
public class GameController {

    private final GameManager gameManager;
    private final DroolsRunner droolsRunner;

    public GameController(GameManager gameManager, DroolsRunner droolsRunner) {
        this.gameManager = gameManager;
        this.droolsRunner = droolsRunner;
    }

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

        if (dice == 6) {
            session.incrementSestice();
        } else {
            session.resetSestice();
        }
        int uzastopneSestice = session.getUzastopneSestice();

        IshodKocke kocka = new IshodKocke(dice, uzastopneSestice);
        DroolsResult result = droolsRunner.run(igracId, session.getFigureList(), kocka, session.getAllStat());
        StanjeIgre stanje = result.stanje;

        if (!stanje.isPreskaciPotez() && stanje.getOdabranaFiguraId() != -1) {
            session.applyMove(stanje.getOdabranaFiguraId(), stanje.getOdabranaPozicija());
        }

        if (session.isGameOver()) {
            session.finalizeGame();
            gameManager.saveStats(session.getAllStat());
        }

        if (stanje.isPreskaciPotez()) {
            session.resetSestice();
            if (!session.isGameOver()) session.advancePlayer();
        } else if (stanje.isBonusRoll()) {
            // same player rolls again — do not advance
        } else {
            if (!session.isGameOver()) session.advancePlayer();
        }

        return buildResponse(igracId, dice, stanje, result, session);
    }

    @PostMapping("/{id}/reset")
    public Map<String, Object> reset(@PathVariable String id) {
        requireSession(id);
        gameManager.removeSession(id);
        gameManager.getSession(id);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("status", "reset");
        resp.put("hint", "Call POST /api/game/new to start a fresh game");
        return resp;
    }

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

        r.prioritet = result.sviPotezi.stream()
                .filter(p -> p.getFiguraId() == stanje.getOdabranaFiguraId() && p.isValidan())
                .mapToInt(Potez::getPrioritet)
                .findFirst()
                .orElse(0);

        r.kandidatPotezi = result.sviPotezi.stream()
                .filter(p -> p.getVlasnikId() == igracId)
                .map(PotezDTO::from)
                .collect(Collectors.toList());

        r.cepEventi = result.dogadjaji.stream()
                .map(DogadjajDTO::from)
                .collect(Collectors.toList());

        r.figure = toFiguraDTOs(session);

        r.statistike = toStatistikeDTOs(session);

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
