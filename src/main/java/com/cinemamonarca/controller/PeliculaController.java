package com.cinemamonarca.controller;

import com.cinemamonarca.model.Pelicula;
import com.cinemamonarca.service.PeliculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/peliculas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PeliculaController {

    private final PeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<List<Pelicula>> obtenerTodas(
            @RequestParam(required = false) String nombre) {
        if (nombre != null && !nombre.isEmpty()) {
            return ResponseEntity.ok(peliculaService.buscarPorNombre(nombre));
        }
        return ResponseEntity.ok(peliculaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Pelicula> crear(@Valid @RequestBody Pelicula pelicula) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peliculaService.guardar(pelicula));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Pelicula pelicula) {
        return ResponseEntity.ok(peliculaService.actualizar(id, pelicula));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        peliculaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/peliculas/{id}/poster
     * Solo ADMIN (ver SecurityConfig).
     * Body: { "posterUrl": "data:image/jpeg;base64,..." }
     */
    @PostMapping("/{id}/poster")
    public ResponseEntity<Pelicula> subirPoster(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String dataUrl = body.get("posterUrl");
        if (dataUrl == null || dataUrl.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(peliculaService.actualizarPoster(id, dataUrl));
    }

    /**
     * DELETE /api/peliculas/{id}/poster
     * Solo ADMIN — elimina el poster de la película.
     */
    @DeleteMapping("/{id}/poster")
    public ResponseEntity<Pelicula> eliminarPoster(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.actualizarPoster(id, null));
    }
}