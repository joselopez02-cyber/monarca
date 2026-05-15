package com.cinemamonarca.controller;

import com.cinemamonarca.dto.ReservaRequest;
import com.cinemamonarca.model.Reserva;
import com.cinemamonarca.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservaController {

    private final ReservaService reservaService;

    /**
     * GET /api/reservas
     * ADMIN  → devuelve TODAS las reservas
     * USER   → devuelve solo las reservas cuyo cliente.nombreCliente = username del JWT
     */
    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodas(Authentication auth) {
        boolean isAdmin = auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        List<Reserva> lista = isAdmin
                ? reservaService.obtenerTodas()
                : reservaService.obtenerPorUsername(auth.getName());
        return ResponseEntity.ok(lista);
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

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReservaRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.guardar(req));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

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