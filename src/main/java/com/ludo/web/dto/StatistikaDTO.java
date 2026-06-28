package com.ludo.web.dto;

import com.ludo.model.StatistikaIgraca;

public class StatistikaDTO {
    public int igracId;
    public int ukupnoPoteza;
    public int eliminacijeIzvedene;
    public int eliminacijePrimljene;
    public int figureUCilju;
    public int blokadeKreirane;
    public double stilIgreSkor;
    public String omiljeniStil;
    public int totalPartija;
    public int totalPobjeda;
    public double winRate;
    public int najduzaPartija;
    public int najkracaPartija;
    public double prosjecnoTrajanje;

    public static StatistikaDTO from(StatistikaIgraca s) {
        StatistikaDTO dto = new StatistikaDTO();
        dto.igracId = s.getIgracId();
        dto.ukupnoPoteza = s.getUkupnoPoteza();
        dto.eliminacijeIzvedene = s.getEliminacijeIzvedene();
        dto.eliminacijePrimljene = s.getEliminacijePrimljene();
        dto.figureUCilju = s.getFigureUCilju();
        dto.blokadeKreirane = s.getBlokadeKreirane();
        dto.stilIgreSkor = s.getStilIgreSkor();
        dto.omiljeniStil = s.getOmiljeniStil();
        dto.totalPartija = s.getTotalPartija();
        dto.totalPobjeda = s.getTotalPobjeda();
        dto.winRate = s.getWinRate();
        dto.najduzaPartija = s.getNajduzaPartija();
        dto.najkracaPartija = s.getNajkracaPartija();
        dto.prosjecnoTrajanje = s.getProsjecnoTrajanje();
        return dto;
    }
}
