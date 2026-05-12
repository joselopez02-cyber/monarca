package com.cinemamonarca.service;

import com.cinemamonarca.model.Cine;
import com.cinemamonarca.repository.CineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CineService {

    private final CineRepository cineRepository;

    public List<Cine> obtenerTodos()  { return cineRepository.findAll(); }

    public Cine obtenerPorId(Long id) {
        return cineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cine no encontrado con id: " + id));
    }

    @Transactional
    public Cine guardar(Cine cine) { return cineRepository.save(cine); }

    @Transactional
    public Cine actualizar(Long id, Cine datos) {
        Cine cine = obtenerPorId(id);
        cine.setNombreDelCine(datos.getNombreDelCine());
        cine.setCineCont(datos.getCineCont());
        return cineRepository.save(cine);
    }

    @Transactional
    public void eliminar(Long id) { cineRepository.deleteById(id); }
}
