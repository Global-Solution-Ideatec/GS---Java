package br.com.fiap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

@Service
public class OpenAIService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    private final RestTemplate restTemplate;
    private final String apiKey;
    // whether OpenAI features are enabled (either via property or presence of API key)
    private volatile boolean enabled = false;
    // executor used for async calls; may be null during unit tests that create service manually
    private Executor taskExecutor;

    public OpenAIService(RestTemplate restTemplate, @Value("${openai.api.key:}") String apiKey) {
        this.restTemplate = restTemplate;
        // if property is not set, try environment variable OPENAI_API_KEY to make docker usage simpler
        if (apiKey == null || apiKey.isBlank()) {
            String env = System.getenv("OPENAI_API_KEY");
            this.apiKey = env != null ? env : "";
            logger.debug("OpenAI API key loaded from environment: {}", (this.apiKey.isBlank() ? "<empty>" : "[masked]"));
        } else {
            this.apiKey = apiKey;
            logger.debug("OpenAI API key loaded from property (masked)");
        }
        // default enabled if key present; further configuration may override after bean creation
        this.enabled = !(this.apiKey == null || this.apiKey.isBlank());
    }

    // optional autowired configuration: if an explicit property "openai.enabled" is provided it will
    // enable the feature even when apiKey is not set; taskExecutor is injected for async usage.
    @Autowired(required = false)
    public void configure(@Value("${openai.enabled:false}") boolean openaiEnabled,
                          @Qualifier("taskExecutor") Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
        this.enabled = this.enabled || openaiEnabled;
        logger.debug("OpenAI enabled (after config): {}", this.enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    @SuppressWarnings("unchecked")
    public Optional<String> generateRecommendationExplanation(String prompt) {
        if (!enabled) {
            logger.debug("OpenAI integration is disabled, skipping call");
            return Optional.empty();
        }

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", List.of(message));
        body.put("max_tokens", 300);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                Map<String, Object> b = (Map<String, Object>) resp.getBody();
                Object choicesObj = b.get("choices");
                if (choicesObj instanceof List) {
                    List<?> choices = (List<?>) choicesObj;
                    if (!choices.isEmpty() && choices.get(0) instanceof Map) {
                        Map<?, ?> first = (Map<?, ?>) choices.get(0);
                        Object messageRespObj = first.get("message");
                        if (messageRespObj instanceof Map) {
                            Map<?, ?> messageResp = (Map<?, ?>) messageRespObj;
                            Object contentObj = messageResp.get("content");
                            if (contentObj instanceof String) {
                                String content = ((String) contentObj).trim();
                                return Optional.of(content);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // falha segura: log e retornar empty
            logger.error("OpenAI call failed", e);
        }

        return Optional.empty();
    }

    public CompletableFuture<Optional<String>> generateRecommendationExplanationAsync(String prompt) {
        Executor exec = this.taskExecutor != null ? this.taskExecutor : ForkJoinPool.commonPool();
        return CompletableFuture.supplyAsync(() -> generateRecommendationExplanation(prompt), exec);
    }
}
