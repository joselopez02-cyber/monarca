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
@Table(name = "funcion")
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

    @Column(nullable = false, length = 20)
    private String fecha;

    @Column(name = "hora_inicio", nullable = false, length = 10)
    private String horaInicio;

    @Column(name = "precio_boleto", nullable = false)
    private Double precioBoleto;

    @Column(name = "capacidad_total", nullable = false)
    private Integer capacidadTotal;

    @Column(name = "asientos_disponibles", nullable = false)
    private Integer asientosDisponibles;

    @OneToMany(mappedBy = "funcion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Silla> sillas = new ArrayList<>();

    @Transient
    public String getUbicacion() {
        if (sala == null) return "Sin sala";
        return sala.getNombre() + " — " + sala.getTipo();
    }

    @PrePersist @PreUpdate
    public void syncCapacidad() {
        if (sala != null && sala.getCapacidad() != null) {
            if (capacidadTotal == null)      capacidadTotal = sala.getCapacidad();
            if (asientosDisponibles == null) asientosDisponibles = capacidadTotal;
        }
    }
}