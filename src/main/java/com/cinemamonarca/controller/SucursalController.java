package com.cinemamonarca.controller;

import com.cinemamonarca.model.Gestor;
import com.cinemamonarca.model.Sucursal;
import com.cinemamonarca.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public ResponseEntity<List<Sucursal>> obtenerTodas() {
        return ResponseEntity.ok(sucursalService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sucursal> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(id));
    }

    @GetMapping("/cine/{cineId}")
    public ResponseEntity<List<Sucursal>> obtenerPorCine(@PathVariable Long cineId) {
        return ResponseEntity.ok(sucursalService.obtenerPorCine(cineId));
    }

    @GetMapping("/{id}/gestores")
    public ResponseEntity<List<Gestor>> obtenerGestores(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.obtenerGestores(id));
    }

    @PostMapping
    public ResponseEntity<Sucursal> crear(
            @RequestBody Sucursal sucursal,
            @RequestParam Long cineId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sucursalService.guardar(sucursal, cineId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
