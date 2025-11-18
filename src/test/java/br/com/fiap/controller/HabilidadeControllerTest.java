package br.com.fiap.controller;

import br.com.fiap.dto.HabilidadeDTO;
import br.com.fiap.model.Habilidade;
import br.com.fiap.model.Usuario;
import br.com.fiap.repository.HabilidadeRepository;
import br.com.fiap.repository.UsuarioRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabilidadeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private HabilidadeRepository habilidadeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private HabilidadeController controller;

    @BeforeEach
    void setup() {
        controller = new HabilidadeController();
        try {
            java.lang.reflect.Field f = HabilidadeController.class.getDeclaredField("habilidadeRepository");
            f.setAccessible(true);
            f.set(controller, habilidadeRepository);

            f = HabilidadeController.class.getDeclaredField("usuarioRepository");
            f.setAccessible(true);
            f.set(controller, usuarioRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createSkill_returns201() throws Exception {
        Habilidade saved = new Habilidade();
        saved.setIdHabilidade(5L);
        saved.setNmHabilidade("Java");

        when(habilidadeRepository.save(any(Habilidade.class))).thenReturn(saved);

        HabilidadeDTO dto = new HabilidadeDTO();
        dto.setNmHabilidade("Java");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/habilidades").contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void getSkill_returns200() throws Exception {
        Habilidade h = new Habilidade();
        h.setIdHabilidade(6L);
        h.setNmHabilidade("Spring");

        when(habilidadeRepository.findById(6L)).thenReturn(Optional.of(h));

        mockMvc.perform(get("/api/habilidades/6"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}

