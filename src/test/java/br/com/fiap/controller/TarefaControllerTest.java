package br.com.fiap.controller;

import br.com.fiap.dto.TarefaCreateDTO;
import br.com.fiap.model.Tarefa;
import br.com.fiap.model.Usuario;
import br.com.fiap.repository.UsuarioRepository;
import br.com.fiap.service.TarefaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
class TarefaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TarefaService tarefaService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private TarefaController controller;

    @BeforeEach
    void setup() {
        controller = new TarefaController();
        try {
            java.lang.reflect.Field f = TarefaController.class.getDeclaredField("tarefaService");
            f.setAccessible(true);
            f.set(controller, tarefaService);

            f = TarefaController.class.getDeclaredField("usuarioRepository");
            f.setAccessible(true);
            f.set(controller, usuarioRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createTask_returns201() throws Exception {
        Tarefa saved = new Tarefa();
        saved.setIdTarefa(20L);
        saved.setDsTarefa("Do something");

        when(tarefaService.create(any(Tarefa.class))).thenReturn(saved);

        TarefaCreateDTO dto = new TarefaCreateDTO();
        dto.setDsTarefa("Do something");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/tarefas").contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void getTask_returns200() throws Exception {
        Tarefa t = new Tarefa();
        t.setIdTarefa(21L);
        t.setDsTarefa("Task");

        when(tarefaService.findById(21L)).thenReturn(Optional.of(t));

        mockMvc.perform(get("/api/tarefas/21"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void updateTask_returns200() throws Exception {
        Tarefa existing = new Tarefa();
        existing.setIdTarefa(22L);
        existing.setDsTarefa("Old");

        Tarefa updated = new Tarefa();
        updated.setIdTarefa(22L);
        updated.setDsTarefa("New");

        when(tarefaService.findById(22L)).thenReturn(Optional.of(existing));
        when(tarefaService.update(any(Tarefa.class))).thenReturn(updated);

        TarefaCreateDTO dto = new TarefaCreateDTO();
        dto.setDsTarefa("New");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/tarefas/22").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void deleteTask_returns204() throws Exception {
        // delete does not return body; just ensure endpoint responds
        mockMvc.perform(delete("/api/tarefas/99"))
                .andExpect(status().isNoContent());
    }
}

