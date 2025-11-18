package br.com.fiap.dto;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class RecomendacaoDTO {
    private Long idRecomendacao;
    private Long idUsuario;
    private String dsRecomendacao;
    private LocalDateTime dtRecomendacao;
    private String tpRecomendacao;
    private String fatores; // novo campo

    public Long getIdRecomendacao() { return idRecomendacao; }
    public void setIdRecomendacao(Long idRecomendacao) { this.idRecomendacao = idRecomendacao; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getDsRecomendacao() { return dsRecomendacao; }
    public void setDsRecomendacao(String dsRecomendacao) { this.dsRecomendacao = dsRecomendacao; }
    public LocalDateTime getDtRecomendacao() { return dtRecomendacao; }
    public void setDtRecomendacao(LocalDateTime dtRecomendacao) { this.dtRecomendacao = dtRecomendacao; }
    public String getTpRecomendacao() { return tpRecomendacao; }
    public void setTpRecomendacao(String tpRecomendacao) { this.tpRecomendacao = tpRecomendacao; }
    public String getFatores() { return fatores; }
    public void setFatores(String fatores) { this.fatores = fatores; }
}
