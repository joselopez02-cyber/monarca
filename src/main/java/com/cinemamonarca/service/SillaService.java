package com.cinemamonarca.service;

import com.cinemamonarca.dto.SillaDTO;
import com.cinemamonarca.model.Funcion;
import com.cinemamonarca.model.Reserva;
import com.cinemamonarca.model.Sala;
import com.cinemamonarca.model.Silla;
import com.cinemamonarca.repository.FuncionRepository;
import com.cinemamonarca.repository.SillaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SillaService {

    private final SillaRepository    sillaRepo;
    private final FuncionRepository  funcionRepo;

    /**
     * Genera los asientos de una función según el tipo de sala:
     *   PRO    → filas A-D × col 1-5  (20 sillas)
     *   TRES_D → filas A-E × col 1-6  (30 sillas)
     *   DOS_D  → filas A-E × col 1-10 (50 sillas)
     */
    @Transactional
    public void inicializarSillas(Funcion funcion) {
        Long id = funcion.getFuncionId();
        if (!sillaRepo.findByFuncion_FuncionIdOrderByFilaAscNumeroAsc(id).isEmpty()) return;

        Sala sala = funcion.getSala();
        Sala.TipoSala tipo = sala != null ? sala.getTipo() : Sala.TipoSala.DOS_D;

        // Usar filas/columnas configuradas en la sala si existen, sino defaults por tipo
        int numFilas;
        int columnas;

        if (sala != null && sala.getFilas() != null && sala.getColumnas() != null) {
            // Config real de la sala
            numFilas = sala.getFilas();
            columnas = sala.getColumnas();
        } else {
            // Fallback por tipo
            numFilas = switch (tipo) {
                case PRO    -> 4;
                case TRES_D -> 5;
                case DOS_D  -> 5;
            };
            columnas = switch (tipo) {
                case PRO    -> 5;
                case TRES_D -> 6;
                case DOS_D  -> 10;
            };
        }

        // Generar letras de fila: A, B, C...
        String[] filas = new String[Math.min(numFilas, 26)];
        for (int i = 0; i < filas.length; i++) {
            filas[i] = String.valueOf((char) ('A' + i));
        }

        List<Silla> sillas = new ArrayList<>();
        for (String fila : filas) {
            for (int n = 1; n <= columnas; n++) {
                sillas.add(Silla.builder()
                        .fila(fila).numero(n)
                        .estado(Silla.Estado.DISPONIBLE)
                        .funcion(funcion)
                        .build());
            }
        }
        sillaRepo.saveAll(sillas);
    }

    @Transactional
    public List<SillaDTO> obtenerPorFuncion(Long funcionId) {
        // Si la función no tiene sillas aún (fue creada antes del mecanismo de inicialización
        // o es una función legacy), las generamos on-demand de forma transparente.
        List<Silla> existentes = sillaRepo.findByFuncion_FuncionIdOrderByFilaAscNumeroAsc(funcionId);
        if (existentes.isEmpty()) {
            Funcion funcion = funcionRepo.findByIdWithDetails(funcionId)
                    .orElseThrow(() -> new RuntimeException("Función no encontrada: " + funcionId));
            inicializarSillas(funcion);
            existentes = sillaRepo.findByFuncion_FuncionIdOrderByFilaAscNumeroAsc(funcionId);
        }
        return existentes.stream()
                .map(s -> new SillaDTO(
                        s.getSillaId(), s.getFila(), s.getNumero(),
                        s.getFila() + s.getNumero(),
                        s.getEstado().name(),
                        s.getReserva() != null ? s.getReserva().getResCode() : null))
                .toList();
    }

    @Transactional
    public List<Silla> reservarSillas(Long funcionId, List<String> codigos, Reserva reserva) {
        List<Silla> reservadas = new ArrayList<>();
        for (String codigo : codigos) {
            String fila   = String.valueOf(codigo.charAt(0)).toUpperCase();
            int    numero = Integer.parseInt(codigo.substring(1));

            Silla silla = sillaRepo.findByFuncion_FuncionIdAndFilaAndNumero(funcionId, fila, numero)
                    .orElseThrow(() -> new RuntimeException("Silla " + codigo + " no existe en esta función."));

            if (silla.getEstado() != Silla.Estado.DISPONIBLE)
                throw new RuntimeException("La silla " + codigo + " ya está ocupada.");

            silla.setEstado(Silla.Estado.OCUPADA);
            silla.setReserva(reserva);
            reservadas.add(sillaRepo.save(silla));
        }

        // Actualizar contador de disponibles en la función
        Funcion f = funcionRepo.findById(funcionId).orElseThrow();
        long disponibles = sillaRepo.countByFuncion_FuncionIdAndEstado(funcionId, Silla.Estado.DISPONIBLE);
        f.setAsientosDisponibles((int) disponibles);
        funcionRepo.save(f);

        return reservadas;
    }

    @Transactional
    public void liberarSillas(Reserva reserva) {
        // Query específico por reserva — evita findAll()
        List<Silla> sillas = sillaRepo.findByReservaId(reserva.getResCode());
        sillas.forEach(s -> {
            s.setEstado(Silla.Estado.DISPONIBLE);
            s.setReserva(null);
        });
        sillaRepo.saveAll(sillas);

        // Actualizar contador de la función
        if (reserva.getFuncion() != null) {
            Long fid = reserva.getFuncion().getFuncionId();
            long disponibles = sillaRepo.countByFuncion_FuncionIdAndEstado(fid, Silla.Estado.DISPONIBLE);
            Funcion f = funcionRepo.findById(fid).orElseThrow();
            f.setAsientosDisponibles((int) disponibles);
            funcionRepo.save(f);
        }
    }
}