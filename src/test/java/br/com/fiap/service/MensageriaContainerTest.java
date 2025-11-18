package br.com.fiap.service;

import br.com.fiap.config.RabbitConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@EnabledIfSystemProperty(named = "runIntegrationTests", matches = "true")
public class MensageriaContainerTest {

    private static final RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.11-management");

    @DynamicPropertySource
    static void rabbitProperties(DynamicPropertyRegistry registry) {
        rabbit.start();
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
    }

    @Autowired
    private MensageriaService mensageriaService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @BeforeAll
    static void beforeAll() {
        // ensure container is started by DynamicPropertySource
    }

    @AfterAll
    static void afterAll() {
        if (rabbit != null && rabbit.isRunning()) {
            rabbit.stop();
        }
    }

    @Test
    void enviarRecomendacaoRequest_publishesToQueue_and_listenerCanConsume() throws Exception {
        // send a request via MensageriaService
        boolean ok = mensageriaService.enviarRecomendacaoRequest("TI");
        // if Rabbit is available mensageriaService should return true
        assertThat(ok).isTrue();

        // Try to receive the message directly from the queue (primary verification)
        Object msg = null;
        // Retry for a few times to allow delivery
        for (int i = 0; i < 10 && msg == null; i++) {
            msg = rabbitTemplate.receiveAndConvert(RabbitConfig.REQUEST_QUEUE);
            if (msg == null) Thread.sleep(200);
        }

        assertThat(msg).isNotNull();
        assertThat(msg.toString()).contains("TI");
    }
}

