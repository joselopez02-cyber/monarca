package cinema.monarca.controller;

import cinema.monarca.model.Cine;
import cinema.monarca.service.CineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CineController {

    private final CineService cineService;

    @GetMapping
    public ResponseEntity<List<Cine>> obtenerTodos() {
        return ResponseEntity.ok(cineService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cine> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cineService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Cine> crear(@Valid @RequestBody Cine cine) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cineService.guardar(cine));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cine> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Cine cine) {
        return ResponseEntity.ok(cineService.actualizar(id, cine));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cineService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
