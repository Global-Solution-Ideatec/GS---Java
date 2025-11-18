package br.com.fiap.config;

import br.com.fiap.model.Empresa;
import br.com.fiap.model.Usuario;
import br.com.fiap.repository.EmpresaRepository;
import br.com.fiap.repository.UsuarioRepository;
import br.com.fiap.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DataLoader implements ApplicationRunner {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // create a default company if none exists
        if (empresaRepository.count() == 0) {
            Empresa e = new Empresa();
            e.setNmEmpresa("Demo Company");
            e.setDsCnpj("00.000.000/0001-00");
            e.setDsPoliticaHibrida("Flexível");
            empresaRepository.save(e);
        }

        Empresa company = empresaRepository.findAll().stream().findFirst().orElse(null);

        // create gestor if not exists
        if (!usuarioRepository.findByDsEmail("gestor@demo").isPresent()) {
            Usuario gestor = new Usuario();
            gestor.setNmUsuario("Gestor Demo");
            gestor.setDsEmail("gestor@demo");
            gestor.setDsSenha(passwordEncoder.encode("demo123"));
            gestor.setTpUsuario("G");
            gestor.setIdEmpresa(company);
            usuarioService.create(gestor);
        }

        // create colaborador if not exists
        if (!usuarioRepository.findByDsEmail("colaborador@demo").isPresent()) {
            Usuario colab = new Usuario();
            colab.setNmUsuario("Colaborador Demo");
            colab.setDsEmail("colaborador@demo");
            colab.setDsSenha(passwordEncoder.encode("demo123"));
            colab.setTpUsuario("C");
            colab.setIdEmpresa(company);
            usuarioService.create(colab);
        }
    }
}

