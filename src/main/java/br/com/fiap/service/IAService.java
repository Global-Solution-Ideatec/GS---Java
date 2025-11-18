package br.com.fiap.service;

import br.com.fiap.model.Recomendacao;
import br.com.fiap.model.Usuario;
import br.com.fiap.repository.HabilidadeRepository;
import br.com.fiap.repository.RecomendacaoRepository;
import br.com.fiap.repository.TarefaRepository;
import br.com.fiap.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class IAService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private HabilidadeRepository habilidadeRepository;

    @Autowired
    private RecomendacaoRepository recomendacaoRepository;

    @Autowired
    private MensageriaService mensageriaService;

    @Autowired
    private OpenAIService openAIService;

    @Cacheable(value = "usuariosByType", key = "#tp")
    public List<Usuario> findUsuariosByTipo(String tp) {
        return usuarioRepository.findByTpUsuario(tp);
    }

    /**
     * Recomenda um colaborador para executar uma tarefa na área informada.
     * Critério simples: menor quantidade de tarefas abertas; empate por maior número de habilidades.
     * Retorna a Recomendacao persistida (opcional). Se houver integração com OpenAI, atualiza a justificativa assincronamente.
     */
    @Transactional
    public Optional<Recomendacao> recomendarColaborador(String area) {
        List<Usuario> colaboradores = findUsuariosByTipo("C");
        if (colaboradores == null || colaboradores.isEmpty()) {
            return Optional.empty();
        }

        Usuario escolhido = colaboradores.stream()
                .min(Comparator.comparingLong((Usuario u) -> tarefaRepository.countOpenTasksByColaborador(u.getIdUsuario()))
                        .thenComparing((Usuario u) -> -habilidadeRepository.findByUsuarioIdUsuario(u.getIdUsuario()).size()))
                .orElse(null);

        if (escolhido != null) {
            long openTasks = tarefaRepository.countOpenTasksByColaborador(escolhido.getIdUsuario());
            int skills = habilidadeRepository.findByUsuarioIdUsuario(escolhido.getIdUsuario()).size();

            String fallbackMensagem = String.format("Recomendado %s (id=%d) para área '%s' - tarefas abertas=%d - skills=%d",
                    escolhido.getNmUsuario(), escolhido.getIdUsuario(), area, openTasks, skills);

            Recomendacao r = new Recomendacao();
            r.setUsuario(escolhido);
            r.setTpRecomendacao("REPARTICAO_TAREFA");

            // Initially set fallback message (will be updated asynchronously if IA gerar)
            r.setDsRecomendacao(fallbackMensagem);
            r.setFatores(fallbackMensagem); // gravar fallback também em 'fatores'

            Recomendacao saved = recomendacaoRepository.save(r);

            // enqueue notification (initial)
            mensageriaService.enviarRecomendacaoAsync(saved);

            // Prepare prompt and call OpenAI asynchronously; update saved recommendation if result present
            try {
                String prompt = String.format("Você é um assistente que ajuda a decidir alocação de tarefas.\nContexto: área=%s; colaborador=%s (id=%d); tarefas abertas=%d; habilidades=%d.\nExplique de forma concisa porque esse colaborador é recomendado e sugira próximos passos para o gestor.",
                        area, escolhido.getNmUsuario(), escolhido.getIdUsuario(), openTasks, skills);

                openAIService.generateRecommendationExplanationAsync(prompt).thenAccept(optGen -> {
                    try {
                        if (optGen != null && optGen.isPresent()) {
                            // armazenar explicação detalhada em 'fatores' e atualizar justificativa curta também
                            saved.setFatores(optGen.get());
                            saved.setDsRecomendacao(optGen.get());
                            recomendacaoRepository.save(saved);
                            // re-enqueue to notify consumers about updated recommendation
                            mensageriaService.enviarRecomendacaoAsync(saved);
                        }
                    } catch (Exception ex) {
                        // safely ignore async failures, log if necessary
                    }
                });

            } catch (Exception ex) {
                // ignore async scheduling failures, fallback already saved
            }

            return Optional.of(saved);
        }

        return Optional.empty();
    }
}
