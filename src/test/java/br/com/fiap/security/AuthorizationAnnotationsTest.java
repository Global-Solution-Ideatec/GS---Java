package br.com.fiap.security;

import br.com.fiap.controller.EmpresaController;
import br.com.fiap.controller.HabilidadeController;
import br.com.fiap.controller.UsuarioController;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationAnnotationsTest {

    @Test
    void deleteMethodsShouldRequireGestorRole() throws Exception {
        // EmpresaController deleteFromForm
        Method eForm = EmpresaController.class.getDeclaredMethod("deleteFromForm", Long.class, RedirectAttributes.class);
        PreAuthorize p = eForm.getAnnotation(PreAuthorize.class);
        assertNotNull(p, "EmpresaController.deleteFromForm should be annotated with @PreAuthorize");
        assertTrue(p.value().contains("GESTOR"), "EmpresaController.deleteFromForm should require GESTOR role");

        // EmpresaController deleteRest
        Method eRest = EmpresaController.class.getDeclaredMethod("deleteRest", Long.class);
        p = eRest.getAnnotation(PreAuthorize.class);
        assertNotNull(p, "EmpresaController.deleteRest should be annotated with @PreAuthorize");
        assertTrue(p.value().contains("GESTOR"), "EmpresaController.deleteRest should require GESTOR role");

        // UsuarioController deleteFromForm
        Method u = UsuarioController.class.getDeclaredMethod("deleteFromForm", Long.class, RedirectAttributes.class);
        p = u.getAnnotation(PreAuthorize.class);
        assertNotNull(p, "UsuarioController.deleteFromForm should be annotated with @PreAuthorize");
        assertTrue(p.value().contains("GESTOR"), "UsuarioController.deleteFromForm should require GESTOR role");

        // HabilidadeController deleteFromForm
        Method h = HabilidadeController.class.getDeclaredMethod("deleteFromForm", Long.class, RedirectAttributes.class);
        p = h.getAnnotation(PreAuthorize.class);
        assertNotNull(p, "HabilidadeController.deleteFromForm should be annotated with @PreAuthorize");
        assertTrue(p.value().contains("GESTOR"), "HabilidadeController.deleteFromForm should require GESTOR role");
    }
}

