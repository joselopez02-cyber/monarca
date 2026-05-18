package com.cinemamonarca.service;

import com.cinemamonarca.dto.ProgramacionRequest;
import com.cinemamonarca.model.Funcion;
import com.cinemamonarca.model.Pelicula;
import com.cinemamonarca.model.Sala;
import com.cinemamonarca.repository.FuncionRepository;
import com.cinemamonarca.repository.PeliculaRepository;
import com.cinemamonarca.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuncionService {

    private final FuncionRepository   funcionRepo;
    private final PeliculaRepository  peliculaRepo;
    private final SalaRepository      salaRepo;
    private final SillaService        sillaService;

    // ── Consultas ──────────────────────────────────────────────────────────

    public List<Funcion> obtenerTodas() {
        return funcionRepo.findAllWithDetails();
    }

    /** Solo funciones de hoy en adelante — para la cartelera pública */
    public List<Funcion> obtenerDesdeHoy() {
        String hoy = LocalDate.now().toString(); // yyyy-MM-dd
        return funcionRepo.findAllDesdeHoy(hoy);
    }

    public Funcion obtenerPorId(Long id) {
        return funcionRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Función no encontrada: " + id));
    }

    public List<Funcion> porPelicula(Long movieId) {
        return funcionRepo.findByPelicula_MovieId(movieId);
    }

    public List<Funcion> porSala(Long salaId) {
        return funcionRepo.findBySala_SalaId(salaId);
    }

    public List<Funcion> porFecha(String fecha) {
        return funcionRepo.findByFecha(fecha);
    }

    /**
     * Funciones dentro de un rango de fechas — cartelera por período.
     * Ejemplo: inicio="2026-05-13", fin="2026-05-20"
     */
    public List<Funcion> porRango(String inicio, String fin) {
        if (fin.compareTo(inicio) < 0)
            throw new RuntimeException("La fecha fin no puede ser anterior a la fecha inicio.");
        return funcionRepo.findByFechaEntreFechas(inicio, fin);
    }

    // ── Crear función individual ───────────────────────────────────────────

    @Transactional
    public Funcion guardar(Funcion funcion) {
        // Resolver referencias por ID si vienen sólo con el ID
        if (funcion.getPelicula() != null && funcion.getPelicula().getMovieId() != null) {
            Pelicula p = peliculaRepo.findById(funcion.getPelicula().getMovieId())
                    .orElseThrow(() -> new RuntimeException("Película no encontrada"));
            funcion.setPelicula(p);
        }
        if (funcion.getSala() != null && funcion.getSala().getSalaId() != null) {
            Sala s = salaRepo.findById(funcion.getSala().getSalaId())
                    .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
            funcion.setSala(s);
        }
        funcion.syncCapacidad();
        Funcion saved = funcionRepo.save(funcion);
        sillaService.inicializarSillas(saved);
        return saved;
    }

    // ── Programar rango de fechas con múltiples horarios ──────────────────

    /**
     * Crea automáticamente una función por cada combinación de
     * (día del rango) × (horario). Omite silenciosamente los duplicados.
     *
     * Ejemplo: fechaInicio=2026-05-19, fechaFin=2026-05-25,
     *          horarios=["09:00","12:00","15:00","18:00","21:00"]
     *          → hasta 35 funciones creadas.
     */
    @Transactional
    public List<Funcion> programar(ProgramacionRequest req) {
        Pelicula pelicula = peliculaRepo.findById(req.getMovieId())
                .orElseThrow(() -> new RuntimeException("Película no encontrada: " + req.getMovieId()));
        Sala sala = salaRepo.findById(req.getSalaId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada: " + req.getSalaId()));

        LocalDate inicio = LocalDate.parse(req.getFechaInicio());
        LocalDate fin    = LocalDate.parse(req.getFechaFin());

        if (fin.isBefore(inicio))
            throw new RuntimeException("La fecha de fin no puede ser anterior a la de inicio.");
        if (req.getHorarios() == null || req.getHorarios().isEmpty())
            throw new RuntimeException("Debes indicar al menos un horario.");

        List<Funcion> creadas = new ArrayList<>();

        for (LocalDate dia = inicio; !dia.isAfter(fin); dia = dia.plusDays(1)) {
            String fechaStr = dia.toString(); // yyyy-MM-dd

            for (String hora : req.getHorarios()) {
                // Evitar duplicados: misma sala, misma fecha, misma hora
                if (funcionRepo.existsBySalaFechaHora(sala.getSalaId(), fechaStr, hora)) continue;

                Funcion funcion = Funcion.builder()
                        .pelicula(pelicula)
                        .sala(sala)
                        .fecha(fechaStr)
                        .horaInicio(hora)
                        .fechaInicio(req.getFechaInicio())
                        .fechaFin(req.getFechaFin())
                        .precioBoleto(req.getPrecioBoleto())
                        .build();

                funcion.syncCapacidad();
                Funcion saved = funcionRepo.save(funcion);
                sillaService.inicializarSillas(saved);
                creadas.add(saved);
            }
        }
        return creadas;
    }

    // ── Actualizar ─────────────────────────────────────────────────────────

    @Transactional
    public Funcion actualizar(Long id, Funcion datos) {
        Funcion funcion = obtenerPorId(id);
        if (datos.getFecha()       != null) funcion.setFecha(datos.getFecha());
        if (datos.getHoraInicio()  != null) funcion.setHoraInicio(datos.getHoraInicio());
        if (datos.getFechaInicio() != null) funcion.setFechaInicio(datos.getFechaInicio());
        if (datos.getFechaFin()    != null) funcion.setFechaFin(datos.getFechaFin());
        if (datos.getPrecioBoleto()!= null) funcion.setPrecioBoleto(datos.getPrecioBoleto());
        if (datos.getPelicula()    != null && datos.getPelicula().getMovieId() != null) {
            funcion.setPelicula(peliculaRepo.findById(datos.getPelicula().getMovieId())
                    .orElseThrow(() -> new RuntimeException("Película no encontrada")));
        }
        if (datos.getSala() != null && datos.getSala().getSalaId() != null) {
            funcion.setSala(salaRepo.findById(datos.getSala().getSalaId())
                    .orElseThrow(() -> new RuntimeException("Sala no encontrada")));
        }
        funcion.syncCapacidad();
        return funcionRepo.save(funcion);
    }

    // ── Eliminar ───────────────────────────────────────────────────────────

    @Transactional
    public void eliminar(Long id) {
        funcionRepo.deleteById(id);
    }
}