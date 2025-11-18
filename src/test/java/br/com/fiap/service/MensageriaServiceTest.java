 package br.com.fiap.service;

import br.com.fiap.config.RabbitConfig;
import br.com.fiap.model.Recomendacao;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MensageriaServiceTest {

    @Test
    void enviarRecomendacaoRequest_withRabbitTemplate_publishesToQueue() throws Exception {
        RabbitTemplate rabbit = Mockito.mock(RabbitTemplate.class);
        MensageriaService svc = new MensageriaService();

        // set rabbitTemplate via reflection
        Field f = MensageriaService.class.getDeclaredField("rabbitTemplate");
        f.setAccessible(true);
        f.set(svc, rabbit);

        // set exchange field
        Field ex = MensageriaService.class.getDeclaredField("exchange");
        ex.setAccessible(true);
        ex.set(svc, "test-exchange");

        boolean result = svc.enviarRecomendacaoRequest("TI");
        assertTrue(result);

        // verify convertAndSend called with exchange and REQUEST_ROUTING_KEY
        verify(rabbit, times(1)).convertAndSend(eq("test-exchange"), eq(RabbitConfig.REQUEST_ROUTING_KEY), eq("TI"));
    }

    @Test
    void enviarRecomendacaoAsync_withRabbitTemplate_callsConvertAndSend() throws Exception {
        RabbitTemplate rabbit = Mockito.mock(RabbitTemplate.class);
        MensageriaService svc = new MensageriaService();

        // set rabbitTemplate and properties
        Field f = MensageriaService.class.getDeclaredField("rabbitTemplate");
        f.setAccessible(true);
        f.set(svc, rabbit);

        Field ex = MensageriaService.class.getDeclaredField("exchange");
        ex.setAccessible(true);
        ex.set(svc, "test-exchange");

        Field rk = MensageriaService.class.getDeclaredField("routingKey");
        rk.setAccessible(true);
        rk.set(svc, "test.routing");

        Recomendacao r = new Recomendacao();
        r.setIdRecomendacao(123L);

        // call method (it's async annotated but synchronous in test invocation)
        svc.enviarRecomendacaoAsync(r);

        // verify convertAndSend called
        verify(rabbit, timeout(1000).times(1)).convertAndSend(eq("test-exchange"), eq("test.routing"), eq(r));
    }
}

