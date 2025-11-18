package br.com.fiap.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HabilidadeDTO {
    private Long idHabilidade;

    @NotBlank(message = "{habilidade.nmHabilidade.required}")
    @Size(max = 100, message = "{habilidade.nmHabilidade.maxlength}")
    private String nmHabilidade;

    @Min(value = 1, message = "{habilidade.dsNivel.range}")
    @Max(value = 10, message = "{habilidade.dsNivel.range}")
    private Integer dsNivel;

    private Long idUsuario;

    public Long getIdHabilidade() { return idHabilidade; }
    public void setIdHabilidade(Long idHabilidade) { this.idHabilidade = idHabilidade; }
    public String getNmHabilidade() { return nmHabilidade; }
    public void setNmHabilidade(String nmHabilidade) { this.nmHabilidade = nmHabilidade; }
    public Integer getDsNivel() { return dsNivel; }
    public void setDsNivel(Integer dsNivel) { this.dsNivel = dsNivel; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
}
