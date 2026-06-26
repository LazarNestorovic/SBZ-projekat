package com.ludo.web.dto;

import com.ludo.model.Potez;

public class PotezDTO {
    public int figuraId;
    public int vlasnikId;
    public int novaRelativnaPozicija;
    public int novaApsolutnaPozicija;
    public int prioritet;
    public boolean validan;
    public boolean mozeEliminisati;
    public boolean naSignurnoPlje;
    public boolean izOpasneZone;
    public boolean novaFigura;
    public double rizikSkor;
    public String razlog;

    public static PotezDTO from(Potez p) {
        PotezDTO dto = new PotezDTO();
        dto.figuraId = p.getFiguraId();
        dto.vlasnikId = p.getVlasnikId();
        dto.novaRelativnaPozicija = p.getNovaRelativnaPozicija();
        dto.novaApsolutnaPozicija = p.getNovaApsolutnaPozicija();
        dto.prioritet = p.getPrioritet();
        dto.validan = p.isValidan();
        dto.mozeEliminisati = p.isMozeEliminisati();
        dto.naSignurnoPlje = p.isNaSignurnoPlje();
        dto.izOpasneZone = p.isIzOpasneZone();
        dto.novaFigura = p.isNovaFigura();
        dto.rizikSkor = p.getRizikSkor();
        dto.razlog = p.getRazlog();
        return dto;
    }
}
