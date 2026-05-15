package com.cinemamonarca.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO para programar una película en cartelera durante un rango de fechas
 * con múltiples horarios por día.
 *
 * Ejemplo:
 * {
 *   "movieId": 1,
 *   "salaId": 2,
 *   "fechaInicio": "2026-05-19",
 *   "fechaFin":    "2026-05-25",
 *   "horarios":    ["09:00","12:00","15:00","18:00","21:00"],
 *   "precioBoleto": 18000
 * }
 *
 * El servicio generará automáticamente 1 función por horario × día del rango.
 * Con 5 horarios y 7 días → 35 funciones creadas en un solo POST.
 */
@Data
public class ProgramacionRequest {

    /** ID de la película */
    private Long movieId;

    /** ID de la sala donde se proyecta */
    private Long salaId;

    /** Primera fecha de exhibición (inclusive) en formato yyyy-MM-dd */
    private String fechaInicio;

    /** Última fecha de exhibición (inclusive) en formato yyyy-MM-dd */
    private String fechaFin;

    /**
     * Horarios de inicio de cada función durante el día.
     * Formato HH:mm — ejemplo: ["09:00","12:00","15:00","18:00","21:00"]
     * Máximo recomendado: 8 horarios por día.
     */
    private List<String> horarios;

    /** Precio por boleto en COP */
    private Double precioBoleto;
}
