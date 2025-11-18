package br.com.fiap.controller;

import br.com.fiap.service.UsuarioService;
import br.com.fiap.repository.EmpresaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UsuarioValidationAuthTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private EmpresaRepository empresaRepository;

    @MockBean(name = "passwordEncoderConfig")
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @MockBean
    private org.springframework.context.MessageSource messageSource;

    @Test
    @WithMockUser
    void create_returns400_whenPayloadInvalid() throws Exception {
        String body = "{}"; // missing required fields

        mockMvc.perform(post("/api/usuarios")
                        .with(user("test").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_returns409_whenEmailExists() throws Exception {
        // Prepare DTO
        br.com.fiap.dto.UsuarioCreateDTO dto = new br.com.fiap.dto.UsuarioCreateDTO();
        dto.setNmUsuario("Test User");
        dto.setDsEmail("existing@example.com");
        dto.setDsSenha("secret12");
        String body = objectMapper.writeValueAsString(dto);

        // Mock service to throw DataIntegrityViolationException
        when(usuarioService.create(any())).thenThrow(new org.springframework.dao.DataIntegrityViolationException("duplicate"));
        when(messageSource.getMessage("user.email.exists", null, java.util.Locale.getDefault())).thenReturn("Email already registered");

        mockMvc.perform(post("/api/usuarios")
                        .with(user("test").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already registered"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void delete_withGestor_returnsRedirect() throws Exception {
        doNothing().when(usuarioService).delete(1L);

        mockMvc.perform(post("/api/usuarios/delete/1"))
                .andExpect(status().is3xxRedirection());

        verify(usuarioService).delete(1L);
    }

    @Test
    @WithMockUser(roles = "COLABORADOR")
    void delete_withoutGestor_returnsForbidden() throws Exception {
        mockMvc.perform(post("/api/usuarios/delete/1"))
                .andExpect(status().isForbidden());
    }
}
