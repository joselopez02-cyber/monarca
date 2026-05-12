package cinema.monarca.controller;

import cinema.monarca.dto.LoginRequest;
import cinema.monarca.dto.LoginResponse;
import cinema.monarca.dto.RegisterRequest;
import cinema.monarca.model.Usuario;
import cinema.monarca.repository.UsuarioRepository;
import cinema.monarca.security.JwtUtil;
import cinema.monarca.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepo;

    /** POST /api/auth/login  — { username, password } */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(
                    java.util.Map.of("error", "Credenciales inválidas"));
        }

        Usuario u = usuarioRepo.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtUtil.generate(u.getUsername(), u.getRol().name());
        return ResponseEntity.ok(new LoginResponse(token, u.getUsername(), u.getEmail(),
                u.getRol().name(), u.getUsuarioId()));
    }

    /** POST /api/auth/register  — autoregistro público, rol siempre USER */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            Usuario u = usuarioService.registrar(
                    req.getUsername(), req.getEmail(), req.getPassword(),
                    "USER",                  // rol forzado; admin lo cambia luego
                    req.getNombreCompleto(),
                    req.getCedula(),
                    req.getTelefono(),
                    req.getDireccion(),
                    req.getFechaNacimiento());
            String token = jwtUtil.generate(u.getUsername(), u.getRol().name());
            return ResponseEntity.status(201).body(new LoginResponse(
                    token, u.getUsername(), u.getEmail(), u.getRol().name(), u.getUsuarioId()));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    /** GET /api/auth/me  — Devuelve perfil del usuario autenticado */
    @GetMapping("/me")
    public ResponseEntity<?> me(org.springframework.security.core.Authentication auth) {
        return usuarioRepo.findByUsername(auth.getName())
                .map(u -> ResponseEntity.ok(java.util.Map.of(
                        "usuarioId", u.getUsuarioId(),
                        "username",  u.getUsername(),
                        "email",     u.getEmail(),
                        "rol",       u.getRol().name())))
                .orElse(ResponseEntity.notFound().build());
    }
}
