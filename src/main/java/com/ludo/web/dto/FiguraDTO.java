package com.ludo.web.dto;

import com.ludo.model.Figura;

public class FiguraDTO {
    public int id;
    public int vlasnikId;
    public int relativnaPozicija;
    public int apsolutnaPozicija;
    public String status;
    public boolean sigurna;

    public static FiguraDTO from(Figura f) {
        FiguraDTO dto = new FiguraDTO();
        dto.id = f.getId();
        dto.vlasnikId = f.getVlasnikId();
        dto.relativnaPozicija = f.getRelativnaPozicija();
        dto.apsolutnaPozicija = f.getApsolutnaPozicija();
        dto.status = f.getStatus().name();
        dto.sigurna = f.isSigurna();
        return dto;
    }
}
