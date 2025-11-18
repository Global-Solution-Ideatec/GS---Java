package br.com.fiap.service;

import br.com.fiap.model.Usuario;
import br.com.fiap.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario create(@Valid Usuario u) {
        return usuarioRepository.save(u);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Cacheable("usuarios")
    public Page<Usuario> listAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByDsEmail(email);
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario update(Usuario u) {
        return usuarioRepository.save(u);
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }
}
