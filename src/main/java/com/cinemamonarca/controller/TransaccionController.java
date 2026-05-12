package com.cinemamonarca.controller;

import com.cinemamonarca.model.Transaccion;
import com.cinemamonarca.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

    @PostMapping
    public ResponseEntity<Transaccion> crear(
            @RequestBody Transaccion transaccion,
            @RequestParam Long custId,
            @RequestParam Long resCode) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transaccionService.guardar(transaccion, custId, resCode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        transaccionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
