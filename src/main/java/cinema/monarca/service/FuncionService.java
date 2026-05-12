package cinema.monarca.service;

import cinema.monarca.model.Funcion;
import cinema.monarca.model.Pelicula;
import cinema.monarca.model.Sala;
import cinema.monarca.repository.FuncionRepository;
import cinema.monarca.repository.PeliculaRepository;
import cinema.monarca.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuncionService {

    private final FuncionRepository   funcionRepo;
    private final PeliculaRepository  peliculaRepo;
    private final SalaRepository      salaRepo;
    private final SillaService        sillaService;

    public List<Funcion> obtenerTodas()          { return funcionRepo.findAllWithDetails(); }
    public List<Funcion> porPelicula(Long id)    { return funcionRepo.findByPelicula_MovieId(id); }
    public List<Funcion> porSala(Long id)        { return funcionRepo.findBySala_SalaId(id); }
    public List<Funcion> porFecha(String fecha)  { return funcionRepo.findByFecha(fecha); }

    public Funcion obtenerPorId(Long id) {
        return funcionRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Función no encontrada: " + id));
    }

    @Transactional
    public Funcion guardar(Funcion funcion) {
        if (funcion.getPelicula() != null && funcion.getPelicula().getMovieId() != null) {
            Pelicula p = peliculaRepo.findById(funcion.getPelicula().getMovieId())
                    .orElseThrow(() -> new RuntimeException("Película no encontrada"));
            funcion.setPelicula(p);
        }
        if (funcion.getSala() != null && funcion.getSala().getSalaId() != null) {
            Sala s = salaRepo.findById(funcion.getSala().getSalaId())
                    .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
            funcion.setSala(s);
            funcion.setCapacidadTotal(s.getCapacidad());
            funcion.setAsientosDisponibles(s.getCapacidad());
        }
        if (funcion.getPrecioBoleto() == null) funcion.setPrecioBoleto(16000.0);

        Funcion saved = funcionRepo.save(funcion);
        sillaService.inicializarSillas(saved);
        return saved;
    }

    @Transactional
    public Funcion actualizar(Long id, Funcion datos) {
        Funcion f = obtenerPorId(id);
        f.setFecha(datos.getFecha());
        f.setHoraInicio(datos.getHoraInicio());
        f.setPrecioBoleto(datos.getPrecioBoleto());
        return funcionRepo.save(f);
    }

    @Transactional
    public void eliminar(Long id) { funcionRepo.deleteById(id); }
}