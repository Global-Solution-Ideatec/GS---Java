package br.com.fiap.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpenAIServiceTest {

    @Mock
    RestTemplate restTemplate;

    @Test
    public void whenApiKeyBlank_thenReturnEmpty() {
        OpenAIService service = new OpenAIService(restTemplate, "");
        Optional<String> opt = service.generateRecommendationExplanation("prompt");
        assertTrue(opt.isEmpty());
    }

    @Test
    public void whenApiKeyPresentAndResponseValid_thenReturnContent() {
        String apiKey = "sk-test";
        OpenAIService service = new OpenAIService(restTemplate, apiKey);

        Map<String, Object> message = Map.of("role", "assistant", "content", "Resposta gerada");
        Map<String, Object> firstChoice = Map.of("message", Map.of("content", "Resposta gerada pelo modelo"));
        Map<String, Object> body = Map.of("choices", List.of(firstChoice));

        when(restTemplate.postForEntity(any(String.class), any(), any(Class.class)))
                .thenReturn(ResponseEntity.ok(body));

        Optional<String> opt = service.generateRecommendationExplanation("prompt");
        assertTrue(opt.isPresent());
        assertEquals("Resposta gerada pelo modelo", opt.get());
    }
}

