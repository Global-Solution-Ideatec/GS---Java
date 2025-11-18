package br.com.fiap.dto;

import br.com.fiap.model.Empresa;

public class EmpresaMapper {
    public static EmpresaDTO toDTO(Empresa e) {
        if (e == null) return null;
        EmpresaDTO dto = new EmpresaDTO();
        dto.setIdEmpresa(e.getIdEmpresa());
        dto.setNmEmpresa(e.getNmEmpresa());
        dto.setDsCnpj(e.getDsCnpj());
        dto.setDsPoliticaHibrida(e.getDsPoliticaHibrida());
        dto.setDtCadastro(e.getDtCadastro());
        return dto;
    }

    public static Empresa fromDTO(EmpresaDTO dto) {
        if (dto == null) return null;
        Empresa e = new Empresa();
        e.setIdEmpresa(dto.getIdEmpresa());
        e.setNmEmpresa(dto.getNmEmpresa());
        e.setDsCnpj(dto.getDsCnpj());
        e.setDsPoliticaHibrida(dto.getDsPoliticaHibrida());
        e.setDtCadastro(dto.getDtCadastro());
        return e;
    }
}

