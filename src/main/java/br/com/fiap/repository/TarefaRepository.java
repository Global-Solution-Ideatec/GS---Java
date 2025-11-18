package br.com.fiap.repository;

import br.com.fiap.model.Tarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    Page<Tarefa> findAll(Pageable pageable);

    @Query("SELECT COUNT(t) FROM Tarefa t WHERE t.colaborador.idUsuario = :id AND t.stTarefa <> 'Concluida'")
    long countOpenTasksByColaborador(@Param("id") Long id);

    @Query("SELECT t FROM Tarefa t WHERE t.colaborador.idUsuario = :id")
    Page<Tarefa> findByColaboradorId(@Param("id") Long id, Pageable pageable);
}

