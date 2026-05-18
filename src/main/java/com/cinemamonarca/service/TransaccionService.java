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

    private final TransaccionRepository transaccionRepo;
    private final ClienteRepository     clienteRepo;
    private final ReservaRepository     reservaRepo;

    public List<Transaccion> obtenerTodas() {
        return transaccionRepo.findAllWithDetails();
    }

    public Transaccion obtenerPorId(Long id) {
        return transaccionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada: " + id));
    }

    public List<Transaccion> obtenerPorCliente(Long custId) {
        return transaccionRepo.findByCliente_CustId(custId);
    }

    public List<Transaccion> obtenerPorReserva(Long resCode) {
        return transaccionRepo.findByReserva_ResCode(resCode);
    }

    @Transactional
    public Transaccion guardar(Transaccion transaccion, Long custId, Long resCode) {
        Cliente cliente = clienteRepo.findById(custId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + custId));
        Reserva reserva = reservaRepo.findById(resCode)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + resCode));
        transaccion.setCliente(cliente);
        transaccion.setReserva(reserva);
        return transaccionRepo.save(transaccion);
    }

    @Transactional
    public void eliminar(Long id) {
        transaccionRepo.deleteById(id);
    }
}