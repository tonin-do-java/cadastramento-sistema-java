package com.sistemadecadastramento.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notificacao")
public class Notificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role", nullable = false)
    private Roles role;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "texto", nullable = false)
    private String texto;

    @Column(name = "dataHora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "foiLida", nullable = false)
    private Boolean foiLida;

    @Column(name = "id_referencia", nullable = false)
    private Long idReferencia;

    @Column(name = "tipo_notificacao", nullable = false)
    private TipoNotificacao tipoNotificacao;
}
