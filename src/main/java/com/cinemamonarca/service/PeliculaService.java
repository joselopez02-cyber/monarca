package com.cinemamonarca.service;

import com.cinemamonarca.model.Pelicula;
import com.cinemamonarca.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;

    public List<Pelicula> obtenerTodas()           { return peliculaRepository.findAll(); }
    public List<Pelicula> buscarPorNombre(String n) { return peliculaRepository.findByNombreContainingIgnoreCase(n); }

    public Pelicula obtenerPorId(Long id) {
        return peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada con id: " + id));
    }

    @Transactional
    public Pelicula guardar(Pelicula pelicula) { return peliculaRepository.save(pelicula); }

    @Transactional
    public Pelicula actualizar(Long id, Pelicula datos) {
        Pelicula p = obtenerPorId(id);
        p.setNombre(datos.getNombre());
        p.setDescripcion(datos.getDescripcion());
        p.setDuracionMin(datos.getDuracionMin());
        p.setGenero(datos.getGenero());
        p.setClasificacion(datos.getClasificacion());
        return peliculaRepository.save(p);
    }

    @Transactional
    public void eliminar(Long id) { peliculaRepository.deleteById(id); }
}
