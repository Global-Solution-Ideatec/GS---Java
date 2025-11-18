package br.com.fiap.service;

import br.com.fiap.model.Recomendacao;
import br.com.fiap.repository.RecomendacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RecomendacaoListener {

    private static final Logger logger = LoggerFactory.getLogger(RecomendacaoListener.class);

    private final RecomendacaoRepository repository;
    private final IAService iaService;

    public RecomendacaoListener(RecomendacaoRepository repository, IAService iaService) {
        this.repository = repository;
        this.iaService = iaService;
    }

    @RabbitListener(queues = "recomendacao-queue")
    public void receive(Recomendacao r) {
        // Aqui você pode processar a recomendação: enviar email, notificação, etc.
        logger.info("Recomendacao recebida no listener: id={} userId={} ", r.getIdRecomendacao(), r.getUsuario() != null ? r.getUsuario().getIdUsuario() : null);
        // Exemplo: marcar como processada ou persistir log adicional
        // Neste MVP apenas logamos
    }

    // Novo listener para requisições (mensagens com a área como payload)
    @RabbitListener(queues = "recomendacao-request-queue")
    public void receiveRequest(String area) {
        logger.info("Request de recomendação recebida para área={}", area);
        try {
            Optional<Recomendacao> opt = iaService.recomendarColaborador(area);
            if (opt.isPresent()) {
                Recomendacao saved = opt.get();
                logger.info("Recomendação gerada assíncronamente: id={} usuarioId={}", saved.getIdRecomendacao(), saved.getUsuario() != null ? saved.getUsuario().getIdUsuario() : null);
            } else {
                logger.warn("Nenhuma recomendação gerada para área={}", area);
            }
        } catch (Exception e) {
            logger.error("Erro ao processar request de recomendação para área=" + area, e);
        }
    }
}
