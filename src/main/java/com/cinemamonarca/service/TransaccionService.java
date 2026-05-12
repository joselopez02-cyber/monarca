package com.cinemamonarca.service;

import com.cinemamonarca.model.Cliente;
import com.cinemamonarca.model.Reserva;
import com.cinemamonarca.model.Transaccion;
import com.cinemamonarca.repository.ClienteRepository;
import com.cinemamonarca.repository.ReservaRepository;
import com.cinemamonarca.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final ClienteRepository     clienteRepository;
    private final ReservaRepository     reservaRepository;

    public List<Transaccion> obtenerTodas()              { return transaccionRepository.findAll(); }
    public List<Transaccion> obtenerPorCliente(Long id)  { return transaccionRepository.findByCliente_CustId(id); }

    public Transaccion obtenerPorId(Long id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada con id: " + id));
    }

    @Transactional
    public Transaccion guardar(Transaccion transaccion, Long custId, Long resCode) {
        Cliente cliente = clienteRepository.findById(custId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Reserva reserva = reservaRepository.findById(resCode)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        transaccion.setCliente(cliente);
        transaccion.setReserva(reserva);
        return transaccionRepository.save(transaccion);
    }

    @Transactional
    public void eliminar(Long id) { transaccionRepository.deleteById(id); }
}
