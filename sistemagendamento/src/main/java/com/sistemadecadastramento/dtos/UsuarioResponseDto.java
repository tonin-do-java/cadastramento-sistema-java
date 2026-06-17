package com.sistemadecadastramento.dtos;


import com.sistemadecadastramento.models.Roles;
import com.sistemadecadastramento.models.Usuario;

import lombok.Getter;

@Getter
public class UsuarioResponseDto{
    private Long id;
    private String nome;
    private String email;
    private Roles role;

    public UsuarioResponseDto(Usuario usuarioEntity){
        this.id = usuarioEntity.getId();
        this.nome = usuarioEntity.getNome();
        this.email = usuarioEntity.getEmail();
        this.role = usuarioEntity.getRole();
    }
}
