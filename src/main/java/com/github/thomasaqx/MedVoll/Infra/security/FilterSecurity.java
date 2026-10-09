package com.github.thomasaqx.MedVoll.Infra.security;

import com.github.thomasaqx.MedVoll.interfaces.UsuarioInterface;
import com.github.thomasaqx.MedVoll.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FilterSecurity extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioInterface usuarioInterface;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        var tokenJWT = recuperarToken(request);

        //Sem token (ex: /login) a requisição segue sem usuário; o Spring decide se a rota é pública.
        if (tokenJWT != null) {
            try {
                var subject = tokenService.getSubject(tokenJWT);
                var usuario = usuarioInterface.findByLogin(subject);

                //Marca o usuário do token como autenticado nesta requisição.
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException exception) {
                //Token inválido ou expirado: segue sem autenticar, e a rota protegida responde 403.
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) {
        return authorizationHeader.replace("Bearer ", "");
        }
            return null;
    }
}
