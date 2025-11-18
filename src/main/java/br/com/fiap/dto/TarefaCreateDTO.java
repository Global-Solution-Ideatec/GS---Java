package br.com.fiap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TarefaCreateDTO {
    @NotBlank(message = "{tarefa.dsTarefa.required}")
    @Size(max = 255, message = "{tarefa.dsTarefa.maxlength}")
    private String dsTarefa;
    private String dsArea;
    private Long idGestor;
    private Long idColaborador;
    private String stTarefa;

    public String getDsTarefa() { return dsTarefa; }
    public void setDsTarefa(String dsTarefa) { this.dsTarefa = dsTarefa; }
    public String getDsArea() { return dsArea; }
    public void setDsArea(String dsArea) { this.dsArea = dsArea; }
    public Long getIdGestor() { return idGestor; }
    public void setIdGestor(Long idGestor) { this.idGestor = idGestor; }
    public Long getIdColaborador() { return idColaborador; }
    public void setIdColaborador(Long idColaborador) { this.idColaborador = idColaborador; }
    public String getStTarefa() { return stTarefa; }
    public void setStTarefa(String stTarefa) { this.stTarefa = stTarefa; }
}
