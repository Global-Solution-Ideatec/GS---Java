package br.com.fiap.dto;

import jakarta.validation.constraints.NotBlank;

public class RecomendacaoRequestDTO {
    @NotBlank(message = "{recomendacao.area.required}")
    private String area;

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
}
