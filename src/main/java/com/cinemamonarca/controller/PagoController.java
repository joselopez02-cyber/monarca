package com.cinemamonarca.controller;

import com.cinemamonarca.dto.PagoRequest;
import com.cinemamonarca.dto.PagoResponse;
import com.cinemamonarca.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API de pagos SIMULADOS — solo para fines académicos.
 *
 * POST /api/pagos/procesar   → procesa un pago de prueba
 * GET  /api/pagos/metodos    → lista los métodos disponibles
 *
 * NOTA: Ningún dato financiero es real ni se envía a pasarelas externas.
 */
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagoController {

    private final PagoService pagoService;

    /**
     * Procesa un pago simulado.
     * Usar tarjeta que empieza en "0000 ..." para simular rechazo.
     */
    @PostMapping("/procesar")
    public ResponseEntity<PagoResponse> procesar(@RequestBody PagoRequest req) {
        PagoResponse resp = pagoService.procesarPago(req);
        return resp.isAprobado()
                ? ResponseEntity.ok(resp)
                : ResponseEntity.badRequest().body(resp);
    }

    /** Lista los métodos de pago disponibles en la demo. */
    @GetMapping("/metodos")
    public ResponseEntity<?> metodos() {
        return ResponseEntity.ok(new String[]{
            "TARJETA_CREDITO",
            "TARJETA_DEBITO",
            "NEQUI",
            "PSE"
        });
    }
}
