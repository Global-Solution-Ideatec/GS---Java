package br.com.fiap.controller;

import br.com.fiap.dto.EmpresaDTO;
import br.com.fiap.model.Empresa;
import br.com.fiap.repository.EmpresaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmpresaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EmpresaRepository empresaRepository;

    private EmpresaController controller;

    @BeforeEach
    void setup() {
        controller = new EmpresaController();
        try {
            java.lang.reflect.Field f = EmpresaController.class.getDeclaredField("empresaRepository");
            f.setAccessible(true);
            f.set(controller, empresaRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createCompany_returns201() throws Exception {
        Empresa saved = new Empresa();
        saved.setIdEmpresa(1L);
        saved.setNmEmpresa("ACME");
        saved.setDsCnpj("00.000.000/0001-00");

        when(empresaRepository.save(any(Empresa.class))).thenReturn(saved);

        EmpresaDTO dto = new EmpresaDTO();
        dto.setNmEmpresa("ACME");
        dto.setDsCnpj("00.000.000/0001-00");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/empresas").contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void getCompany_returns200() throws Exception {
        Empresa e = new Empresa();
        e.setIdEmpresa(2L);
        e.setNmEmpresa("Beta");
        e.setDsCnpj("11.111.111/0001-11");

        when(empresaRepository.findById(2L)).thenReturn(Optional.of(e));

        mockMvc.perform(get("/api/empresas/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void updateCompany_returns200() throws Exception {
        Empresa existing = new Empresa();
        existing.setIdEmpresa(3L);
        existing.setNmEmpresa("Old");

        Empresa updated = new Empresa();
        updated.setIdEmpresa(3L);
        updated.setNmEmpresa("New");

        when(empresaRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(empresaRepository.save(any(Empresa.class))).thenReturn(updated);

        EmpresaDTO dto = new EmpresaDTO();
        dto.setNmEmpresa("New");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/empresas/3").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}

