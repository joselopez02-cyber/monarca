package com.cinemamonarca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "reserva")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "res_code")
    private Long resCode;

    @Column(length = 150)
    private String nombre;

    @Column(length = 20)
    private String tiempo;

    @Column(length = 20)
    private String fecha;

    @Column(name = "cont_num", length = 20)
    private String contNum;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.CONFIRMADA;

    // funcion puede ser NULL si fue eliminada al cierre del día
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "funcion_id")
    @JsonIgnoreProperties({"sillas","hibernateLazyInitializer","handler"})
    private Funcion funcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cust_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Cliente cliente;

    @OneToMany(mappedBy = "reserva", fetch = FetchType.EAGER)
    @JsonIgnore
    @Builder.Default
    private List<Silla> sillas = new ArrayList<>();

    // ── Snapshot histórico ───────────────────────────────────────────────
    // Se llenan al crear la reserva y nunca se borran.
    // Cuando funcion == null (fue eliminada), estos campos
    // mantienen toda la info visible en ventas/historial.

    @Column(name = "snap_pelicula", length = 255)
    private String snapPelicula;

    @Column(name = "snap_sala", length = 100)
    private String snapSala;

    @Column(name = "snap_tipo_sala", length = 20)
    private String snapTipoSala;

    @Column(name = "snap_fecha", length = 20)
    private String snapFecha;

    @Column(name = "snap_hora", length = 10)
    private String snapHora;

    @Column(name = "snap_precio")
    private Double snapPrecio;

    @Column(name = "snap_sillas", length = 500)
    private String snapSillas;

    @Column(name = "snap_total")
    private Double snapTotal;

    // ── Campos resueltos para el frontend ────────────────────────────────
    // Si la función aún existe  → usa el dato en vivo
    // Si la función fue borrada → usa el snapshot
    // El frontend siempre recibe estos campos sin importar el estado

    @Transient
    public String getPeliculaNombre() {
        if (funcion != null && funcion.getPelicula() != null)
            return funcion.getPelicula().getNombre();
        return snapPelicula;
    }

    @Transient
    public String getSalaNombre() {
        if (funcion != null && funcion.getSala() != null)
            return funcion.getSala().getNombre();
        return snapSala;
    }

    @Transient
    public String getTipoSala() {
        if (funcion != null && funcion.getSala() != null && funcion.getSala().getTipo() != null)
            return funcion.getSala().getTipo().name();
        return snapTipoSala;
    }

    @Transient
    public String getFuncionFecha() {
        if (funcion != null) return funcion.getFecha();
        return snapFecha;
    }

    @Transient
    public String getFuncionHora() {
        if (funcion != null) return funcion.getHoraInicio();
        return snapHora;
    }

    @Transient
    public Double getPrecioBoleto() {
        if (funcion != null && funcion.getPrecioBoleto() != null)
            return funcion.getPrecioBoleto();
        return snapPrecio;
    }

    @Transient
    public List<String> getSillasSeleccionadas() {
        // Si hay sillas vivas en BD las usa; si no, parsea el snapshot
        if (sillas != null && !sillas.isEmpty()) {
            return sillas.stream()
                    .map(s -> s.getFila() + s.getNumero())
                    .sorted()
                    .collect(Collectors.toList());
        }
        if (snapSillas != null && !snapSillas.isBlank())
            return List.of(snapSillas.split(","));
        return List.of();
    }

    @Transient
    public double getTotal() {
        if (snapTotal != null) return snapTotal;
        if (funcion == null || sillas == null) return 0;
        return (funcion.getPrecioBoleto() != null ? funcion.getPrecioBoleto() : 0) * sillas.size();
    }

    public enum Estado { PENDIENTE, CONFIRMADA, CANCELADA }
}