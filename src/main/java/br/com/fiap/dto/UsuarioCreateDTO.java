package br.com.fiap.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioCreateDTO {
    @NotBlank(message = "{usuario.nmUsuario.required}")
    private String nmUsuario;

    @NotBlank(message = "{usuario.dsEmail.required}")
    @Email(message = "{usuario.dsEmail.invalid}")
    private String dsEmail;

    @NotBlank(message = "{usuario.dsSenha.required}")
    @Size(min = 6, message = "{usuario.dsSenha.size}")
    private String dsSenha;
    private String tpUsuario;
    private Long idEmpresa;

    public String getNmUsuario() { return nmUsuario; }
    public void setNmUsuario(String nmUsuario) { this.nmUsuario = nmUsuario; }
    public String getDsEmail() { return dsEmail; }
    public void setDsEmail(String dsEmail) { this.dsEmail = dsEmail; }
    public String getDsSenha() { return dsSenha; }
    public void setDsSenha(String dsSenha) { this.dsSenha = dsSenha; }
    public String getTpUsuario() { return tpUsuario; }
    public void setTpUsuario(String tpUsuario) { this.tpUsuario = tpUsuario; }
    public Long getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }
}
