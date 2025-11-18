package br.com.fiap.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TB_HABILIDADE")
public class Habilidade {

    @Id
    @SequenceGenerator(name = "seq_habilidade", sequenceName = "SEQ_HABILIDADE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_habilidade")
    private Long idHabilidade;

    @Column(name = "NM_HABILIDADE", length = 100, nullable = false)
    private String nmHabilidade;

    @Column(name = "DS_NIVEL")
    private Integer dsNivel;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    // getters/setters
    public Long getIdHabilidade() {
        return idHabilidade;
    }

    public void setIdHabilidade(Long idHabilidade) {
        this.idHabilidade = idHabilidade;
    }

    public String getNmHabilidade() {
        return nmHabilidade;
    }

    public void setNmHabilidade(String nmHabilidade) {
        this.nmHabilidade = nmHabilidade;
    }

    public Integer getDsNivel() {
        return dsNivel;
    }

    public void setDsNivel(Integer dsNivel) {
        this.dsNivel = dsNivel;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
