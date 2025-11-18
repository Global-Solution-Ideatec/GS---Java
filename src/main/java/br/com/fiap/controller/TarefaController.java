package br.com.fiap.controller;

import br.com.fiap.dto.TarefaCreateDTO;
import br.com.fiap.dto.TarefaDTO;
import br.com.fiap.dto.TarefaMapper;
import br.com.fiap.model.Tarefa;
import br.com.fiap.model.Usuario;
import br.com.fiap.repository.UsuarioRepository;
import br.com.fiap.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<TarefaDTO> create(@Valid @RequestBody TarefaCreateDTO dto) {
        Tarefa toSave = TarefaMapper.fromCreateDTO(dto);
        if (dto.getIdGestor() != null) {
            Usuario gestor = usuarioRepository.findById(dto.getIdGestor()).orElse(null);
            toSave.setGestor(gestor);
        }
        if (dto.getIdColaborador() != null) {
            Usuario colab = usuarioRepository.findById(dto.getIdColaborador()).orElse(null);
            toSave.setColaborador(colab);
        }
        Tarefa saved = tarefaService.create(toSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(TarefaMapper.toDTO(saved));
    }

    @GetMapping
    public ResponseEntity<Page<TarefaDTO>> list(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        Pageable p = PageRequest.of(page, size);
        Page<TarefaDTO> tasks = tarefaService.listAll(p).map(TarefaMapper::toDTO);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaDTO> get(@PathVariable Long id) {
        return tarefaService.findById(id)
                .map(TarefaMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<TarefaDTO> update(@PathVariable Long id, @Valid @RequestBody TarefaCreateDTO dto) {
        Optional<Tarefa> opt = tarefaService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Tarefa existing = opt.get();
        existing.setDsTarefa(dto.getDsTarefa());
        existing.setDsArea(dto.getDsArea());
        existing.setStTarefa(dto.getStTarefa());
        if (dto.getIdColaborador() != null) {
            Usuario colab = usuarioRepository.findById(dto.getIdColaborador()).orElse(null);
            existing.setColaborador(colab);
        }
        if (dto.getIdGestor() != null) {
            Usuario gestor = usuarioRepository.findById(dto.getIdGestor()).orElse(null);
            existing.setGestor(gestor);
        }
        Tarefa updated = tarefaService.update(existing);
        return ResponseEntity.ok(TarefaMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tarefaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
