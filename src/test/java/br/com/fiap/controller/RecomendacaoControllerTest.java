package br.com.fiap.controller;

import br.com.fiap.model.Recomendacao;
import br.com.fiap.repository.RecomendacaoRepository;
import br.com.fiap.service.IAService;
import br.com.fiap.service.MensageriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RecomendacaoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IAService iaService;

    @Mock
    private MensageriaService mensageriaService;

    @Mock
    private RecomendacaoRepository recomendacaoRepository;

    private RecomendacaoController controller;

    @BeforeEach
    void setup() {
        // init controller and inject mocks via reflection
        controller = new RecomendacaoController();
        try {
            java.lang.reflect.Field f = RecomendacaoController.class.getDeclaredField("iaService");
            f.setAccessible(true);
            f.set(controller, iaService);

            f = RecomendacaoController.class.getDeclaredField("mensageriaService");
            f.setAccessible(true);
            f.set(controller, mensageriaService);

            f = RecomendacaoController.class.getDeclaredField("recomendacaoRepository");
            f.setAccessible(true);
            f.set(controller, recomendacaoRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void recomendar_endpoint_returns200_whenRecommendationExists() throws Exception {
        Recomendacao r = new Recomendacao();
        r.setIdRecomendacao(1L);
        r.setDsRecomendacao("Justificativa X");

        when(iaService.recomendarColaborador(anyString())).thenReturn(Optional.of(r));

        mockMvc.perform(get("/api/recomendacao/colaborador").param("area", "TI"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void requestRecommendation_returnsAccepted_whenEnqueued() throws Exception {
        when(mensageriaService.enviarRecomendacaoRequest(anyString())).thenReturn(true);

        String body = objectMapper.writeValueAsString(java.util.Collections.singletonMap("area", "TI"));

        mockMvc.perform(post("/api/recomendacao/request")
                .contentType("application/json")
                .content(body))
                .andExpect(status().isAccepted());
    }

    @Test
    void requestRecommendation_fallbacksToSynchronous_whenQueueUnavailable() throws Exception {
        Recomendacao r = new Recomendacao();
        r.setIdRecomendacao(5L);
        r.setDsRecomendacao("fallback");

        when(mensageriaService.enviarRecomendacaoRequest(anyString())).thenReturn(false);
        when(iaService.recomendarColaborador(anyString())).thenReturn(Optional.of(r));

        String body = objectMapper.writeValueAsString(java.util.Collections.singletonMap("area", "TI"));

        mockMvc.perform(post("/api/recomendacao/request")
                .contentType("application/json")
                .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}
