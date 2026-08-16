package com.sistemadecadastramento.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemadecadastramento.dtos.DadosLoginDto;
import com.sistemadecadastramento.dtos.SenhaRequestDto;
import com.sistemadecadastramento.dtos.TokenResponseDto;
import com.sistemadecadastramento.infra.security.TokenService;
import com.sistemadecadastramento.models.Usuario;
import com.sistemadecadastramento.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final UsuarioService usuarioService;
    
    private final AuthenticationManager manager;
    
    private final TokenService tokenService;

    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponseDto> efetuarLogin(@RequestBody @Valid DadosLoginDto dados){
        var tokenSpring = new UsernamePasswordAuthenticationToken(dados.getEmail(), dados.getSenha());

        var authentication = manager.authenticate(tokenSpring);

        var usuarioLogado = (Usuario) authentication.getPrincipal();

        var tokenJWT = tokenService.gerarToken(usuarioLogado);

        return ResponseEntity.ok(new TokenResponseDto(tokenJWT));
    }

    @PostMapping("/auth/esqueciSenha")
    public ResponseEntity<String> alterarSenha(@RequestBody @Valid SenhaRequestDto dados){

        usuarioService.alterarSenha(dados);

        return ResponseEntity.ok("Senha Alterada com Sucesso");
    }
}