package br.com.fiap.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_USUARIO")
public class Usuario {

    @Id
    @SequenceGenerator(name = "seq_usuario", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario")
    private Long idUsuario;

    @Column(name = "NM_USUARIO", length = 100, nullable = false)
    @NotBlank
    private String nmUsuario;

    @Column(name = "DS_EMAIL", length = 100, unique = true)
    @Email
    private String dsEmail;

    @Column(name = "DS_SENHA", length = 200, nullable = false)
    @NotBlank
    private String dsSenha;

    @Column(name = "TP_USUARIO", length = 1)
    private String tpUsuario; // 'C' ou 'G'

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro = LocalDateTime.now();

    @Column(name = "ST_ATIVO", length = 1)
    private String stAtivo = "S";

    @ManyToOne
    @JoinColumn(name = "ID_EMPRESA")
    private Empresa idEmpresa;

    // getters/setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNmUsuario() {
        return nmUsuario;
    }

    public void setNmUsuario(String nmUsuario) {
        this.nmUsuario = nmUsuario;
    }

    public String getDsEmail() {
        return dsEmail;
    }

    public void setDsEmail(String dsEmail) {
        this.dsEmail = dsEmail;
    }

    public String getDsSenha() {
        return dsSenha;
    }

    public void setDsSenha(String dsSenha) {
        this.dsSenha = dsSenha;
    }

    public String getTpUsuario() {
        return tpUsuario;
    }

    public void setTpUsuario(String tpUsuario) {
        this.tpUsuario = tpUsuario;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public String getStAtivo() {
        return stAtivo;
    }

    public void setStAtivo(String stAtivo) {
        this.stAtivo = stAtivo;
    }

    public Empresa getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Empresa idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
}
