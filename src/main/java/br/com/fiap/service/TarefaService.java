package br.com.fiap.service;

import br.com.fiap.model.Tarefa;
import br.com.fiap.repository.TarefaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public Tarefa create(@Valid Tarefa t) {
        return tarefaRepository.save(t);
    }

    public Optional<Tarefa> findById(Long id) {
        return tarefaRepository.findById(id);
    }

    public Page<Tarefa> listAll(Pageable pageable) {
        return tarefaRepository.findAll(pageable);
    }

    @CacheEvict(value = "tarefas", allEntries = true)
    public Tarefa update(Tarefa t) {
        return tarefaRepository.save(t);
    }

    public void delete(Long id) {
        tarefaRepository.deleteById(id);
    }
}

