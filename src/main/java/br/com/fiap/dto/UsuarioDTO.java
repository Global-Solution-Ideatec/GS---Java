package br.com.fiap.dto;

public class UsuarioDTO {
    private Long idUsuario;
    private String nmUsuario;
    private String dsEmail;
    private String tpUsuario;
    private String stAtivo;

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getNmUsuario() { return nmUsuario; }
    public void setNmUsuario(String nmUsuario) { this.nmUsuario = nmUsuario; }
    public String getDsEmail() { return dsEmail; }
    public void setDsEmail(String dsEmail) { this.dsEmail = dsEmail; }
    public String getTpUsuario() { return tpUsuario; }
    public void setTpUsuario(String tpUsuario) { this.tpUsuario = tpUsuario; }
    public String getStAtivo() { return stAtivo; }
    public void setStAtivo(String stAtivo) { this.stAtivo = stAtivo; }
}

