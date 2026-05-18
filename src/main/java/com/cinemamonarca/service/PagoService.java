package com.cinemamonarca.service;

import com.cinemamonarca.dto.PagoRequest;
import com.cinemamonarca.dto.PagoResponse;
import com.cinemamonarca.model.Cliente;
import com.cinemamonarca.model.Reserva;
import com.cinemamonarca.model.Transaccion;
import com.cinemamonarca.repository.ClienteRepository;
import com.cinemamonarca.repository.ReservaRepository;
import com.cinemamonarca.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Sistema de pago SIMULADO — solo para fines académicos / demo.
 * No se procesa ninguna transacción financiera real.
 */
@Service
@RequiredArgsConstructor
public class PagoService {

    private final TransaccionRepository transaccionRepo;
    private final ClienteRepository     clienteRepo;
    private final ReservaRepository     reservaRepo;

    private static final DateTimeFormatter FMT    = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final SecureRandom      RANDOM = new SecureRandom();

    @Transactional
    public PagoResponse procesarPago(PagoRequest req) {

        // ── 1. Validaciones ───────────────────────────────────────────────
        validarCampos(req);

        Cliente cliente = clienteRepo.findById(req.getCustId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + req.getCustId()));

        Reserva reserva = reservaRepo.findById(req.getResCode())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada: " + req.getResCode()));

        // ── 2. Simular aprobación / rechazo ───────────────────────────────
        boolean aprobado     = true;
        String  motivoRechazo = null;

        Transaccion.TipoPago tipoPago = parseTipoPago(req.getTipoPago());

        if (tipoPago == Transaccion.TipoPago.TARJETA_CREDITO ||
            tipoPago == Transaccion.TipoPago.TARJETA_DEBITO) {

            String numero = req.getNumeroTarjeta() != null
                    ? req.getNumeroTarjeta().replaceAll("\\s", "") : "";

            if (numero.startsWith("0000")) {
                aprobado      = false;
                motivoRechazo = "Tarjeta rechazada por el banco emisor (simulado).";
            } else if (numero.length() < 16) {
                aprobado      = false;
                motivoRechazo = "Número de tarjeta inválido.";
            }
        }

        // ── 3. Generar referencia y metadatos ─────────────────────────────
        String referencia = generarReferencia();
        String ahora      = LocalDateTime.now().format(FMT);
        String ultimos4   = extraerUltimos4(req);

        // ── 4. Persistir transacción ──────────────────────────────────────
        Transaccion trans = Transaccion.builder()
                .tipoPago(tipoPago)
                .pagoTotal(req.getMontoTotal() != null
                        ? BigDecimal.valueOf(req.getMontoTotal())
                        : BigDecimal.ZERO)
                .estadoPago(aprobado ? Transaccion.EstadoPago.SIMULADO : Transaccion.EstadoPago.RECHAZADO)
                .referencia(referencia)
                .ultimos4(ultimos4)
                .fechaInicio(ahora)
                .fechaFinal(ahora)
                .fechaTrans(ahora)
                .cliente(cliente)
                .reserva(reserva)
                .build();

        Transaccion saved = transaccionRepo.save(trans);

        // ── 5. Respuesta completa con todos los datos de la venta ─────────
        return PagoResponse.builder()
                .aprobado(aprobado)
                .referencia(referencia)
                .mensaje(aprobado
                        ? "✅ Pago simulado aprobado. Referencia: " + referencia
                        : "❌ " + motivoRechazo)
                .tipoPago(req.getTipoPago())
                .montoTotal(req.getMontoTotal())
                .transNo(saved.getTransNo())
                .ultimos4(ultimos4)
                .estadoPago(saved.getEstadoPago().name())
                // ── Datos de la reserva ──
                .resCode(reserva.getResCode())
                .nombreCliente(cliente.getNombreCliente())
                .fechaReserva(reserva.getFecha())
                // ── Datos de la función (live o snapshot) ──
                .peliculaNombre(reserva.getPeliculaNombre())
                .salaNombre(reserva.getSalaNombre())
                .tipoSala(reserva.getTipoSala())
                .funcionFecha(reserva.getFuncionFecha())
                .funcionHora(reserva.getFuncionHora())
                .sillasReservadas(reserva.getSillasSeleccionadas())
                .precioBoleto(reserva.getPrecioBoleto())
                .build();
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void validarCampos(PagoRequest req) {
        if (req.getCustId()     == null) throw new RuntimeException("custId es requerido.");
        if (req.getResCode()    == null) throw new RuntimeException("resCode es requerido.");
        if (req.getTipoPago()   == null) throw new RuntimeException("tipoPago es requerido.");
        if (req.getMontoTotal() == null || req.getMontoTotal() <= 0)
            throw new RuntimeException("montoTotal debe ser mayor a 0.");
    }

    private Transaccion.TipoPago parseTipoPago(String s) {
        try { return Transaccion.TipoPago.valueOf(s.toUpperCase()); }
        catch (Exception e) { throw new RuntimeException("tipoPago inválido: " + s); }
    }

    private String generarReferencia() {
        int rand = RANDOM.nextInt(900000) + 100000;
        return "MON-" + LocalDateTime.now().getYear() + "-" + rand;
    }

    private String extraerUltimos4(PagoRequest req) {
        if (req.getNumeroTarjeta() != null) {
            String n = req.getNumeroTarjeta().replaceAll("\\s", "");
            return n.length() >= 4 ? n.substring(n.length() - 4) : "****";
        }
        if (req.getCelular() != null && req.getCelular().length() >= 4)
            return req.getCelular().substring(req.getCelular().length() - 4);
        return "****";
    }
}