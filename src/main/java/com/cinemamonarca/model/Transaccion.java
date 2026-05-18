package com.cinemamonarca.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "transaccion")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trans_no")
    private Long transNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false)
    private TipoPago tipoPago;

    @Column(name = "pago_total")
    private BigDecimal pagoTotal;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false)
    private EstadoPago estadoPago = EstadoPago.SIMULADO;

    @Column(name = "referencia", length = 50)
    private String referencia;

    @Column(name = "ultimos4", length = 4)
    private String ultimos4;

    @Column(name = "fecha_inicio")
    private String fechaInicio;

    @Column(name = "fecha_final")
    private String fechaFinal;

    @Column(name = "fecha_trans")
    private String fechaTrans;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Cliente cliente;

    // EAGER para poder leer los snap_* de la reserva sin lazy problems
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "res_code")
    @JsonIgnoreProperties({"sillas","funcion","hibernateLazyInitializer","handler"})
    private Reserva reserva;

    // ── Campos calculados para ventas ────────────────────────────────────
    // Resuelven datos de película, sala, fecha, hora y sillas
    // tanto si la función aún existe como si ya fue eliminada.

    @jakarta.persistence.Transient
    public String getPeliculaNombre() {
        if (reserva == null) return null;
        return reserva.getPeliculaNombre(); // usa snap si funcion == null
    }

    @jakarta.persistence.Transient
    public String getSalaNombre() {
        if (reserva == null) return null;
        return reserva.getSalaNombre();
    }

    @jakarta.persistence.Transient
    public String getTipoSala() {
        if (reserva == null) return null;
        return reserva.getTipoSala();
    }

    @jakarta.persistence.Transient
    public String getFuncionFecha() {
        if (reserva == null) return null;
        return reserva.getFuncionFecha();
    }

    @jakarta.persistence.Transient
    public String getFuncionHora() {
        if (reserva == null) return null;
        return reserva.getFuncionHora();
    }

    @jakarta.persistence.Transient
    public java.util.List<String> getSillasReservadas() {
        if (reserva == null) return java.util.List.of();
        return reserva.getSillasSeleccionadas();
    }

    @jakarta.persistence.Transient
    public double getTotalPagado() {
        if (pagoTotal != null) return pagoTotal.doubleValue();
        if (reserva != null)   return reserva.getTotal();
        return 0;
    }

    @jakarta.persistence.Transient
    public String getNombreCliente() {
        if (cliente != null) return cliente.getNombreCliente();
        if (reserva != null) return reserva.getNombre();
        return null;
    }

    public enum TipoPago {
        TARJETA_CREDITO,
        TARJETA_DEBITO,
        NEQUI,
        PSE
    }

    public enum EstadoPago {
        SIMULADO,
        APROBADO,
        RECHAZADO
    }
}