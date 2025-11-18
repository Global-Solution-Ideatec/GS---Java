package br.com.fiap.service;

import br.com.fiap.model.Recomendacao;
import br.com.fiap.model.Usuario;
import br.com.fiap.model.Habilidade;
import br.com.fiap.repository.HabilidadeRepository;
import br.com.fiap.repository.RecomendacaoRepository;
import br.com.fiap.repository.TarefaRepository;
import br.com.fiap.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IAServiceUnitTest {

    private UsuarioRepository usuarioRepository;
    private TarefaRepository tarefaRepository;
    private HabilidadeRepository habilidadeRepository;
    private RecomendacaoRepository recomendacaoRepository;
    private MensageriaService mensageriaService;
    private OpenAIService openAIService;
    private IAService iaService;

    @BeforeEach
    void setup() {
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        tarefaRepository = Mockito.mock(TarefaRepository.class);
        habilidadeRepository = Mockito.mock(HabilidadeRepository.class);
        recomendacaoRepository = Mockito.mock(RecomendacaoRepository.class);
        mensageriaService = Mockito.mock(MensageriaService.class);
        openAIService = Mockito.mock(OpenAIService.class);

        iaService = new IAService();
        // inject mocks via reflection
        try {
            java.lang.reflect.Field f;
            f = IAService.class.getDeclaredField("usuarioRepository"); f.setAccessible(true); f.set(iaService, usuarioRepository);
            f = IAService.class.getDeclaredField("tarefaRepository"); f.setAccessible(true); f.set(iaService, tarefaRepository);
            f = IAService.class.getDeclaredField("habilidadeRepository"); f.setAccessible(true); f.set(iaService, habilidadeRepository);
            f = IAService.class.getDeclaredField("recomendacaoRepository"); f.setAccessible(true); f.set(iaService, recomendacaoRepository);
            f = IAService.class.getDeclaredField("mensageriaService"); f.setAccessible(true); f.set(iaService, mensageriaService);
            f = IAService.class.getDeclaredField("openAIService"); f.setAccessible(true); f.set(iaService, openAIService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void recomendarColaborador_shouldChooseUserWithLeastOpenTasks_andSaveRecommendation() {
        Usuario u1 = new Usuario(); u1.setIdUsuario(1L); u1.setNmUsuario("A");
        Usuario u2 = new Usuario(); u2.setIdUsuario(2L); u2.setNmUsuario("B");

        when(usuarioRepository.findByTpUsuario("C")).thenReturn(List.of(u1, u2));
        when(tarefaRepository.countOpenTasksByColaborador(1L)).thenReturn(3L);
        when(tarefaRepository.countOpenTasksByColaborador(2L)).thenReturn(1L);

        when(habilidadeRepository.findByUsuarioIdUsuario(1L)).thenReturn(List.of());
        // Return a real Habilidade instance for user 2
        Habilidade hab = new Habilidade(); hab.setNmHabilidade("skill-x"); hab.setDsNivel(3); hab.setUsuario(u2);
        when(habilidadeRepository.findByUsuarioIdUsuario(2L)).thenReturn(List.of(hab));

        when(openAIService.generateRecommendationExplanation(anyString())).thenReturn(Optional.of("generated explanation"));
        // Mock async variant as IAService now calls generateRecommendationExplanationAsync
        when(openAIService.generateRecommendationExplanationAsync(anyString()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(Optional.of("generated explanation")));

        ArgumentCaptor<Recomendacao> captor = ArgumentCaptor.forClass(Recomendacao.class);
        when(recomendacaoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Optional<Recomendacao> opt = iaService.recomendarColaborador("TI");
        assertTrue(opt.isPresent());
        Recomendacao saved = opt.get();
        assertEquals(2L, saved.getUsuario().getIdUsuario());

        // the service now updates the recommendation asynchronously; repository.save may be called
        // for the initial save and again when the generated explanation is available. Accept at least one save
        verify(recomendacaoRepository, atLeast(1)).save(any(Recomendacao.class));
        // ensure the async update eventually saved the generated explanation
        var allSaved = captor.getAllValues();
        var lastSaved = allSaved.get(allSaved.size() - 1);
        assertEquals("generated explanation", lastSaved.getDsRecomendacao());

        // mensageria is called at least once (initial enqueue); it may be called again after async update
        verify(mensageriaService, atLeast(1)).enviarRecomendacaoAsync(any(Recomendacao.class));
    }

    @Test
    void recomendarColaborador_shouldReturnEmpty_whenNoCollaborators() {
        when(usuarioRepository.findByTpUsuario("C")).thenReturn(List.of());
        Optional<Recomendacao> opt = iaService.recomendarColaborador("TI");
        assertTrue(opt.isEmpty());
    }
}
