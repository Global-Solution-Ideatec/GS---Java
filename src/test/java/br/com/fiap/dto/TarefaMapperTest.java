package br.com.fiap.dto;

import br.com.fiap.model.Tarefa;
import br.com.fiap.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TarefaMapperTest {

    @Test
    public void toDTO_shouldMapFieldsAndUserIds() {
        Tarefa t = new Tarefa();
        t.setIdTarefa(5L);
        t.setDsTarefa("Implementar feature X");
        t.setDsArea("TI");
        t.setStTarefa("Pendente");

        Usuario gestor = new Usuario(); gestor.setIdUsuario(1L);
        Usuario colab = new Usuario(); colab.setIdUsuario(2L);
        t.setGestor(gestor);
        t.setColaborador(colab);

        TarefaDTO dto = TarefaMapper.toDTO(t);

        assertNotNull(dto);
        assertEquals(5L, dto.getIdTarefa());
        assertEquals("Implementar feature X", dto.getDsTarefa());
        assertEquals("TI", dto.getDsArea());
        assertEquals("Pendente", dto.getStTarefa());
        assertEquals(1L, dto.getIdGestor());
        assertEquals(2L, dto.getIdColaborador());
    }
}

