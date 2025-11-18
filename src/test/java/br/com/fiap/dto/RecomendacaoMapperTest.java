 package br.com.fiap.dto;

import br.com.fiap.model.Recomendacao;
import br.com.fiap.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class RecomendacaoMapperTest {

    @Test
    public void toDTO_shouldMapFields() {
        Recomendacao r = new Recomendacao();
        r.setIdRecomendacao(15L);
        Usuario u = new Usuario(); u.setIdUsuario(3L);
        r.setUsuario(u);
        r.setDsRecomendacao("Teste de recomendacao");
        r.setDtRecomendacao(LocalDateTime.now());
        r.setTpRecomendacao("TESTE");

        RecomendacaoDTO dto = RecomendacaoMapper.toDTO(r);

        assertNotNull(dto);
        assertEquals(15L, dto.getIdRecomendacao());
        assertEquals(3L, dto.getIdUsuario());
        assertEquals("Teste de recomendacao", dto.getDsRecomendacao());
        assertEquals("TESTE", dto.getTpRecomendacao());
        assertNotNull(dto.getDtRecomendacao());
    }
}

