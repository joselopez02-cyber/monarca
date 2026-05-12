package com.cinemamonarca.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data @AllArgsConstructor
public class SillaDTO {
    private Long sillaId;
    private String fila;
    private Integer numero;
    private String codigo;   // "A3"
    private String estado;   // DISPONIBLE / OCUPADA / RESERVADA
    private Long reservaId;
}
