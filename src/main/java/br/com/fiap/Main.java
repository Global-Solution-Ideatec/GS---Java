package br.com.fiap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.fiap.model.Usuario;
import br.com.fiap.model.Empresa;
import br.com.fiap.repository.UsuarioRepository;
import br.com.fiap.repository.EmpresaRepository;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // Seed de dados para desenvolvimento (H2)
    @Bean
    @Profile("!test")
    CommandLineRunner seed(EmpresaRepository empresaRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (empresaRepository.count() == 0) {
                Empresa e = new Empresa();
                e.setIdEmpresa(1L);
                e.setNmEmpresa("SmartLeader Ltda");
                e.setDsCnpj("00.000.000/0001-00");
                empresaRepository.save(e);

                Usuario gestor = new Usuario();
                gestor.setIdUsuario(1L);
                gestor.setNmUsuario("Gestor Exemplo");
                gestor.setDsEmail("gestor@example.com");
                gestor.setDsSenha(passwordEncoder.encode("senha"));
                gestor.setTpUsuario("G");
                gestor.setIdEmpresa(e);
                usuarioRepository.save(gestor);

                Usuario colab = new Usuario();
                colab.setIdUsuario(2L);
                colab.setNmUsuario("Colaborador Exemplo");
                colab.setDsEmail("colab@example.com");
                colab.setDsSenha(passwordEncoder.encode("senha"));
                colab.setTpUsuario("C");
                colab.setIdEmpresa(e);
                usuarioRepository.save(colab);
            }
        };
    }
}