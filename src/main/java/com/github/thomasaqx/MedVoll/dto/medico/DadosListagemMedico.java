package com.github.thomasaqx.MedVoll.dto.medico;

import com.github.thomasaqx.MedVoll.domain.medico.Especialidade;
import com.github.thomasaqx.MedVoll.domain.medico.Medico;

public record DadosListagemMedico(
        String nome,
        String email,
        String crm,
        Especialidade especialidade) {


    public DadosListagemMedico(Medico medico) {
        this(
                medico.getNome()
                , medico.getEmail()
                , medico.getCrm()
                , medico.getEspecialidade());
    }
}
