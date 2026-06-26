package com.ludo.web.dto;

import com.ludo.model.DogadjajIgre;

public class DogadjajDTO {
    public String tip;
    public int igracId;
    public int figuraId;
    public int pozicija;

    public static DogadjajDTO from(DogadjajIgre d) {
        DogadjajDTO dto = new DogadjajDTO();
        dto.tip = d.getTip().name();
        dto.igracId = d.getIgracId();
        dto.figuraId = d.getFiguraId();
        dto.pozicija = d.getPozicija();
        return dto;
    }
}
