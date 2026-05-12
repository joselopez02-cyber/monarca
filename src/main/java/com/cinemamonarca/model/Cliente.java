package com.cinemamonarca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cust_id")
    private Long custId;

    @NotBlank
    @Column(name = "nombre_cliente", nullable = false)
    private String nombreCliente;

    @Column(name = "cust_age")
    private Integer custAge;

    @Column(name = "direccion_cliente")
    private String direccionCliente;

    @Column(name = "numero_cliente")
    private String numeroCliente;

    /** Alias email → se almacena en direccion_cliente para compatibilidad */
    @Transient
    public String getEmail() { return direccionCliente; }
    public void setEmail(String v) { this.direccionCliente = v; }

    /** Alias telefono → se almacena en numero_cliente */
    @Transient
    public String getTelefono() { return numeroCliente; }
    public void setTelefono(String v) { this.numeroCliente = v; }
}
