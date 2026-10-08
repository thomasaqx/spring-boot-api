package com.github.thomasaqx.MedVoll.domain.usuario;


import com.github.thomasaqx.MedVoll.dto.usuario.DTOUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "usuarios") //Mapeia a tabela no banco de dados (ela precisa estar criada)
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor //Cria um construtor vazio.
@AllArgsConstructor //Cria um construtor com todos os atributos.
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String senha;

    public Usuario(DTOUsuario usuario) {
        this.id = usuario.id();
        this.login = usuario.login();
        this.senha = usuario.senha();
    }

    //Perfis de acesso do usuário. Por enquanto todos são USER.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    //O Spring Security lê a senha e o login por estes métodos.
    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }
}
