package com.github.thomasaqx.MedVoll.controller;

import com.github.thomasaqx.MedVoll.dto.medico.DadosCadastroMedico;
import com.github.thomasaqx.MedVoll.domain.medico.Medico;
import com.github.thomasaqx.MedVoll.dto.medico.DadosListagemMedico;
import com.github.thomasaqx.MedVoll.repository.MedicoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
@Transactional
public class MedicoController {
    @Autowired
    private MedicoRepository repository;

    @PostMapping
    public void cadastrar(@RequestBody @Valid DadosCadastroMedico dados) {
        repository.save(new Medico(dados));
    }

    @GetMapping
    public Page<DadosListagemMedico> listar(Pageable paginacao) {
        return repository.findAll(paginacao).map(DadosListagemMedico::new);
    }

}
