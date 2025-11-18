package br.com.fiap.service;

import br.com.fiap.model.Recomendacao;
import br.com.fiap.model.Usuario;
import br.com.fiap.model.Habilidade;
import br.com.fiap.repository.HabilidadeRepository;
import br.com.fiap.repository.RecomendacaoRepository;
import br.com.fiap.repository.TarefaRepository;
import br.com.fiap.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class IAServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private HabilidadeRepository habilidadeRepository;

    @Mock
    private RecomendacaoRepository recomendacaoRepository;

    @Mock
    private MensageriaService mensageriaService;

    @Mock
    private OpenAIService openAIService;

    @InjectMocks
    private IAService iaService;

    @Test
    public void whenThereAreCollaborators_chooseOneWithLeastOpenTasks_thenPersistAndSend() {
        Usuario u1 = new Usuario();
        u1.setIdUsuario(1L);
        u1.setNmUsuario("Alice");
        u1.setTpUsuario("C");

        Usuario u2 = new Usuario();
        u2.setIdUsuario(2L);
        u2.setNmUsuario("Bob");
        u2.setTpUsuario("C");

        when(usuarioRepository.findByTpUsuario("C")).thenReturn(List.of(u1, u2));

        // u1 has 5 open tasks, u2 has 2 -> choose u2
        when(tarefaRepository.countOpenTasksByColaborador(1L)).thenReturn(5L);
        when(tarefaRepository.countOpenTasksByColaborador(2L)).thenReturn(2L);

        // skills: use actual Habilidade instances to satisfy generics
        when(habilidadeRepository.findByUsuarioIdUsuario(1L)).thenReturn(List.of(new Habilidade(), new Habilidade(), new Habilidade()));
        when(habilidadeRepository.findByUsuarioIdUsuario(2L)).thenReturn(List.of(new Habilidade()));

        // OpenAI returns empty (fallback)
        when(openAIService.generateRecommendationExplanation(anyString())).thenReturn(Optional.empty());

        ArgumentCaptor<Recomendacao> captor = ArgumentCaptor.forClass(Recomendacao.class);
        when(recomendacaoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Optional<Recomendacao> opt = iaService.recomendarColaborador("TI");

        assertTrue(opt.isPresent());
        Recomendacao saved = opt.get();
        assertNotNull(saved.getUsuario());
        assertEquals(2L, saved.getUsuario().getIdUsuario());
        assertEquals("REPARTICAO_TAREFA", saved.getTpRecomendacao());
        assertNotNull(saved.getDsRecomendacao());

        // Verify mensageria was called
        verify(mensageriaService, times(1)).enviarRecomendacaoAsync(any(Recomendacao.class));
    }

    @Test
    public void whenNoCollaborators_returnEmpty() {
        when(usuarioRepository.findByTpUsuario("C")).thenReturn(List.of());
        Optional<Recomendacao> opt = iaService.recomendarColaborador("TI");
        assertTrue(opt.isEmpty());
        verify(recomendacaoRepository, never()).save(any());
        verify(mensageriaService, never()).enviarRecomendacaoAsync(any());
    }
}
