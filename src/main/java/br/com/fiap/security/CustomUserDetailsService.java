package br.com.fiap.security;

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
import java.util.Optional;

// Define explicit bean name to avoid conflict with br.com.fiap.service.CustomUserDetailsService
@Service("securityCustomUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> opt = usuarioRepository.findByDsEmail(username);
        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        Usuario u = opt.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Map tpUsuario to roles
        if ("G".equalsIgnoreCase(u.getTpUsuario())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_GESTOR"));
        } else if ("C".equalsIgnoreCase(u.getTpUsuario())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_COLABORADOR"));
        } else {
            // default to collaborator
            authorities.add(new SimpleGrantedAuthority("ROLE_COLABORADOR"));
        }
        // Build Spring Security user
        return User.withUsername(u.getDsEmail())
                .password(u.getDsSenha())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled("N".equalsIgnoreCase(u.getStAtivo()))
                .build();
    }
}
