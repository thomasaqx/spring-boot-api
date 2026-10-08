package com.github.thomasaqx.MedVoll.controller;

import com.github.thomasaqx.MedVoll.domain.usuario.Usuario;
import com.github.thomasaqx.MedVoll.dto.auth.DTOAuth;
import com.github.thomasaqx.MedVoll.dto.auth.DTOTokenJWT;
import com.github.thomasaqx.MedVoll.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;


    @PostMapping
    //Recebe apenas o body login e senha na requisição.
    public ResponseEntity login (@RequestBody @Valid DTOAuth dtoAuth) {
        //Cria variável authToken recebendo um objeto com os dados do body(DTOAuth)
        var authToken = new UsernamePasswordAuthenticationToken(dtoAuth.email(), dtoAuth.senha());
        var authentication = manager.authenticate(authToken);

        var tokenJWT = tokenService.gerarTokenJwt((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new DTOTokenJWT(tokenJWT));

    }
}
