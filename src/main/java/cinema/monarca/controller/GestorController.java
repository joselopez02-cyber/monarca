package cinema.monarca.controller;

import cinema.monarca.model.Gestor;
import cinema.monarca.service.GestorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gestores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GestorController {

    private final GestorService gestorService;

    @GetMapping
    public ResponseEntity<List<Gestor>> obtenerTodos() {
        return ResponseEntity.ok(gestorService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gestor> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(gestorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Gestor> crear(
            @RequestBody Gestor gestor,
            @RequestParam Long sucursalId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gestorService.guardar(gestor, sucursalId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gestorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
