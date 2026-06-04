package com.sistemadecadastramento.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemadecadastramento.dtos.DadosLoginDto;
import com.sistemadecadastramento.dtos.TokenResponseDto;
import com.sistemadecadastramento.infra.security.TokenService;
import com.sistemadecadastramento.models.Usuario;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
public class AuthController {
    
    @Autowired
    private AuthenticationManager manager;
    
    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<TokenResponseDto> efetuarLogin(@RequestBody @Valid DadosLoginDto dados){
        var tokenSpring = new UsernamePasswordAuthenticationToken(dados.getEmail(), dados.getSenha());

        var authentication = manager.authenticate(tokenSpring);

        var usuarioLogado = (Usuario) authentication.getPrincipal();

        var tokenJWT = tokenService.gerarToken(usuarioLogado);

        return ResponseEntity.ok(new TokenResponseDto(tokenJWT));
    }
}