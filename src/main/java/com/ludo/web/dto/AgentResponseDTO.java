package com.ludo.web.dto;

import java.util.List;

public class AgentResponseDTO {

    public int activePlayer;
    public int odabranaFiguraId;
    public int novaRelativnaPozicija;
    public String razlog;
    public int prioritet;
    public String modus;
    public boolean bonusRoll;
    public boolean preskaciPotez;
    public int dice;

    public List<PotezDTO> kandidatPotezi;

    public List<DogadjajDTO> cepEventi;

    public List<StatistikaDTO> statistike;

    public List<FiguraDTO> figure;

    public int nextPlayer;
    public boolean gameOver;
    public int winnerId;
}
