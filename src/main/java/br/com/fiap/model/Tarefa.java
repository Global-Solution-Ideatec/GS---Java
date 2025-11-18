package br.com.fiap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_TAREFA")
public class Tarefa {

    @Id
    @SequenceGenerator(name = "seq_tarefa", sequenceName = "SEQ_TAREFA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tarefa")
    private Long idTarefa;

    @Column(name = "DS_TAREFA", length = 150, nullable = false)
    private String dsTarefa;

    @Column(name = "DS_AREA", length = 50)
    private String dsArea;

    @Column(name = "DT_CRIACAO")
    private LocalDateTime dtCriacao = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "ID_GESTOR", nullable = false)
    private Usuario gestor;

    @ManyToOne
    @JoinColumn(name = "ID_COLABORADOR")
    private Usuario colaborador;

    @Column(name = "ST_TAREFA", length = 20)
    private String stTarefa = "Pendente";

    // getters/setters
    public Long getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(Long idTarefa) {
        this.idTarefa = idTarefa;
    }

    public String getDsTarefa() {
        return dsTarefa;
    }

    public void setDsTarefa(String dsTarefa) {
        this.dsTarefa = dsTarefa;
    }

    public String getDsArea() {
        return dsArea;
    }

    public void setDsArea(String dsArea) {
        this.dsArea = dsArea;
    }

    public LocalDateTime getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(LocalDateTime dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

    public Usuario getGestor() {
        return gestor;
    }

    public void setGestor(Usuario gestor) {
        this.gestor = gestor;
    }

    public Usuario getColaborador() {
        return colaborador;
    }

    public void setColaborador(Usuario colaborador) {
        this.colaborador = colaborador;
    }

    public String getStTarefa() {
        return stTarefa;
    }

    public void setStTarefa(String stTarefa) {
        this.stTarefa = stTarefa;
    }
}
