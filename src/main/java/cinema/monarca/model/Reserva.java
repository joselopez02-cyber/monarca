package cinema.monarca.model;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "funcion_id")
    @JsonIgnoreProperties({"sillas","hibernateLazyInitializer","handler"})
    private Funcion funcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cust_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Cliente cliente;

    @OneToMany(mappedBy = "reserva", fetch = FetchType.EAGER)
    @JsonIgnore   // EAGER para leer en @Transient, @JsonIgnore para no serializar directamente
    @Builder.Default
    private List<Silla> sillas = new ArrayList<>();

    // ── Campos calculados expuestos al frontend ──────────────────────────
    /** Códigos de sillas reservadas: ["A1","A2",...] */
    @Transient
    public List<String> getSillasSeleccionadas() {
        if (sillas == null) return List.of();
        return sillas.stream()
                .map(s -> s.getFila() + s.getNumero())
                .sorted()
                .collect(Collectors.toList());
    }

    /** Total cobrado = precio × cantidad de sillas */
    @Transient
    public double getTotal() {
        if (funcion == null || sillas == null) return 0;
        return (funcion.getPrecioBoleto() != null ? funcion.getPrecioBoleto() : 0) * sillas.size();
    }

    public enum Estado { PENDIENTE, CONFIRMADA, CANCELADA }
}