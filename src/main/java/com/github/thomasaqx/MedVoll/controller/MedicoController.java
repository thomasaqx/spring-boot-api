package com.github.thomasaqx.MedVoll.controller;

import com.github.thomasaqx.MedVoll.dto.medico.DTOAtualizacaoMedico;
import com.github.thomasaqx.MedVoll.dto.medico.DTOCadastroMedico;
import com.github.thomasaqx.MedVoll.domain.medico.Medico;
import com.github.thomasaqx.MedVoll.dto.medico.DTODetalhamentoMedico;
import com.github.thomasaqx.MedVoll.dto.medico.DTOListagemMedico;
import com.github.thomasaqx.MedVoll.interfaces.MedicoInterface;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/medicos")
@Transactional
public class MedicoController {
    @Autowired
    private MedicoInterface repository;

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid DTOCadastroMedico data, UriComponentsBuilder uriBuilder) {
        var medico = new Medico(data);
        repository.save(medico);
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DTODetalhamentoMedico(medico));
    }

    @GetMapping
    public ResponseEntity<Page<DTOListagemMedico>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(DTOListagemMedico::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizarCadastro(@RequestBody @Valid DTOAtualizacaoMedico data) {
        Medico medico = repository.getReferenceById(data.id());
        medico.atualizarInfo(data);
        return ResponseEntity.ok(new DTODetalhamentoMedico(medico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity deletarCadastro(@PathVariable Long id) {
        Medico medico = repository.getReferenceById(id);
        medico.excluir();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        return ResponseEntity.ok(new DTODetalhamentoMedico(medico));
    }
}
