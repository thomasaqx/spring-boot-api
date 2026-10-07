package com.github.thomasaqx.MedVoll.domain.medico;

import com.github.thomasaqx.MedVoll.domain.endereco.Endereco;
import com.github.thomasaqx.MedVoll.dto.medico.DTOAtualizacaoMedico;
import com.github.thomasaqx.MedVoll.dto.medico.DTOCadastroMedico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Table(name = "medicos") //Mapeia a tabela no banco de dados (ela precisa estar criada)
@Entity(name = "Medico")
@Getter
@NoArgsConstructor //Cria um construtor vazio.
@AllArgsConstructor //Cria um construtor com todos os atributos.
@EqualsAndHashCode(of = "id")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String crm;

    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;

    @Embedded
    private Endereco endereco;

    public Medico(DTOCadastroMedico data) {
        this.nome = data.nome();
        this.email = data.email();
        this.telefone = data.telefone();
        this.crm = data.crm();
        this.especialidade = data.especialidade();
        //cria um novo objeto do tipo Endereco que chama o contrutor que espera o molde de enderços contido em data.
        this.endereco = new Endereco(data.endereco());
    }

    public void atualizarInfo(DTOAtualizacaoMedico data){
        if(data.nome() != null){
            this.nome = data.nome();
        }
        if(data.telefone() != null){
            this.telefone = data.telefone();
        }
        if(data.endereco() != null){
            this.endereco.atualizarInfoEndereco(data.endereco());
        }

    }
}
