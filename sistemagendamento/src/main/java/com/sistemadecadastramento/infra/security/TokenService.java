package com.sistemadecadastramento.infra.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


import com.sistemadecadastramento.models.Usuario;

@Service
public class TokenService {
    
    @Value("${api.security.token.secret}")
    private String chaveApi;

    public String gerarToken(Usuario usuario){
        SecretKey key = Keys.hmacShaKeyFor(chaveApi.getBytes());

        return Jwts.builder()
                .subject(usuario.getId().toString())
                .claim("email", usuario.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000L))
                .signWith(key)
                .compact();
    }

    public String validarToken(String token){
        SecretKey key = Keys.hmacShaKeyFor(chaveApi.getBytes());

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
