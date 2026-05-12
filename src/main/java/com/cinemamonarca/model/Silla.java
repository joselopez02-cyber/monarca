package com.cinemamonarca.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Asiento dentro de una {@link Funcion} específica.
 * Un mismo asiento físico (ej: A3) tiene un registro por cada función,
 * lo que permite gestionar disponibilidad independientemente por horario.
 */
@Entity
@Table(name = "silla",
       uniqueConstraints = @UniqueConstraint(columnNames = {"funcion_id", "fila", "numero"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Silla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "silla_id")
    private Long sillaId;

    @Column(nullable = false, length = 2)
    private String fila;       // A, B, C, D, E

    @Column(nullable = false)
    private Integer numero;    // 1 – 10

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.DISPONIBLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcion_id", nullable = false)
    private Funcion funcion;

    /** Reserva que ocupa este asiento (null si está disponible) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    public enum Estado { DISPONIBLE, OCUPADA, RESERVADA }

    /** Código legible: "A3", "F10" */
    @Transient
    public String getCodigo() { return fila + numero; }
}
