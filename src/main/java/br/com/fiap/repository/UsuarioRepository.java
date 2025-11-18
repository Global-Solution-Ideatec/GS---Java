package br.com.fiap.repository;

import br.com.fiap.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByDsEmail(String email);

    Page<Usuario> findAll(Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.tpUsuario = :tp")
    Page<Usuario> findByTpUsuario(@Param("tp") String tp, Pageable pageable);

    // Overload to get all users by type
    List<Usuario> findByTpUsuario(String tp);
}
