package cinema.monarca.model;

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

    /** Método de pago simulado */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false)
    private TipoPago tipoPago;

    @Column(name = "pago_total")
    private BigDecimal pagoTotal;

    /**
     * Estado del pago simulado.
     * SIMULADO = pago de prueba (siempre aprobado en demo).
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false)
    private EstadoPago estadoPago = EstadoPago.SIMULADO;

    /** Código de referencia generado automáticamente (ej: "MON-2025-000123") */
    @Column(name = "referencia", length = 50)
    private String referencia;

    /** Últimos 4 dígitos de la tarjeta (solo para demo, nunca datos reales) */
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
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "res_code")
    private Reserva reserva;

    public enum TipoPago {
        TARJETA_CREDITO,
        TARJETA_DEBITO,
        NEQUI,
        PSE
    }

    public enum EstadoPago {
        SIMULADO,   // pago de prueba — siempre aprobado
        APROBADO,
        RECHAZADO
    }
}
