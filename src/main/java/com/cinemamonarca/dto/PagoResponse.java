package com.cinemamonarca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Respuesta completa del pago simulado.
 * Incluye todos los datos de la venta para mostrar en el recibo
 * y en el historial de ventas, incluso si la función ya fue eliminada.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PagoResponse {

    // ── Resultado del pago ───────────────────────────────────────────────
    private boolean aprobado;
    private String  referencia;
    private String  mensaje;
    private String  tipoPago;
    private Double  montoTotal;
    private Long    transNo;
    private String  ultimos4;
    private String  estadoPago;

    // ── Datos de la reserva ──────────────────────────────────────────────
    private Long    resCode;
    private String  nombreCliente;
    private String  fechaReserva;

    // ── Datos de la función (desde snap_* si ya fue eliminada) ───────────
    private String       peliculaNombre;
    private String       salaNombre;
    private String       tipoSala;
    private String       funcionFecha;
    private String       funcionHora;
    private List<String> sillasReservadas;
    private Double       precioBoleto;
}