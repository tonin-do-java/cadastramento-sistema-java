package com.sistemadecadastramento.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    private long id;
    private String nome;
    private String email;
    private String senhaHash;
    
}