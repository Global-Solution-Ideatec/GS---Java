package br.com.fiap.dto;

import br.com.fiap.model.Habilidade;

public class HabilidadeMapper {
    public static HabilidadeDTO toDTO(Habilidade h) {
        if (h == null) return null;
        HabilidadeDTO dto = new HabilidadeDTO();
        dto.setIdHabilidade(h.getIdHabilidade());
        dto.setNmHabilidade(h.getNmHabilidade());
        dto.setDsNivel(h.getDsNivel());
        dto.setIdUsuario(h.getUsuario() != null ? h.getUsuario().getIdUsuario() : null);
        return dto;
    }

    public static Habilidade fromDTO(HabilidadeDTO dto) {
        if (dto == null) return null;
        Habilidade h = new Habilidade();
        h.setIdHabilidade(dto.getIdHabilidade());
        h.setNmHabilidade(dto.getNmHabilidade());
        h.setDsNivel(dto.getDsNivel());
        // usuario association should be set by service/controller when necessary
        return h;
    }
}

