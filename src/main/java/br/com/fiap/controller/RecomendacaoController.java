package br.com.fiap.controller;

import br.com.fiap.dto.RecomendacaoDTO;
import br.com.fiap.dto.RecomendacaoMapper;
import br.com.fiap.dto.RecomendacaoRequestDTO;
import br.com.fiap.model.Recomendacao;
import br.com.fiap.service.IAService;
import br.com.fiap.service.MensageriaService;
import br.com.fiap.repository.RecomendacaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/recomendacao")
@Tag(name = "Recomendação", description = "Endpoints para gerar recomendações de alocação de colaboradores")
public class RecomendacaoController {

    private IAService iaService;
    private MensageriaService mensageriaService;
    private RecomendacaoRepository recomendacaoRepository;

    // construtor sem-argumentos para usos em testes que instanciam diretamente
    public RecomendacaoController() {
        // keep nulls for tests which will mock/assign dependencies if needed
    }

    public RecomendacaoController(IAService iaService, MensageriaService mensageriaService, RecomendacaoRepository recomendacaoRepository) {
        this.iaService = iaService;
        this.mensageriaService = mensageriaService;
        this.recomendacaoRepository = recomendacaoRepository;
    }

    @GetMapping("/colaborador")
    @Operation(summary = "Recomendar colaborador", description = "Recomenda um colaborador para a área informada (usa regras internas e IA quando disponível)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recomendação encontrada"),
                    @ApiResponse(responseCode = "204", description = "Nenhuma recomendação disponível")
            })
    public ResponseEntity<RecomendacaoDTO> recomendar(@RequestParam String area) {
        Optional<Recomendacao> opt = iaService.recomendarColaborador(area);
        return opt.map(r -> ResponseEntity.ok(RecomendacaoMapper.toDTO(r)))
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/request")
    @Operation(summary = "Solicitar recomendação (assíncrono)", description = "Envia uma requisição para a fila para gerar recomendação assíncrona; se Rabbit não disponível, processa sincronicamente")
    public ResponseEntity<?> requestRecommendation(@Valid @RequestBody RecomendacaoRequestDTO request) {
        boolean enqueued = mensageriaService.enviarRecomendacaoRequest(request.getArea());
        if (enqueued) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Request enfileirada");
        } else {
            // fallback síncrono
            Optional<Recomendacao> opt = iaService.recomendarColaborador(request.getArea());
            return opt.map(r -> ResponseEntity.ok(RecomendacaoMapper.toDTO(r)))
                    .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
        }
    }

    @GetMapping
    @Operation(summary = "Listar recomendações", description = "Lista recomendações geradas (paginado)")
    public ResponseEntity<Page<RecomendacaoDTO>> list(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable p = PageRequest.of(page, size);
        Page<RecomendacaoDTO> result = recomendacaoRepository.findAll(p).map(RecomendacaoMapper::toDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/fatores")
    @Operation(summary = "Obter fatores de uma recomendação", description = "Retorna os fatores/metadados que explicam a decisão da recomendação")
    public ResponseEntity<?> getFatores(@PathVariable Long id) {
        Optional<Recomendacao> opt = recomendacaoRepository.findById(id);
        if (opt.isPresent()) {
            String fatores = opt.get().getFatores();
            if (fatores == null || fatores.trim().isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok().body(fatores);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recomendação não encontrada");
    }
}
