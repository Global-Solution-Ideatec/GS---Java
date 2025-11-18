package br.com.fiap.dto;

import br.com.fiap.model.Usuario;

public class UsuarioMapper {
    public static UsuarioDTO toDTO(Usuario u) {
        if (u == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setNmUsuario(u.getNmUsuario());
        dto.setDsEmail(u.getDsEmail());
        dto.setTpUsuario(u.getTpUsuario());
        dto.setStAtivo(u.getStAtivo());
        return dto;
    }

    public static Usuario fromCreateDTO(br.com.fiap.dto.UsuarioCreateDTO dto) {
        if (dto == null) return null;
        Usuario u = new Usuario();
        u.setNmUsuario(dto.getNmUsuario());
        u.setDsEmail(dto.getDsEmail());
        u.setDsSenha(dto.getDsSenha());
        // default to 'C' (colaborador) when not specified
        String tp = dto.getTpUsuario();
        u.setTpUsuario((tp == null || tp.isBlank()) ? "C" : tp);
        return u;
    }
}
