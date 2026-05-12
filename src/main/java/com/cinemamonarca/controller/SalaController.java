package com.cinemamonarca.controller;

import com.cinemamonarca.model.Sala;
import com.cinemamonarca.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalaController {

    private final SalaRepository salaRepo;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Sala>> listar() {
        return ResponseEntity.ok(salaRepo.findAllWithSucursal());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Sala> obtener(@PathVariable Long id) {
        return salaRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipo}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Sala>> porTipo(@PathVariable String tipo) {
        try {
            Sala.TipoSala t = Sala.TipoSala.valueOf(tipo.toUpperCase()
                    .replace("3D", "TRES_D")
                    .replace("2D", "DOS_D"));
            return ResponseEntity.ok(salaRepo.findByTipo(t));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Sala> crear(@RequestBody Sala sala) {
        sala.syncCapacidad();
        return ResponseEntity.status(HttpStatus.CREATED).body(salaRepo.save(sala));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Sala> actualizar(@PathVariable Long id, @RequestBody Sala datos) {
        return salaRepo.findById(id).map(sala -> {
            sala.setNombre(datos.getNombre());
            sala.setTipo(datos.getTipo());
            if (datos.getFilas()    != null) sala.setFilas(datos.getFilas());
            if (datos.getColumnas() != null) sala.setColumnas(datos.getColumnas());
            sala.setSucursal(datos.getSucursal()); // FIX: persistir cambio de sucursal
            sala.syncCapacidad();
            return ResponseEntity.ok(salaRepo.save(sala));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        salaRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}