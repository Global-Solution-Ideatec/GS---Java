package br.com.fiap.dto;

import br.com.fiap.model.Tarefa;

public class TarefaMapper {
    public static TarefaDTO toDTO(Tarefa t) {
        if (t == null) return null;
        TarefaDTO dto = new TarefaDTO();
        dto.setIdTarefa(t.getIdTarefa());
        dto.setDsTarefa(t.getDsTarefa());
        dto.setDsArea(t.getDsArea());
        dto.setDtCriacao(t.getDtCriacao());
        dto.setStTarefa(t.getStTarefa());
        if (t.getGestor() != null) dto.setIdGestor(t.getGestor().getIdUsuario());
        if (t.getColaborador() != null) dto.setIdColaborador(t.getColaborador().getIdUsuario());
        return dto;
    }

    public static Tarefa fromCreateDTO(br.com.fiap.dto.TarefaCreateDTO dto) {
        if (dto == null) return null;
        Tarefa t = new Tarefa();
        t.setDsTarefa(dto.getDsTarefa());
        t.setDsArea(dto.getDsArea());
        t.setStTarefa(dto.getStTarefa() != null ? dto.getStTarefa() : "Pendente");
        // gestor/colaborador devem ser setados no controller a partir de ids
        return t;
    }
}

