package com.cinemamonarca.controller;

import com.cinemamonarca.model.Usuario;
import com.cinemamonarca.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/perfil")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id,
                                               @RequestBody Map<String, String> body) {
        usuarioService.actualizarPerfil(id, body);
        return ResponseEntity.ok(Map.of("mensaje", "Perfil actualizado"));
    }

    @PatchMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id,
                                         @RequestBody Map<String, String> body) {
        usuarioService.cambiarRol(id, body.get("rol"));
        return ResponseEntity.ok(Map.of("mensaje", "Rol actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario desactivado"));
    }
}
