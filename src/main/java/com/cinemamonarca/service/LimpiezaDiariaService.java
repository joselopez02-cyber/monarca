package com.cinemamonarca.service;

import com.cinemamonarca.model.Funcion;
import com.cinemamonarca.model.Silla;
import com.cinemamonarca.repository.FuncionRepository;
import com.cinemamonarca.repository.SillaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LimpiezaDiariaService {

    private static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");

    private final FuncionRepository funcionRepo;
    private final SillaRepository   sillaRepo;
    private final EntityManager     em;

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Bogota")
    @Transactional
    public void limpiezaCompleta() {

        String hoy = LocalDate.now(ZONA_COLOMBIA).toString();
        log.info("[LIMPIEZA] ══════════════════════════════════════════");
        log.info("[LIMPIEZA] Inicio limpieza diaria — fecha Colombia: {}", hoy);

        // 1. Eliminar funciones de días pasados
        // Sillas caen en cascada (ON DELETE CASCADE).
        // Reservas conservan funcion_id=NULL + snap_* con el historial.
        List<Funcion> vencidas = funcionRepo.findFuncionesAnterioresA(hoy);

        if (!vencidas.isEmpty()) {
            log.info("[LIMPIEZA] ① Eliminando {} función(es) de días anteriores...", vencidas.size());
            vencidas.forEach(f -> log.info("[LIMPIEZA]    → #{} {} {} — {}",
                    f.getFuncionId(), f.getFecha(), f.getHoraInicio(),
                    f.getPelicula() != null ? f.getPelicula().getNombre() : "?"));
            funcionRepo.deleteAll(vencidas);
            log.info("[LIMPIEZA]   ✓ Funciones + sillas eliminadas.");
        } else {
            log.info("[LIMPIEZA] ① Sin funciones vencidas hoy.");
        }

        // 2. Limpiar sillas huérfanas (bloqueadas sin reserva activa)
        List<Silla> huerfanas = sillaRepo.findSillasHuerfanas();
        if (!huerfanas.isEmpty()) {
            log.info("[LIMPIEZA] ② Reseteando {} silla(s) huérfana(s) a DISPONIBLE...", huerfanas.size());
            huerfanas.forEach(s -> s.setEstado(Silla.Estado.DISPONIBLE));
            sillaRepo.saveAll(huerfanas);
            log.info("[LIMPIEZA]   ✓ Sillas restablecidas.");
        } else {
            log.info("[LIMPIEZA] ② Sin sillas huérfanas.");
        }

        // 3. Limpiar caché L1 de Hibernate
        em.clear();
        log.info("[LIMPIEZA] ③ Caché Hibernate limpiada.");
        log.info("[LIMPIEZA] ✅ Limpieza completa finalizada.");
        log.info("[LIMPIEZA] ══════════════════════════════════════════");
    }
}