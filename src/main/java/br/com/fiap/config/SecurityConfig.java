package br.com.fiap.config;

import br.com.fiap.security.JwtAuthenticationFilter;
import br.com.fiap.security.JwtUtil;
import br.com.fiap.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.http.HttpMethod;

// Damos um nome explícito ao bean de configuração para evitar conflito com outra classe SecurityConfig
@Configuration("configSecurityConfig")
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(CustomUserDetailsService uds) {
        return uds;
    }

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        // Em desenvolvimento usamos BCrypt
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil, AuthenticationProvider authProvider, UserDetailsService userDetailsService) throws Exception {
        // Use lambda-style configuration to avoid deprecated chained methods
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        http.authenticationProvider(authProvider);

        // Definição explícita de regras: rotas públicas, autenticação necessária para recomendação, e demais rotas autenticadas
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/h2-console/**", "/actuator/**", "/","/index","/index.html","/login","/css/**","/js/**","/images/**","/swagger-ui/**","/v3/api-docs/**").permitAll()
                .requestMatchers("/api/public/**", "/api/auth/**").permitAll()
                // Tornar explícito que a API de recomendações requer autenticação (JWT)
                .requestMatchers("/api/recomendacao/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(form -> form.loginPage("/login").permitAll().defaultSuccessUrl("/dashboard", true));

        http.httpBasic(withDefaults());

        // JWT filter for APIs
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
