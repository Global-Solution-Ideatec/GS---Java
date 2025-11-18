package br.com.fiap.dto;

import br.com.fiap.model.Recomendacao;

public class RecomendacaoMapper {
    public static RecomendacaoDTO toDTO(Recomendacao r) {
        if (r == null) return null;
        RecomendacaoDTO dto = new RecomendacaoDTO();
        dto.setIdRecomendacao(r.getIdRecomendacao());
        dto.setIdUsuario(r.getUsuario() != null ? r.getUsuario().getIdUsuario() : null);
        dto.setDsRecomendacao(r.getDsRecomendacao());
        dto.setDtRecomendacao(r.getDtRecomendacao());
        dto.setTpRecomendacao(r.getTpRecomendacao());
        dto.setFatores(r.getFatores());
        return dto;
    }
}
