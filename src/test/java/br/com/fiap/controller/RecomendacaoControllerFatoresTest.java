package br.com.fiap.controller;

import br.com.fiap.model.Recomendacao;
import br.com.fiap.repository.RecomendacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RecomendacaoControllerFatoresTest {

    private MockMvc mockMvc;

    @Mock
    private RecomendacaoRepository recomendacaoRepository;

    private RecomendacaoController controller;

    @BeforeEach
    void setup() {
        controller = new RecomendacaoController();
        try {
            java.lang.reflect.Field f = RecomendacaoController.class.getDeclaredField("recomendacaoRepository");
            f.setAccessible(true);
            f.set(controller, recomendacaoRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getFatores_returns200_withContent() throws Exception {
        Recomendacao r = new Recomendacao();
        r.setIdRecomendacao(10L);
        r.setFatores("fatores explicativos aqui");

        when(recomendacaoRepository.findById(10L)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/api/recomendacao/10/fatores"))
                .andExpect(status().isOk())
                .andExpect(content().string("fatores explicativos aqui"));
    }

    @Test
    void getFatores_returns204_whenEmpty() throws Exception {
        Recomendacao r = new Recomendacao();
        r.setIdRecomendacao(11L);
        r.setFatores(null);

        when(recomendacaoRepository.findById(11L)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/api/recomendacao/11/fatores"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getFatores_returns404_whenNotFound() throws Exception {
        when(recomendacaoRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recomendacao/999/fatores"))
                .andExpect(status().isNotFound());
    }
}

