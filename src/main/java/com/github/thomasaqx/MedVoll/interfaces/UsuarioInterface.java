package com.github.thomasaqx.MedVoll.interfaces;

import com.github.thomasaqx.MedVoll.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioInterface extends JpaRepository <Usuario, Long>{


    UserDetails findByLogin(String login);
}
