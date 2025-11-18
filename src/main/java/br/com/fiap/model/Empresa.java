package br.com.fiap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_EMPRESA")
public class Empresa {

    @Id
    @SequenceGenerator(name = "seq_empresa", sequenceName = "SEQ_EMPRESA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_empresa")
    private Long idEmpresa;

    @Column(name = "NM_EMPRESA", length = 100, nullable = false)
    private String nmEmpresa;

    @Column(name = "DS_CNPJ", length = 18)
    private String dsCnpj;

    @Column(name = "DS_POLITICA_HIBRIDA", length = 200)
    private String dsPoliticaHibrida;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dtCadastro = LocalDateTime.now();

    // getters and setters

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNmEmpresa() {
        return nmEmpresa;
    }

    public void setNmEmpresa(String nmEmpresa) {
        this.nmEmpresa = nmEmpresa;
    }

    public String getDsCnpj() {
        return dsCnpj;
    }

    public void setDsCnpj(String dsCnpj) {
        this.dsCnpj = dsCnpj;
    }

    public String getDsPoliticaHibrida() {
        return dsPoliticaHibrida;
    }

    public void setDsPoliticaHibrida(String dsPoliticaHibrida) {
        this.dsPoliticaHibrida = dsPoliticaHibrida;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }
}
