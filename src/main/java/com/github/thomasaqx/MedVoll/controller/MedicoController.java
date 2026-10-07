package com.github.thomasaqx.MedVoll.controller;

import com.github.thomasaqx.MedVoll.dto.medico.DTOAtualizacaoMedico;
import com.github.thomasaqx.MedVoll.dto.medico.DTOCadastroMedico;
import com.github.thomasaqx.MedVoll.domain.medico.Medico;
import com.github.thomasaqx.MedVoll.dto.medico.DTOListagemMedico;
import com.github.thomasaqx.MedVoll.repository.MedicoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicos")
@Transactional
public class MedicoController {
    @Autowired
    private MedicoRepository repository;

    @PostMapping
    public void cadastrar(@RequestBody @Valid DTOCadastroMedico data) {
        repository.save(new Medico(data));
    }

    @GetMapping
    public Page<DTOListagemMedico> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        return repository.findAll(paginacao).map(DTOListagemMedico::new);
    }
    @PutMapping
    @Transactional
    public void atualizarCadastro(@RequestBody @Valid DTOAtualizacaoMedico data) {
        Medico medico = repository.getReferenceById(data.id());
        medico.atualizarInfo(data);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void deletarCadastro(@PathVariable Long id){
        repository.deleteById(id);
    }

}
