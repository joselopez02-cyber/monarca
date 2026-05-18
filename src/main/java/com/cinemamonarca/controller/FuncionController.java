package com.cinemamonarca.controller;

import com.cinemamonarca.dto.ProgramacionRequest;
import com.cinemamonarca.model.Funcion;
import com.cinemamonarca.service.FuncionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FuncionController {

    private final FuncionService funcionService;

    // ── GET ───────────────────────────────────────────────────────────────

    /** Lista todas las funciones con pelicula + sala cargados */
    @GetMapping
    public ResponseEntity<List<Funcion>> listar() {
        return ResponseEntity.ok(funcionService.obtenerTodas());
    }

    /** Lista solo funciones de hoy en adelante (cartelera pública) */
    @GetMapping("/vigentes")
    public ResponseEntity<List<Funcion>> listarVigentes() {
        return ResponseEntity.ok(funcionService.obtenerDesdeHoy());
    }

    /** Obtiene una función por ID */
    @GetMapping("/{id}")
    public ResponseEntity<Funcion> obtener(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(funcionService.obtenerPorId(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Funciones de una película específica */
    @GetMapping("/pelicula/{movieId}")
    public ResponseEntity<List<Funcion>> porPelicula(@PathVariable Long movieId) {
        return ResponseEntity.ok(funcionService.porPelicula(movieId));
    }

    /** Funciones de una sala específica */
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<Funcion>> porSala(@PathVariable Long salaId) {
        return ResponseEntity.ok(funcionService.porSala(salaId));
    }

    /**
     * Funciones de una fecha exacta.
     * Ejemplo: GET /api/funciones/fecha/2026-05-13
     */
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Funcion>> porFecha(@PathVariable String fecha) {
        return ResponseEntity.ok(funcionService.porFecha(fecha));
    }

    /**
     * Funciones dentro de un rango de fechas (cartelera por período).
     * Ejemplo: GET /api/funciones/rango?inicio=2026-05-13&fin=2026-05-20
     */
    @GetMapping("/rango")
    public ResponseEntity<?> porRango(
            @RequestParam String inicio,
            @RequestParam String fin) {
        try {
            return ResponseEntity.ok(funcionService.porRango(inicio, fin));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ── POST: función individual ──────────────────────────────────────────

    /**
     * Crea una función individual.
     *
     * Body ejemplo:
     * {
     *   "pelicula":    { "movieId": 1 },
     *   "sala":        { "salaId": 2 },
     *   "fecha":       "2026-05-13",
     *   "horaInicio":  "19:00",
     *   "fechaInicio": "2026-05-10",
     *   "fechaFin":    "2026-05-20",
     *   "precioBoleto": 18000
     * }
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Funcion funcion) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(funcionService.guardar(funcion));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ── POST: programar rango ─────────────────────────────────────────────

    /**
     * Programa automáticamente una función por cada día del rango × horario.
     * Omite silenciosamente los duplicados (misma sala + fecha + hora).
     *
     * Body ejemplo:
     * {
     *   "movieId":     1,
     *   "salaId":      2,
     *   "fechaInicio": "2026-05-13",
     *   "fechaFin":    "2026-05-20",
     *   "horarios":    ["09:00","12:00","15:00","19:00","21:00"],
     *   "precioBoleto": 18000
     * }
     *
     * Respuesta:
     * {
     *   "creadas": 40,
     *   "funciones": [ ... ]
     * }
     */
    @PostMapping("/programar")
    public ResponseEntity<?> programar(@RequestBody ProgramacionRequest req) {
        try {
            List<Funcion> creadas = funcionService.programar(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "creadas",   creadas.size(),
                    "funciones", creadas
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ── PUT ───────────────────────────────────────────────────────────────

    /**
     * Actualiza campos de una función existente.
     * Solo actualiza los campos que vengan en el body (null = sin cambio).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Funcion datos) {
        try {
            return ResponseEntity.ok(funcionService.actualizar(id, datos));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────

    /** Elimina una función y sus sillas en cascada */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        funcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}