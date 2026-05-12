package com.cinemamonarca.controller;

import com.cinemamonarca.model.Funcion;
import com.cinemamonarca.service.FuncionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * API de Funciones (proyecciones).
 *
 * GET  /api/funciones                      → todas las funciones
 * GET  /api/funciones/{id}                 → detalle
 * GET  /api/funciones/pelicula/{movieId}   → funciones de una película
 * GET  /api/funciones/sala/{salaId}        → funciones de una sala
 * GET  /api/funciones/fecha/{fecha}        → funciones en una fecha (yyyy-MM-dd)
 * POST /api/funciones                      → crear función
 * PUT  /api/funciones/{id}                 → actualizar fecha/hora/precio
 * DELETE /api/funciones/{id}              → eliminar
 */
@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FuncionController {

    private final FuncionService funcionService;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Funcion>> listar() {
        return ResponseEntity.ok(funcionService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Funcion> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(funcionService.obtenerPorId(id));
    }

    @GetMapping("/pelicula/{movieId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Funcion>> porPelicula(@PathVariable Long movieId) {
        return ResponseEntity.ok(funcionService.porPelicula(movieId));
    }

    @GetMapping("/sala/{salaId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Funcion>> porSala(@PathVariable Long salaId) {
        return ResponseEntity.ok(funcionService.porSala(salaId));
    }

    @GetMapping("/fecha/{fecha}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Funcion>> porFecha(@PathVariable String fecha) {
        return ResponseEntity.ok(funcionService.porFecha(fecha));
    }

    @PostMapping
    public ResponseEntity<Funcion> crear(@RequestBody Funcion funcion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionService.guardar(funcion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcion> actualizar(@PathVariable Long id, @RequestBody Funcion datos) {
        return ResponseEntity.ok(funcionService.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        funcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}