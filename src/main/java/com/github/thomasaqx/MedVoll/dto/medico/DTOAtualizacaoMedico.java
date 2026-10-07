package com.github.thomasaqx.MedVoll.dto.medico;

import com.github.thomasaqx.MedVoll.dto.endereco.DTOEndereco;
import jakarta.validation.constraints.NotNull;

public record DTOAtualizacaoMedico(
        @NotNull
        Long id,
        String nome,
        String telefone,
        String email,
        DTOEndereco endereco) {
}
