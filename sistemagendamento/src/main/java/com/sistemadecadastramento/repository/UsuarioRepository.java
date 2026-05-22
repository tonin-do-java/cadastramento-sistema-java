package com.sistemadecadastramento.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemadecadastramento.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}
