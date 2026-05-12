package cinema.monarca.controller;

import cinema.monarca.dto.ReservaRequest;
import cinema.monarca.model.Reserva;
import cinema.monarca.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodas() {
        return ResponseEntity.ok(reservaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{custId}")
    public ResponseEntity<List<Reserva>> porCliente(@PathVariable Long custId) {
        return ResponseEntity.ok(reservaService.obtenerPorCliente(custId));
    }

    @GetMapping("/funcion/{funcionId}")
    public ResponseEntity<List<Reserva>> porFuncion(@PathVariable Long funcionId) {
        return ResponseEntity.ok(reservaService.obtenerPorFuncion(funcionId));
    }

    /**
     * POST /api/reservas
     * Body: { custId, funcionId, nombre, contNum, fecha, tiempo, sillas:["A1","B3"] }
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReservaRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.guardar(req));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /** PATCH /api/reservas/{id}/cancelar */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
