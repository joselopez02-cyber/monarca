package com.cinemamonarca.dto;

import lombok.Data;

/**
 * DTO para el sistema de pago simulado (solo para fines académicos / demo).
 * NUNCA se procesan datos financieros reales.
 */
@Data
public class PagoRequest {

    /** custId del cliente que paga */
    private Long custId;

    /** resCode de la reserva asociada */
    private Long resCode;

    /** Método de pago: TARJETA_CREDITO | TARJETA_DEBITO | NEQUI | PSE */
    private String tipoPago;

    // ── Datos de tarjeta (solo para demo, nunca se almacenan completos) ──
    /** Número de tarjeta de 16 dígitos (se guarda solo los últimos 4) */
    private String numeroTarjeta;

    /** Nombre en la tarjeta */
    private String nombreTarjeta;

    /** Fecha de vencimiento MM/AA */
    private String vencimiento;

    /** CVV de 3 dígitos (no se almacena) */
    private String cvv;

    // ── PSE / Nequi ──
    /** Número de celular (para Nequi) */
    private String celular;

    /** Banco seleccionado (para PSE) */
    private String banco;

    /** Monto total a pagar */
    private Double montoTotal;
}
