package com.cinemamonarca.service;

import com.cinemamonarca.dto.ReservaRequest;
import com.cinemamonarca.model.Cliente;
import com.cinemamonarca.model.Funcion;
import com.cinemamonarca.model.Reserva;
import com.cinemamonarca.model.Sala;
import com.cinemamonarca.repository.ClienteRepository;
import com.cinemamonarca.repository.FuncionRepository;
import com.cinemamonarca.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservaService {

    private final ReservaRepository  reservaRepository;
    private final ClienteRepository  clienteRepository;
    private final FuncionRepository  funcionRepository;
    private final SillaService       sillaService;

    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAllWithDetails();
    }

    public List<Reserva> obtenerPorCliente(Long custId) {
        return reservaRepository.findByCliente_CustId(custId);
    }

    public List<Reserva> obtenerPorFuncion(Long funcionId) {
        return reservaRepository.findByFuncion_FuncionId(funcionId);
    }

    public List<Reserva> obtenerPorUsername(String username) {
        return reservaRepository.findByUsername(username);
    }

    public List<Reserva> obtenerPorEmail(String email) {
        return reservaRepository.findByClienteEmail(email);
    }

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

        String fechaReserva = req.getFecha();
        if (fechaReserva == null) {
            fechaReserva = LocalDate.now().toString();
        }
        if (!funcion.estaEnRango(fechaReserva)) {
            throw new RuntimeException(
                    "No se puede reservar: la fecha " + fechaReserva +
                            " está fuera del rango de esta función (" +
                            funcion.getFechaInicio() + " → " + funcion.getFechaFin() + ")."
            );
        }

        if (req.getSillas() == null || req.getSillas().isEmpty())
            throw new RuntimeException("Debes seleccionar al menos una silla.");

        // Snapshot histórico
        Sala sala = funcion.getSala();
        double precioBoleto = funcion.getPrecioBoleto() != null ? funcion.getPrecioBoleto() : 0;
        double total = precioBoleto * req.getSillas().size();
        String snapSillas = String.join(",", req.getSillas());

        Reserva reserva = Reserva.builder()
                .nombre(req.getNombre())
                .contNum(req.getContNum())
                .fecha(fechaReserva)
                .tiempo(req.getTiempo())
                .estado(Reserva.Estado.CONFIRMADA)
                .cliente(cliente)
                .funcion(funcion)
                .snapPelicula(funcion.getPelicula() != null ? funcion.getPelicula().getNombre() : null)
                .snapSala(sala != null ? sala.getNombre() : null)
                .snapTipoSala(sala != null && sala.getTipo() != null ? sala.getTipo().name() : null)
                .snapFecha(funcion.getFecha())
                .snapHora(funcion.getHoraInicio())
                .snapPrecio(precioBoleto)
                .snapSillas(snapSillas)
                .snapTotal(total)
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
    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }
}