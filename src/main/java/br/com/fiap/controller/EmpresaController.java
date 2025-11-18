package br.com.fiap.controller;

import br.com.fiap.dto.EmpresaDTO;
import br.com.fiap.dto.EmpresaMapper;
import br.com.fiap.model.Empresa;
import br.com.fiap.repository.EmpresaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private MessageSource messageSource;

    @PostMapping
    @ResponseBody
    public ResponseEntity<EmpresaDTO> create(@Valid @RequestBody EmpresaDTO dto) {
        Empresa e = EmpresaMapper.fromDTO(dto);
        Empresa saved = empresaRepository.save(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmpresaMapper.toDTO(saved));
    }

    // Form submission from Thymeleaf (application/x-www-form-urlencoded)
    @PostMapping(path = "/form", consumes = {"application/x-www-form-urlencoded"})
    public String createFromForm(@Valid EmpresaDTO dto, org.springframework.validation.BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            java.util.Map<String,String> errors = new java.util.HashMap<>();
            bindingResult.getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("editEmpresa", dto);
            return "redirect:/empresas";
        }
        Empresa e = EmpresaMapper.fromDTO(dto);
        empresaRepository.save(e);
        String msg = messageSource.getMessage("empresa.created", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", msg);
        return "redirect:/empresas";
    }

    // Update via form
    @PostMapping(path = "/form/{id}", consumes = {"application/x-www-form-urlencoded"})
    public String updateFromForm(@PathVariable Long id, @Valid EmpresaDTO dto, org.springframework.validation.BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            java.util.Map<String,String> errors = new java.util.HashMap<>();
            bindingResult.getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("editEmpresa", dto);
            return "redirect:/empresas?editId=" + id;
        }
        empresaRepository.findById(id).ifPresent(existing -> {
            existing.setNmEmpresa(dto.getNmEmpresa());
            existing.setDsCnpj(dto.getDsCnpj());
            existing.setDsPoliticaHibrida(dto.getDsPoliticaHibrida());
            empresaRepository.save(existing);
        });
        String msg = messageSource.getMessage("empresa.updated", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", msg);
        return "redirect:/empresas";
    }

    // Also support PUT from HTML forms (HiddenHttpMethodFilter) - delegates to same logic
    @PutMapping(path = "/form/{id}", consumes = {"application/x-www-form-urlencoded"})
    public String updateFromFormPut(@PathVariable Long id, @Valid EmpresaDTO dto, org.springframework.validation.BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        return updateFromForm(id, dto, bindingResult, redirectAttributes);
    }

    @PostMapping(path = "/delete/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public String deleteFromForm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        empresaRepository.findById(id).ifPresent(e -> empresaRepository.deleteById(id));
        String msg = messageSource.getMessage("empresa.deleted", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", msg);
        return "redirect:/empresas";
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<EmpresaDTO>> list(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable p = PageRequest.of(page, size);
        Page<EmpresaDTO> result = empresaRepository.findAll(p).map(EmpresaMapper::toDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EmpresaDTO> get(@PathVariable Long id) {
        return empresaRepository.findById(id)
                .map(EmpresaMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EmpresaDTO> update(@PathVariable Long id, @Valid @RequestBody EmpresaDTO dto) {
        return empresaRepository.findById(id).map(existing -> {
            existing.setNmEmpresa(dto.getNmEmpresa());
            existing.setDsCnpj(dto.getDsCnpj());
            existing.setDsPoliticaHibrida(dto.getDsPoliticaHibrida());
            Empresa saved = empresaRepository.save(existing);
            return ResponseEntity.ok(EmpresaMapper.toDTO(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // REST delete endpoint (secured)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    @ResponseBody
    public ResponseEntity<Void> deleteRest(@PathVariable Long id) {
        if (empresaRepository.existsById(id)) {
            empresaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
