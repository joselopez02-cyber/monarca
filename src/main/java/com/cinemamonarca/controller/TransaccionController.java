package com.cinemamonarca.controller;

import com.cinemamonarca.model.Transaccion;
import com.cinemamonarca.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoint de transacciones (pagos).
 *
 * Flujo esperado:
 *   POST /api/reservas      → crea la reserva
 *   POST /api/transacciones → registra el pago vinculado a esa reserva
 *
 * El campo tipoPago es OBLIGATORIO. Valores validos del enum TipoPago:
 *   TARJETA_CREDITO | TARJETA_DEBITO | NEQUI | PSE
 */
@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransaccionController {

    private final TransaccionService transaccionService;

    @GetMapping
    public ResponseEntity<List<Transaccion>> obtenerTodas() {
        return ResponseEntity.ok(transaccionService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaccion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(transaccionService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{custId}")
    public ResponseEntity<List<Transaccion>> obtenerPorCliente(@PathVariable Long custId) {
        return ResponseEntity.ok(transaccionService.obtenerPorCliente(custId));
    }

    /**
     * Crea una transaccion de pago vinculada a un cliente y una reserva.
     *
     * @param transaccion  cuerpo JSON con tipoPago (obligatorio) y demas campos
     * @param custId       ID del cliente (query param obligatorio)
     * @param resCode      ID de la reserva (query param obligatorio)
     */
    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody Transaccion transaccion,
            @RequestParam(required = true) Long custId,
            @RequestParam(required = true) Long resCode) {
        try {
            if (transaccion.getTipoPago() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "El campo tipoPago es obligatorio. " +
                                "Valores validos: TARJETA_CREDITO, TARJETA_DEBITO, NEQUI, PSE"));
            }
            Transaccion saved = transaccionService.guardar(transaccion, custId, resCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        transaccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}