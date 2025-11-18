package br.com.fiap.service;

import br.com.fiap.config.RabbitConfig;
import br.com.fiap.model.Recomendacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MensageriaService {

    private static final Logger logger = LoggerFactory.getLogger(MensageriaService.class);

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.exchange:recomendacao-exchange}")
    private String exchange;

    @Value("${app.rabbit.routingKey:recomendacao.key}")
    private String routingKey;

    @Async("taskExecutor")
    public void enviarRecomendacaoAsync(Recomendacao r) {
        // Se RabbitTemplate estiver disponível, publica na fila; caso contrário, faz fallback local (simulado)
        try {
            if (rabbitTemplate != null) {
                logger.info("Publicando recomendação no RabbitMQ (exchange={}, routingKey={})", exchange, routingKey);
                rabbitTemplate.convertAndSend(exchange, routingKey, r);
                logger.info("Recomendação publicada no RabbitMQ (id={})", r.getIdRecomendacao());
            } else {
                // Simulação antiga
                logger.info("RabbitTemplate não disponível — processando localmente: {}", r.getDsRecomendacao());
                Thread.sleep(500); // simula latência
                logger.info("Recomendação processada (simulada) para usuário id={}", r.getUsuario() != null ? r.getUsuario().getIdUsuario() : null);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.error("Envio de recomendação interrompido", ie);
        } catch (Exception e) {
            logger.error("Erro ao enviar recomendação", e);
        }
    }

    /**
     * Envia uma requisição de recomendação (apenas a área) para a fila de requests.
     * Se Rabbit não estiver disponível, retorna false para indicar fallback necessário.
     */
    public boolean enviarRecomendacaoRequest(String area) {
        try {
            if (rabbitTemplate != null) {
                logger.info("Publicando request de recomendação no RabbitMQ (exchange={}, routingKey={})", exchange, RabbitConfig.REQUEST_ROUTING_KEY);
                rabbitTemplate.convertAndSend(exchange, RabbitConfig.REQUEST_ROUTING_KEY, area);
                logger.info("Request publicado para area={}", area);
                return true;
            } else {
                logger.warn("RabbitTemplate não disponível — request será processado de forma síncrona para area={}", area);
                return false;
            }
        } catch (Exception e) {
            logger.error("Erro ao enviar request de recomendação", e);
            return false;
        }
    }
}
