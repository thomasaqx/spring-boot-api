package com.github.thomasaqx.MedVoll.Infra.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratamentoDeErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarErro404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DTOErroValidacao>> tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DTOErroValidacao::new).toList());
    }

    //Login ou senha inválidos no /login: 401 em vez do 403 padrão do Spring Security.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity tratarErro401() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private record DTOErroValidacao(
            String campo,
            String mensagem) {
        public DTOErroValidacao(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}
