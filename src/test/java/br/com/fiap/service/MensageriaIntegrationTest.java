package br.com.fiap.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Integration test for Mensageria using Testcontainers.
 * Disabled by default to avoid requiring Docker for local unit test runs.
 */
@Disabled("Integration test - requires Docker/Testcontainers; enable when running E2E with Docker")
public class MensageriaIntegrationTest {

    @Test
    void placeholder() {
        // Placeholder test kept to satisfy IDE/test discovery; real integration test is intentionally disabled.
    }
}
