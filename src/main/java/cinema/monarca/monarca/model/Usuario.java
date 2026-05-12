package cinema.monarca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;

    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Email @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.USER;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    /* ── Campos de perfil ── */
    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Column(name = "cedula", unique = true)
    private String cedula;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "direccion")
    private String direccion;

    /** ISO date string yyyy-MM-dd */
    @Column(name = "fecha_nacimiento")
    private String fechaNacimiento;

    public enum Rol { USER, ADMIN }
}
