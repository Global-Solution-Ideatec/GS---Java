package br.com.fiap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@SuppressWarnings("unused")
@Entity
@Table(name = "TB_RECOMENDACAO_IA")
public class Recomendacao {

    @Id
    @SequenceGenerator(name = "seq_recomendacao", sequenceName = "SEQ_RECOMENDACAO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_recomendacao")
    private Long idRecomendacao;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @Column(name = "DS_RECOMENDACAO", length = 250)
    private String dsRecomendacao;

    @Column(name = "DT_RECOMENDACAO")
    private LocalDateTime dtRecomendacao = LocalDateTime.now();

    @Column(name = "TP_RECOMENDACAO", length = 50)
    private String tpRecomendacao;

    @Column(name = "DS_FATORES", length = 2000)
    private String fatores;

    // getters/setters
    public Long getIdRecomendacao() {
        return idRecomendacao;
    }

    public void setIdRecomendacao(Long idRecomendacao) {
        this.idRecomendacao = idRecomendacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDsRecomendacao() {
        return dsRecomendacao;
    }

    public void setDsRecomendacao(String dsRecomendacao) {
        this.dsRecomendacao = dsRecomendacao;
    }

    public LocalDateTime getDtRecomendacao() {
        return dtRecomendacao;
    }

    public void setDtRecomendacao(LocalDateTime dtRecomendacao) {
        this.dtRecomendacao = dtRecomendacao;
    }

    public String getTpRecomendacao() {
        return tpRecomendacao;
    }

    public void setTpRecomendacao(String tpRecomendacao) {
        this.tpRecomendacao = tpRecomendacao;
    }

    public String getFatores() {
        return fatores;
    }

    public void setFatores(String fatores) {
        this.fatores = fatores;
    }
}
