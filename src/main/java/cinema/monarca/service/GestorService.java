package cinema.monarca.service;

import cinema.monarca.model.Gestor;
import cinema.monarca.model.Sucursal;
import cinema.monarca.repository.GestorRepository;
import cinema.monarca.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GestorService {

    private final GestorRepository gestorRepository;
    private final SucursalRepository sucursalRepository;

    public List<Gestor> obtenerTodos() { return gestorRepository.findAll(); }

    public Gestor obtenerPorId(Long id) {
        return gestorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gestor no encontrado con id: " + id));
    }

    @Transactional
    public Gestor guardar(Gestor gestor, Long sucursalId) {
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
        gestor.setSucursal(sucursal);
        return gestorRepository.save(gestor);
    }

    @Transactional
    public void eliminar(Long id) { gestorRepository.deleteById(id); }
}
