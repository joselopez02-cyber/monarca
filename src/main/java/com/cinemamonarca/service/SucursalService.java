package com.cinemamonarca.service;

import com.cinemamonarca.model.Cine;
import com.cinemamonarca.model.Gestor;
import com.cinemamonarca.model.Sucursal;
import com.cinemamonarca.repository.CineRepository;
import com.cinemamonarca.repository.GestorRepository;
import com.cinemamonarca.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final CineRepository     cineRepository;
    private final GestorRepository   gestorRepository;

    public List<Sucursal> obtenerTodas() {
        return sucursalRepository.findAllWithCine();
    }

    public Sucursal obtenerPorId(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con id: " + id));
    }

    public List<Sucursal> obtenerPorCine(Long cineId) {
        return sucursalRepository.findByCine_CineId(cineId);
    }

    public List<Gestor> obtenerGestores(Long sucursalId) {
        return gestorRepository.findBySucursal_BranId(sucursalId);
    }

    @Transactional
    public Sucursal guardar(Sucursal sucursal, Long cineId) {
        Cine cine = cineRepository.findById(cineId)
                .orElseThrow(() -> new RuntimeException("Cine no encontrado"));
        sucursal.setCine(cine);
        return sucursalRepository.save(sucursal);
    }

    @Transactional
    public void eliminar(Long id) {
        sucursalRepository.deleteById(id);
    }
}