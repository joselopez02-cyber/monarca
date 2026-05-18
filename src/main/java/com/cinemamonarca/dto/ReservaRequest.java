package com.cinemamonarca.dto;

import lombok.Data;
import java.util.List;


@Data
public class ReservaRequest {
    private Long custId;
    private Long funcionId;
    private String nombre;
    private String contNum;
    private String fecha;
    private String tiempo;
    private List<String> sillas;
}