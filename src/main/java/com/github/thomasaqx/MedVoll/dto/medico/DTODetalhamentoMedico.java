package com.github.thomasaqx.MedVoll.dto.medico;

import com.github.thomasaqx.MedVoll.domain.endereco.Endereco;
import com.github.thomasaqx.MedVoll.domain.medico.Especialidade;
import com.github.thomasaqx.MedVoll.domain.medico.Medico;

public record DTODetalhamentoMedico(
        Long id,
        String nome,
        String email,
        String crm,
        Especialidade especialidade,
        Endereco endereco
) {
    public DTODetalhamentoMedico(Medico medico) {
        this(
                medico.getId(),
                medico.getNome(),
                medico.getEmail(),
                medico.getCrm(),
                medico.getEspecialidade(),
                medico.getEndereco());
    }
}
