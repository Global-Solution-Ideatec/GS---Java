package br.com.fiap.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class OpenAIServiceUnitTest {

    @Test
    void generateRecommendationExplanation_shouldReturnContent_whenOpenAIResponds() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        String apiKey = "test-key";
        OpenAIService svc = new OpenAIService(restTemplate, apiKey);

        Map<String, Object> message = Map.of("message", Map.of("content", "Esta é a explicação gerada"));
        Map<String, Object> first = message;
        Map<String, Object> body = Map.of("choices", List.of(first));

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(body));

        Optional<String> opt = svc.generateRecommendationExplanation("prompt qualquer");
        assertTrue(opt.isPresent());
        assertEquals("Esta é a explicação gerada", opt.get());
    }

    @Test
    void generateRecommendationExplanation_shouldReturnEmpty_whenNoApiKey() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        OpenAIService svc = new OpenAIService(restTemplate, "");
        Optional<String> opt = svc.generateRecommendationExplanation("x");
        assertTrue(opt.isEmpty());
    }
}

