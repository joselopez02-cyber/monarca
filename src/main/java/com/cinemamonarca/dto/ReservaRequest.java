package com.cinemamonarca.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReservaRequest {
    /** ID del cliente que realiza la reserva */
    private Long custId;
    /** ID de la función (proyección) seleccionada */
    private Long funcionId;
    private String nombre;
    private String contNum;
    private String fecha;
    private String tiempo;
    /** Códigos de asiento a reservar: ["A1","B3","C10"] */
    private List<String> sillas;
}
