package br.com.fiap.repository;

import br.com.fiap.model.Habilidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabilidadeRepository extends JpaRepository<Habilidade, Long> {
    List<Habilidade> findByUsuarioIdUsuario(Long idUsuario);
}

