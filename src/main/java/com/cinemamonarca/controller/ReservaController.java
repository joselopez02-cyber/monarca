package com.cinemamonarca.controller;

import com.cinemamonarca.dto.ReservaRequest;
import com.cinemamonarca.model.Reserva;
import com.cinemamonarca.model.Usuario;
import com.cinemamonarca.repository.ClienteRepository;
import com.cinemamonarca.repository.UsuarioRepository;
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

    private final ReservaService      reservaService;
    private final UsuarioRepository   usuarioRepository;
    private final ClienteRepository   clienteRepository;

    /**
     * Helper privado — reemplaza la expresion repetida
     *   auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
     * No cambia ninguna logica, solo evita repeticion.
     */
    private boolean esAdmin(Authentication auth) {
        return auth != null &&
               auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodas(Authentication auth) {
        if (esAdmin(auth)) {
            return ResponseEntity.ok(reservaService.obtenerTodas());
        }

        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        List<Reserva> lista;
        if (usuario != null && usuario.getEmail() != null) {
            lista = reservaService.obtenerPorEmail(usuario.getEmail());
            if (lista.isEmpty()) {
                lista = reservaService.obtenerPorUsername(username);
            }
        } else {
            lista = reservaService.obtenerPorUsername(username);
        }

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
    public ResponseEntity<?> crear(@RequestBody ReservaRequest req, Authentication auth) {

        // Los admins pueden crear reservas para cualquier cliente sin restriccion.
        // esAdmin() garantiza que el bloque de ownership nunca se ejecute para ROLE_ADMIN.
        if (!esAdmin(auth) && req.getCustId() != null) {
            String username = auth.getName();
            Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

            boolean custIdPertenece = clienteRepository.findById(req.getCustId())
                    .map(c -> {
                        if (c.getNombreCliente() == null) return false;

                        boolean matchUsername = c.getNombreCliente().equalsIgnoreCase(username);

                        boolean matchEmail = usuario != null
                                && usuario.getEmail() != null
                                && c.getNombreCliente().equalsIgnoreCase(usuario.getEmail());

                        // Fallback robusto: funciona aunque el cliente fue creado
                        // manualmente con nombre completo en lugar de username
                        boolean matchCedula = usuario != null
                                && usuario.getCedula() != null
                                && usuario.getCedula().equals(c.getNumeroCliente());

                        return matchUsername || matchEmail || matchCedula;
                    })
                    .orElse(false);

            if (!custIdPertenece) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error",
                                "No puedes crear reservas a nombre de otro cliente."));
            }
        }

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