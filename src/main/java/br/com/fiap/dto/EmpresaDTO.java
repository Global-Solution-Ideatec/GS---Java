package br.com.fiap.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmpresaDTO {
    private Long idEmpresa;

    @NotBlank(message = "{empresa.nmEmpresa.required}")
    @Size(max = 100, message = "{empresa.nmEmpresa.maxlength}")
    private String nmEmpresa;

    @Size(max = 18, message = "{empresa.dsCnpj.maxlength}")
    private String dsCnpj;

    @Size(max = 200, message = "{empresa.dsPoliticaHibrida.maxlength}")
    private String dsPoliticaHibrida;

    private LocalDateTime dtCadastro;

    public Long getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }
    public String getNmEmpresa() { return nmEmpresa; }
    public void setNmEmpresa(String nmEmpresa) { this.nmEmpresa = nmEmpresa; }
    public String getDsCnpj() { return dsCnpj; }
    public void setDsCnpj(String dsCnpj) { this.dsCnpj = dsCnpj; }
    public String getDsPoliticaHibrida() { return dsPoliticaHibrida; }
    public void setDsPoliticaHibrida(String dsPoliticaHibrida) { this.dsPoliticaHibrida = dsPoliticaHibrida; }
    public LocalDateTime getDtCadastro() { return dtCadastro; }
    public void setDtCadastro(LocalDateTime dtCadastro) { this.dtCadastro = dtCadastro; }
}
