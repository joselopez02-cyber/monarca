package com.cinemamonarca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sala")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sala_id")
    private Long salaId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoSala tipo;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = true)
    private Integer filas;

    @Column(nullable = true)
    private Integer columnas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","salas","cine"})
    private Sucursal sucursal;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Funcion> funciones = new ArrayList<>();

    public enum TipoSala { PRO, TRES_D, DOS_D }

    @PrePersist @PreUpdate
    public void syncCapacidad() {
        if (filas != null && columnas != null) {
            this.capacidad = filas * columnas;
        } else if (tipo != null && (capacidad == null || capacidad == 0)) {
            this.capacidad = switch (tipo) {
                case PRO    -> 20;
                case TRES_D -> 30;
                case DOS_D  -> 50;
            };
        }
        if (tipo != null) {
            if (filas == null)    this.filas    = filasArr().length;
            if (columnas == null) this.columnas = columnasDefault();
        }
    }

    public String[] filasArr() {
        int n = (filas != null) ? Math.min(filas, 26) : switch (tipo) {
            case PRO           -> 4;
            case TRES_D, DOS_D -> 5;
        };
        String[] arr = new String[n];
        for (int i = 0; i < n; i++) arr[i] = String.valueOf((char)('A' + i));
        return arr;
    }

    public int columnasDefault() {
        if (columnas != null) return columnas;
        if (tipo == null) return 10;
        return switch (tipo) {
            case PRO    -> 5;
            case TRES_D -> 6;
            case DOS_D  -> 10;
        };
    }
}