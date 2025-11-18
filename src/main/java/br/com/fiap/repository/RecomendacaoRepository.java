package br.com.fiap.repository;

import br.com.fiap.model.Recomendacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecomendacaoRepository extends JpaRepository<Recomendacao, Long> {
}

