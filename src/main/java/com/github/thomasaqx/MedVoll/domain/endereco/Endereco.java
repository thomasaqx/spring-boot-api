package com.github.thomasaqx.MedVoll.domain.endereco;

import com.github.thomasaqx.MedVoll.dto.endereco.DTOEndereco;
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
    public Endereco(DTOEndereco dados) {
        this.logradouro = dados.logradouro();
        this.bairro = dados.bairro();
        this.cep = dados.cep();
        this.numero = dados.numero();
        this.complemento = dados.complemento();
        this.uf = dados.uf();
        this.cidade = dados.cidade();
    }

    public void atualizarInfoEndereco(DTOEndereco data){
        if (data.logradouro() != null) {
            this.logradouro = data.logradouro();
        }
        if (data.bairro() != null) {
            this.bairro = data.bairro();
        }
        if (data.cep() != null) {
            this.cep = data.cep();
        }
        if (data.uf() != null) {
            this.uf = data.uf();
        }
        if (data.cidade() != null) {
            this.cidade = data.cidade();
        }
        if (data.numero() != null) {
            this.numero = data.numero();
        }
        if (data.complemento() != null) {
            this.complemento = data.complemento();
        }
    }
}
