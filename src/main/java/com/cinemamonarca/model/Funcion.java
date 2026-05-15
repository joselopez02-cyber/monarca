package com.cinemamonarca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funcion",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"sala_id", "fecha", "hora_inicio"},
                name = "uk_funcion_sala_fecha_hora"))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Funcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "funcion_id")
    private Long funcionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnoreProperties({"funciones","hibernateLazyInitializer","handler"})
    private Pelicula pelicula;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    @JsonIgnoreProperties({"funciones","hibernateLazyInitializer","handler","cine"})
    private Sala sala;

    /**
     * Fecha de esta proyección específica (yyyy-MM-dd).
     * Debe estar dentro del rango [fechaInicio, fechaFin].
     */
    @Column(nullable = false, length = 20)
    private String fecha;

    /** Hora de inicio de esta proyección (HH:mm) */
    @Column(name = "hora_inicio", nullable = false, length = 10)
    private String horaInicio;

    /**
     * Primera fecha en que esta función aparece en cartelera (yyyy-MM-dd).
     * Junto con fechaFin define el rango de compra.
     */
    @Column(name = "fecha_inicio", length = 20)
    private String fechaInicio;

    /**
     * Última fecha en que esta función aparece en cartelera (yyyy-MM-dd).
     * No se pueden hacer reservas fuera de este rango.
     */
    @Column(name = "fecha_fin", length = 20)
    private String fechaFin;

    @Column(name = "precio_boleto", nullable = false)
    private Double precioBoleto;

    @Column(name = "capacidad_total", nullable = false)
    private Integer capacidadTotal;

    @Column(name = "asientos_disponibles", nullable = false)
    private Integer asientosDisponibles;

    /**
     * Sillas disponibles para ESTA función específica.
     * Cada Funcion tiene su propio set de sillas (por fecha+hora),
     * lo que permite gestionar disponibilidad independientemente.
     */
    @OneToMany(mappedBy = "funcion", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Silla> sillas = new ArrayList<>();

    // ── Helpers ────────────────────────────────────────────────────────────

    @Transient
    public String getUbicacion() {
        if (sala == null) return "Sin sala";
        return sala.getNombre() + " — " + sala.getTipo();
    }

    /**
     * Verifica si una fecha dada está dentro del rango de compra de esta función.
     * Se usa en ReservaService para validar que la reserva es válida.
     */
    @Transient
    public boolean estaEnRango(String fechaCompra) {
        if (fechaInicio == null || fechaFin == null) return true;
        return fechaCompra.compareTo(fechaInicio) >= 0
                && fechaCompra.compareTo(fechaFin)    <= 0;
    }

    @PrePersist
    @PreUpdate
    public void syncCapacidad() {
        if (sala != null && sala.getCapacidad() != null) {
            if (capacidadTotal == null)      capacidadTotal = sala.getCapacidad();
            if (asientosDisponibles == null) asientosDisponibles = capacidadTotal;
        }
    }
}