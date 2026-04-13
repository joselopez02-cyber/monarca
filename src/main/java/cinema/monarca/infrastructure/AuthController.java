package cinema.monarca.infrastructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "auth-controller", description = "OPERACIONES DE SESIÓN")
public class AuthController {

    @Operation(summary = "LOGOUT", description = "CIERRA LA SESIÓN ACTIVA")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("SESIÓN CERRADA");
    }
}