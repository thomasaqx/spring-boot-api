package com.github.thomasaqx.MedVoll.domain.endereco;

import com.github.thomasaqx.MedVoll.dto.endereco.DadosEndereco;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    private String logradouro;
    private String bairro;
    private String cep;
    private String numero;
    private String complemento;
    private String uf;
    private String cidade;

    //Cria um objeto dados do tipo DadosEndereço,
    // onde o objeto dados possui os mesmos atributos,
    // que serão posteriormente escritos na requisição.
    public Endereco(DadosEndereco dados) {
        this.logradouro = dados.logradouro();
        this.bairro = dados.bairro();
        this.cep = dados.cep();
        this.numero = dados.numero();
        this.complemento = dados.complemento();
        this.uf = dados.uf();
        this.cidade = dados.cidade();
    }
}
