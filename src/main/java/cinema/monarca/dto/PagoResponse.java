package cinema.monarca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta del sistema de pago simulado.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PagoResponse {
    private boolean aprobado;
    private String  referencia;
    private String  mensaje;
    private String  tipoPago;
    private Double  montoTotal;
    private Long    transNo;
    /** Últimos 4 dígitos (para mostrar en el recibo) */
    private String  ultimos4;
}
