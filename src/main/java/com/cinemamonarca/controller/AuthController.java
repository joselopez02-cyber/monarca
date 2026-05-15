package com.cinemamonarca.controller;

import com.cinemamonarca.dto.LoginRequest;
import com.cinemamonarca.dto.LoginResponse;
import com.cinemamonarca.dto.RegisterRequest;
import com.cinemamonarca.model.Usuario;
import com.cinemamonarca.repository.UsuarioRepository;
import com.cinemamonarca.security.JwtUtil;
import com.cinemamonarca.security.LoginRateLimiter;
import com.cinemamonarca.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final LoginRateLimiter rateLimiter;  // ← NUEVO

    private String getClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req,
                                   HttpServletRequest httpReq) {
        String ip = getClientIp(httpReq);

        // ── Bloqueo por exceso de intentos ─────────────────────────────────
        if (rateLimiter.isBlocked(ip)) {
            long wait = rateLimiter.secondsUntilUnblocked(ip);
            return ResponseEntity.status(429)
                    .header("Retry-After", String.valueOf(wait))
                    .body(java.util.Map.of(
                            "error", "Demasiados intentos fallidos. Intenta en " + wait + " segundos."));
        }

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (AuthenticationException ex) {
            rateLimiter.registerFailure(ip);  // ← cuenta el fallo
            return ResponseEntity.status(401).body(
                    java.util.Map.of("error", "Credenciales inválidas"));
        }

        rateLimiter.registerSuccess(ip);  // ← resetea el contador
        Usuario u = usuarioRepo.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtUtil.generate(u.getUsername(), u.getRol().name());
        return ResponseEntity.ok(new LoginResponse(token, u.getUsername(), u.getEmail(),
                u.getRol().name(), u.getUsuarioId()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            Usuario u = usuarioService.registrar(
                    req.getUsername(), req.getEmail(), req.getPassword(),
                    "USER",
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