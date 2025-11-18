package br.com.fiap.dto;

import br.com.fiap.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioMapperTest {

    @Test
    public void toDTO_shouldMapFields() {
        Usuario u = new Usuario();
        u.setIdUsuario(10L);
        u.setNmUsuario("Teste");
        u.setDsEmail("teste@example.com");
        u.setTpUsuario("C");
        u.setStAtivo("S");

        UsuarioDTO dto = UsuarioMapper.toDTO(u);

        assertNotNull(dto);
        assertEquals(10L, dto.getIdUsuario());
        assertEquals("Teste", dto.getNmUsuario());
        assertEquals("teste@example.com", dto.getDsEmail());
        assertEquals("C", dto.getTpUsuario());
        assertEquals("S", dto.getStAtivo());
    }
}

