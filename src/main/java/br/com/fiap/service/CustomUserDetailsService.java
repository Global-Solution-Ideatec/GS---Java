package br.com.fiap.service;

import br.com.fiap.model.Usuario;
import br.com.fiap.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByDsEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("G".equalsIgnoreCase(u.getTpUsuario())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_GESTOR"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_COLABORADOR"));
        }

        return User.withUsername(u.getDsEmail())
                .password(u.getDsSenha())
                .authorities(authorities)
                .build();
    }
}

