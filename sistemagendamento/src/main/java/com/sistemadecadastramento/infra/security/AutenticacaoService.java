package com.sistemadecadastramento.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.sistemadecadastramento.services.UsuarioService;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioService service;
    
    @Override
    public UserDetails loadUserByUsername(String username){
      
        return service.buscarPorEmail(username);
    }
    
}
