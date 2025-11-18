package br.com.fiap.controller;

import br.com.fiap.dto.HabilidadeDTO;
import br.com.fiap.dto.HabilidadeMapper;
import br.com.fiap.model.Habilidade;
import br.com.fiap.model.Usuario;
import br.com.fiap.repository.HabilidadeRepository;
import br.com.fiap.repository.UsuarioRepository;
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
@RequestMapping("/api/habilidades")
public class HabilidadeController {

    @Autowired
    private HabilidadeRepository habilidadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MessageSource messageSource;

    @PostMapping
    @ResponseBody
    public ResponseEntity<HabilidadeDTO> create(@Valid @RequestBody HabilidadeDTO dto) {
        Habilidade h = HabilidadeMapper.fromDTO(dto);
        if (dto.getIdUsuario() != null) {
            Usuario u = usuarioRepository.findById(dto.getIdUsuario()).orElse(null);
            h.setUsuario(u);
        }
        Habilidade saved = habilidadeRepository.save(h);
        return ResponseEntity.status(HttpStatus.CREATED).body(HabilidadeMapper.toDTO(saved));
    }

    // Form support for Thymeleaf (application/x-www-form-urlencoded)
    @PostMapping(path = "/form", consumes = {"application/x-www-form-urlencoded"})
    public String createFromForm(@Valid HabilidadeDTO dto, org.springframework.validation.BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            java.util.Map<String,String> errors = new java.util.HashMap<>();
            bindingResult.getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("editHabilidade", dto);
            return "redirect:/habilidades";
        }
        Habilidade h = HabilidadeMapper.fromDTO(dto);
        if (dto.getIdUsuario() != null) {
            Usuario u = usuarioRepository.findById(dto.getIdUsuario()).orElse(null);
            h.setUsuario(u);
        }
        habilidadeRepository.save(h);
        String msg = messageSource.getMessage("habilidade.created", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", msg);
        return "redirect:/habilidades";
    }

    // Update via form
    @PostMapping(path = "/form/{id}", consumes = {"application/x-www-form-urlencoded"})
    public String updateFromForm(@PathVariable Long id, @Valid HabilidadeDTO dto, org.springframework.validation.BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            java.util.Map<String,String> errors = new java.util.HashMap<>();
            bindingResult.getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("editHabilidade", dto);
            return "redirect:/habilidades?editId=" + id;
        }
        habilidadeRepository.findById(id).ifPresent(existing -> {
            existing.setNmHabilidade(dto.getNmHabilidade());
            existing.setDsNivel(dto.getDsNivel());
            if (dto.getIdUsuario() != null) {
                Usuario u = usuarioRepository.findById(dto.getIdUsuario()).orElse(null);
                existing.setUsuario(u);
            }
            habilidadeRepository.save(existing);
        });
        String msg = messageSource.getMessage("habilidade.updated", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", msg);
        return "redirect:/habilidades";
    }

    @PostMapping(path = "/delete/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public String deleteFromForm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        habilidadeRepository.findById(id).ifPresent(h -> habilidadeRepository.deleteById(id));
        String msg = messageSource.getMessage("habilidade.deleted", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", msg);
        return "redirect:/habilidades";
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Page<HabilidadeDTO>> list(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable p = PageRequest.of(page, size);
        Page<HabilidadeDTO> result = habilidadeRepository.findAll(p).map(HabilidadeMapper::toDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<HabilidadeDTO> get(@PathVariable Long id) {
        return habilidadeRepository.findById(id)
                .map(HabilidadeMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<HabilidadeDTO> update(@PathVariable Long id, @Valid @RequestBody HabilidadeDTO dto) {
        return habilidadeRepository.findById(id).map(existing -> {
            existing.setNmHabilidade(dto.getNmHabilidade());
            existing.setDsNivel(dto.getDsNivel());
            if (dto.getIdUsuario() != null) {
                Usuario u = usuarioRepository.findById(dto.getIdUsuario()).orElse(null);
                existing.setUsuario(u);
            }
            Habilidade saved = habilidadeRepository.save(existing);
            return ResponseEntity.ok(HabilidadeMapper.toDTO(saved));
        }).orElse(ResponseEntity.notFound().build());
    }
}
