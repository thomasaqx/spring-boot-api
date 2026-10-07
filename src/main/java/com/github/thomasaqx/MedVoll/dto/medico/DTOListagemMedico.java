package com.github.thomasaqx.MedVoll.dto.medico;

import com.github.thomasaqx.MedVoll.domain.medico.Especialidade;
import com.github.thomasaqx.MedVoll.domain.medico.Medico;

public record DTOListagemMedico(
        Long id,
        String nome,
        String email,
        String crm,
        Especialidade especialidade) {


    public DTOListagemMedico(Medico medico) {
        this(
                medico.getId(),
                medico.getNome(),
                medico.getEmail(),
                medico.getCrm(),
                medico.getEspecialidade());
    }
}
