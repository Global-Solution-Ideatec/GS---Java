package br.com.fiap.controller;

import br.com.fiap.dto.UsuarioCreateDTO;
import br.com.fiap.model.Empresa;
import br.com.fiap.model.Usuario;
import br.com.fiap.repository.EmpresaRepository;
import br.com.fiap.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioController controller;

    @BeforeEach
    void setup() {
        controller = new UsuarioController();
        try {
            java.lang.reflect.Field f = UsuarioController.class.getDeclaredField("usuarioService");
            f.setAccessible(true);
            f.set(controller, usuarioService);

            f = UsuarioController.class.getDeclaredField("empresaRepository");
            f.setAccessible(true);
            f.set(controller, empresaRepository);

            f = UsuarioController.class.getDeclaredField("passwordEncoder");
            f.setAccessible(true);
            f.set(controller, passwordEncoder);

            // messageSource not necessary for REST create path in this test
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createUser_returns201() throws Exception {
        Usuario saved = new Usuario();
        saved.setIdUsuario(10L);
        saved.setNmUsuario("Test User");
        saved.setDsEmail("test@example.com");

        when(usuarioService.create(any(Usuario.class))).thenReturn(saved);

        UsuarioCreateDTO dto = new UsuarioCreateDTO();
        dto.setNmUsuario("Test User");
        dto.setDsEmail("test@example.com");
        dto.setDsSenha("secret12");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/usuarios").contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void getUser_returns200() throws Exception {
        Usuario u = new Usuario();
        u.setIdUsuario(11L);
        u.setNmUsuario("Another");
        u.setDsEmail("another@example.com");

        when(usuarioService.findById(11L)).thenReturn(Optional.of(u));

        mockMvc.perform(get("/api/usuarios/11"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}

