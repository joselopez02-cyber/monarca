package com.cinemamonarca.service;

import com.cinemamonarca.dto.ReservaRequest;
import com.cinemamonarca.model.Cliente;
import com.cinemamonarca.model.Funcion;
import com.cinemamonarca.model.Reserva;
import com.cinemamonarca.repository.ClienteRepository;
import com.cinemamonarca.repository.FuncionRepository;
import com.cinemamonarca.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservaService {

    private final ReservaRepository  reservaRepository;
    private final ClienteRepository  clienteRepository;
    private final FuncionRepository  funcionRepository;
    private final SillaService       sillaService;

    public List<Reserva> obtenerTodas()           { return reservaRepository.findAllWithDetails(); }
    public List<Reserva> obtenerPorCliente(Long c){ return reservaRepository.findByCliente_CustId(c); }
    public List<Reserva> obtenerPorFuncion(Long f){ return reservaRepository.findByFuncion_FuncionId(f); }

    public Reserva obtenerPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + id));
    }

    @Transactional
    public Reserva guardar(ReservaRequest req) {
        Cliente cliente = clienteRepository.findById(req.getCustId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Funcion funcion = funcionRepository.findByIdWithDetails(req.getFuncionId())
                .orElseThrow(() -> new RuntimeException("Función no encontrada"));

        if (req.getSillas() == null || req.getSillas().isEmpty())
            throw new RuntimeException("Debes seleccionar al menos una silla.");

        Reserva reserva = Reserva.builder()
                .nombre(req.getNombre())
                .contNum(req.getContNum())
                .fecha(req.getFecha())
                .tiempo(req.getTiempo())
                .estado(Reserva.Estado.CONFIRMADA)
                .cliente(cliente)
                .funcion(funcion)
                .build();

        Reserva saved = reservaRepository.save(reserva);
        sillaService.reservarSillas(funcion.getFuncionId(), req.getSillas(), saved);
        return saved;
    }

    @Transactional
    public Reserva cancelar(Long id) {
        Reserva r = obtenerPorId(id);
        r.setEstado(Reserva.Estado.CANCELADA);
        sillaService.liberarSillas(r);
        return reservaRepository.save(r);
    }

    @Transactional
    public void eliminar(Long id) { reservaRepository.deleteById(id); }
}
