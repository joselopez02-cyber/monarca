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

    @Column(name = "duracion_min")
    private Integer duracionMin;

    @Transient
    public Integer getDuracionMinutos() { return duracionMin; }
    public void setDuracionMinutos(Integer v) { this.duracionMin = v; }

    @Column(length = 10)
    private String clasificacion;

    @Column(length = 50)
    private String genero;

    /**
     * Poster en Base64 (data:image/...;base64,...).
     * ✅ TEXT en PostgreSQL (equivalente a MEDIUMTEXT en MySQL)
     */
    @Column(name = "poster_url", columnDefinition = "TEXT")
    private String posterUrl;

    @OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Funcion> funciones = new ArrayList<>();
}