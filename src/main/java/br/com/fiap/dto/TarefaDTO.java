package br.com.fiap.dto;

import java.time.LocalDateTime;

public class TarefaDTO {
    private Long idTarefa;
    private String dsTarefa;
    private String dsArea;
    private LocalDateTime dtCriacao;
    private Long idGestor;
    private Long idColaborador;
    private String stTarefa;

    public Long getIdTarefa() { return idTarefa; }
    public void setIdTarefa(Long idTarefa) { this.idTarefa = idTarefa; }
    public String getDsTarefa() { return dsTarefa; }
    public void setDsTarefa(String dsTarefa) { this.dsTarefa = dsTarefa; }
    public String getDsArea() { return dsArea; }
    public void setDsArea(String dsArea) { this.dsArea = dsArea; }
    public LocalDateTime getDtCriacao() { return dtCriacao; }
    public void setDtCriacao(LocalDateTime dtCriacao) { this.dtCriacao = dtCriacao; }
    public Long getIdGestor() { return idGestor; }
    public void setIdGestor(Long idGestor) { this.idGestor = idGestor; }
    public Long getIdColaborador() { return idColaborador; }
    public void setIdColaborador(Long idColaborador) { this.idColaborador = idColaborador; }
    public String getStTarefa() { return stTarefa; }
    public void setStTarefa(String stTarefa) { this.stTarefa = stTarefa; }
}

