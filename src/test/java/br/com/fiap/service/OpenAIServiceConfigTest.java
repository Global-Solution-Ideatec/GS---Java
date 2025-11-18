// ...existing code...
package br.com.fiap.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class OpenAIServiceConfigTest {

    @Test
    void whenApiKeyBlank_andNotEnabled_thenServiceDisabled_andReturnsEmpty() {
        var restTemplate = Mockito.mock(org.springframework.web.client.RestTemplate.class);
        OpenAIService svc = new OpenAIService(restTemplate, "");
        // ensure not enabled by default
        assertFalse(svc.isEnabled());
        Optional<String> opt = svc.generateRecommendationExplanation("algum prompt");
        assertTrue(opt.isEmpty());
    }

    @Test
    void whenOpenaiEnabledProperty_present_thenServiceEnabled_andAsyncReturnsValue() throws Exception {
        var restTemplate = Mockito.mock(org.springframework.web.client.RestTemplate.class);
        OpenAIService svc = new OpenAIService(restTemplate, "");

        // prepare mock response body matching expected structure
        Map<String, Object> message = Map.of("message", Map.of("content", "Resposta assíncrona"));
        Map<String, Object> first = message;
        Map<String, Object> body = Map.of("choices", List.of(first));

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(body));

        // configure the service as if Spring injected the property and task executor
        Executor exec = Executors.newSingleThreadExecutor();
        svc.configure(true, exec);

        assertTrue(svc.isEnabled());

        var future = svc.generateRecommendationExplanationAsync("prompt async");
        Optional<String> result = future.get(5, TimeUnit.SECONDS);
        assertTrue(result.isPresent());
        assertEquals("Resposta assíncrona", result.get());
    }
}
// ...existing code...
