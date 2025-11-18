package br.com.fiap.controller;

import br.com.fiap.dto.AuthRequestDTO;
import br.com.fiap.dto.AuthResponseDTO;
import br.com.fiap.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(user);
            return ResponseEntity.ok(new AuthResponseDTO(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
    }

    /**
     * Refresh de token: aceita um token JWT no header Authorization (Bearer ...) mesmo expirado
     * e, se a assinatura for válida, emite um novo token de acesso para o usuário.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Cabeçalho Authorization ausente ou inválido");
            }
            String token = authorizationHeader.substring(7);
            // extrai claims mesmo que o token esteja expirado (parse com AllowExpired)
            Claims claims = jwtUtil.extractAllClaimsAllowExpired(token);
            String username = claims.getSubject();
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.status(401).body("Token inválido");
            }

            UserDetails user = userDetailsService.loadUserByUsername(username);
            // opcional: aqui você poderia verificar se o usuário ainda está ativo

            String newToken = jwtUtil.generateToken(user);
            return ResponseEntity.ok(new AuthResponseDTO(newToken));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body("Não foi possível renovar o token: " + ex.getMessage());
        }
    }
}
