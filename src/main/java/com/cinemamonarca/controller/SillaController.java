package com.cinemamonarca.controller;

import com.cinemamonarca.dto.SillaDTO;
import com.cinemamonarca.service.SillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sillas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SillaController {

    private final SillaService sillaService;

    /**
     * GET /api/sillas/funcion/{funcionId}
     * Devuelveel mapa completo de asientos de una función.
     * El frontend lo usa para mostrar el selector de butacas.
     */
    @GetMapping("/funcion/{funcionId}")
    public ResponseEntity<List<SillaDTO>> porFuncion(@PathVariable Long funcionId) {
        return ResponseEntity.ok(sillaService.obtenerPorFuncion(funcionId));
    }
}
