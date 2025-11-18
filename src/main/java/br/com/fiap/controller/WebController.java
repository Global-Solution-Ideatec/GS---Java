package br.com.fiap.controller;

import br.com.fiap.dto.RecomendacaoDTO;
import br.com.fiap.dto.RecomendacaoMapper;
import br.com.fiap.dto.TarefaCreateDTO;
import br.com.fiap.dto.TarefaDTO;
import br.com.fiap.dto.TarefaMapper;
import br.com.fiap.model.Recomendacao;
import br.com.fiap.model.Tarefa;
import br.com.fiap.model.Usuario;
import br.com.fiap.model.Empresa;
import br.com.fiap.model.Habilidade;
import br.com.fiap.service.IAService;
import br.com.fiap.service.TarefaService;
import br.com.fiap.repository.UsuarioRepository;
import br.com.fiap.repository.EmpresaRepository;
import br.com.fiap.repository.HabilidadeRepository;
import br.com.fiap.repository.RecomendacaoRepository;
import br.com.fiap.dto.EmpresaMapper;
import br.com.fiap.dto.HabilidadeMapper;
import br.com.fiap.dto.UsuarioMapper;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    @Autowired
    private IAService iaService;

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private HabilidadeRepository habilidadeRepository;

    @Autowired
    private RecomendacaoRepository recomendacaoRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request, @RequestParam(defaultValue = "0") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() : "-guest-");
        // add role flags for template to show/hide actions
        boolean isGestor = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
        boolean isColaborador = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COLABORADOR") || a.getAuthority().equals("ROLE_C"));
        model.addAttribute("isGestor", isGestor);
        model.addAttribute("isColaborador", isColaborador);

        Pageable p = PageRequest.of(page, 10);
        Page<TarefaDTO> tarefas = tarefaService.listAll(p).map(TarefaMapper::toDTO);
        model.addAttribute("tarefas", tarefas);

        // carregar usuários para select (gestores e colaboradores)
        List<Usuario> gestores = usuarioRepository.findByTpUsuario("G");
        List<Usuario> colaboradores = usuarioRepository.findByTpUsuario("C");
        model.addAttribute("gestores", gestores);
        model.addAttribute("colaboradores", colaboradores);

        return "dashboard";
    }

    // support editing a tarefa via query param ?editId=123
    @GetMapping(value = "/dashboard", params = "editId")
    public String dashboardEdit(Model model, @RequestParam Long editId, @RequestParam(defaultValue = "0") int page) {
        // reuse dashboard logic
        dashboard(model, null, page);
        tarefaService.findById(editId).ifPresent(t -> model.addAttribute("editTarefa", TarefaMapper.toDTO(t)));
        return "dashboard";
    }

    @PostMapping("/dashboard/recomendar")
    public String recomendar(@RequestParam String area, Model model) {
        Optional<Recomendacao> opt = iaService.recomendarColaborador(area);
        if (opt.isPresent()) {
            RecomendacaoDTO dto = RecomendacaoMapper.toDTO(opt.get());
            model.addAttribute("recomendacao", dto);
            model.addAttribute("mensagem", dto.getDsRecomendacao());
        } else {
            String noRec = messageSource.getMessage("recomendacao.none", null, LocaleContextHolder.getLocale());
            model.addAttribute("mensagem", noRec);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() : "-guest-");
        // add role flags
        boolean isGestor = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
        boolean isColaborador = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COLABORADOR") || a.getAuthority().equals("ROLE_C"));
        model.addAttribute("isGestor", isGestor);
        model.addAttribute("isColaborador", isColaborador);
        return "dashboard";
    }

    @PostMapping("/dashboard/tarefa")
    public String createTarefa(@Valid TarefaCreateDTO dto, Model model, RedirectAttributes redirectAttributes) {
        Tarefa toSave = TarefaMapper.fromCreateDTO(dto);
        if (dto.getIdGestor() != null) {
            usuarioRepository.findById(dto.getIdGestor()).ifPresent(toSave::setGestor);
        }
        if (dto.getIdColaborador() != null) {
            usuarioRepository.findById(dto.getIdColaborador()).ifPresent(toSave::setColaborador);
        }
        tarefaService.create(toSave);
        String msg = messageSource.getMessage("tarefa.created", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", msg);
        return "redirect:/dashboard";
    }

    // Support PUT from HTML form via HiddenHttpMethodFilter for tarefa update
    @PutMapping("/dashboard/tarefa/{id}")
    public String updateTarefaPut(@PathVariable Long id, @Valid TarefaCreateDTO dto, org.springframework.validation.BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        return updateTarefa(id, dto, bindingResult, redirectAttributes);
    }

    // Update tarefa via form
    @PostMapping("/dashboard/tarefa/{id}")
    public String updateTarefa(@PathVariable Long id, @Valid TarefaCreateDTO dto, org.springframework.validation.BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            java.util.Map<String,String> errors = new java.util.HashMap<>();
            bindingResult.getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/dashboard?editId=" + id;
        }
        tarefaService.findById(id).ifPresent(existing -> {
            existing.setDsTarefa(dto.getDsTarefa());
            existing.setDsArea(dto.getDsArea());
            existing.setStTarefa(dto.getStTarefa());
            if (dto.getIdGestor() != null) usuarioRepository.findById(dto.getIdGestor()).ifPresent(existing::setGestor);
            if (dto.getIdColaborador() != null) usuarioRepository.findById(dto.getIdColaborador()).ifPresent(existing::setColaborador);
            tarefaService.update(existing);
        });
        String msg = messageSource.getMessage("tarefa.updated", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", msg);
        return "redirect:/dashboard";
    }

    @GetMapping("/empresas")
    public String empresas(Model model, @RequestParam(required = false) Long editId) {
        List<Empresa> list = empresaRepository.findAll();
        model.addAttribute("empresas", list);
        if (editId != null) {
            empresaRepository.findById(editId).ifPresent(e -> model.addAttribute("editEmpresa", EmpresaMapper.toDTO(e)));
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() : "-guest-");
        boolean isGestor = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
        boolean isColaborador = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COLABORADOR") || a.getAuthority().equals("ROLE_C"));
        model.addAttribute("isGestor", isGestor);
        model.addAttribute("isColaborador", isColaborador);
        return "empresas";
    }

    @GetMapping("/habilidades")
    public String habilidades(Model model, @RequestParam(required = false) Long editId) {
        List<Habilidade> list = habilidadeRepository.findAll();
        model.addAttribute("habilidades", list);
        // add users for select in the form
        List<Usuario> usuarios = usuarioRepository.findByTpUsuario("C");
        model.addAttribute("usuarios", usuarios);
        if (editId != null) {
            habilidadeRepository.findById(editId).ifPresent(h -> model.addAttribute("editHabilidade", HabilidadeMapper.toDTO(h)));
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() : "-guest-");
        boolean isGestor = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
        boolean isColaborador = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COLABORADOR") || a.getAuthority().equals("ROLE_C"));
        model.addAttribute("isGestor", isGestor);
        model.addAttribute("isColaborador", isColaborador);
        return "habilidades";
    }

    @GetMapping("/recomendacoes")
    public String recomendacoes(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable p = PageRequest.of(page, 10);
        Page<Recomendacao> pageResult = recomendacaoRepository.findAll(p);
        model.addAttribute("recomendacoes", pageResult);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() : "-guest-");
        boolean isGestor = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
        boolean isColaborador = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COLABORADOR") || a.getAuthority().equals("ROLE_C"));
        model.addAttribute("isGestor", isGestor);
        model.addAttribute("isColaborador", isColaborador);
        return "recomendacoes";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model, @RequestParam(required = false) Long editId) {
        List<Usuario> list = usuarioRepository.findAll();
        model.addAttribute("usuarios", list);
        List<Empresa> empresas = empresaRepository.findAll();
        model.addAttribute("empresas", empresas);
        if (editId != null) {
            usuarioRepository.findById(editId).ifPresent(u -> model.addAttribute("editUsuario", UsuarioMapper.toDTO(u)));
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() : "-guest-");
        boolean isGestor = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
        boolean isColaborador = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COLABORADOR") || a.getAuthority().equals("ROLE_C"));
        model.addAttribute("isGestor", isGestor);
        model.addAttribute("isColaborador", isColaborador);
        return "usuarios";
    }
}
