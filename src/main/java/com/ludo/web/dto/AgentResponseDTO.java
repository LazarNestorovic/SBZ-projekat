package com.ludo.web.dto;

import java.util.List;

/**
 * Full response for one agent turn.
 * Person B's JS reads every field here to drive the UI.
 */
public class AgentResponseDTO {

    // ---- Move result ----
    public int activePlayer;           // player who just moved
    public int odabranaFiguraId;       // -1 if preskaciPotez or no valid move
    public int novaRelativnaPozicija;  // -1 if no move
    public String razlog;
    public int prioritet;              // 1–8 (or 0 if no move)
    public String modus;               // NEUTRALNI / AGRESIVNI / DEFANZIVNI
    public boolean bonusRoll;
    public boolean preskaciPotez;
    public int dice;

    // ---- All candidate moves (valid + invalid) for board highlighting ----
    public List<PotezDTO> kandidatPotezi;

    // ---- CEP events detected this turn ----
    public List<DogadjajDTO> cepEventi;

    // ---- Stats for all 4 players (updated after this turn) ----
    public List<StatistikaDTO> statistike;

    // ---- Updated board state (all 16 pieces) ----
    public List<FiguraDTO> figure;

    // ---- Next turn info ----
    public int nextPlayer;
    public boolean gameOver;
    public int winnerId;               // -1 if game still running
}
