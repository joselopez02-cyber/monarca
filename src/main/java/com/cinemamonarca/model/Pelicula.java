package com.cinemamonarca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Catálogo de películas.
 * Los detalles de proyección (fecha, hora, sala, precio) viven en {@link Funcion}.
 */
@Entity
@Table(name = "pelicula")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    /** Duración en minutos — también accesible como duracionMinutos para compatibilidad frontend */
    @Column(name = "duracion_min")
    private Integer duracionMin;

    /** Alias para compatibilidad con el frontend (mapea al mismo campo) */
    @Transient
    public Integer getDuracionMinutos() { return duracionMin; }
    public void setDuracionMinutos(Integer v) { this.duracionMin = v; }

    /** Clasificación: G, PG, PG-13, R */
    @Column(length = 10)
    private String clasificacion;

    /** Género: ACCION, DRAMA, COMEDIA, TERROR, ANIMACION, etc. */
    @Column(length = 50)
    private String genero;

    @OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Funcion> funciones = new ArrayList<>();
}
