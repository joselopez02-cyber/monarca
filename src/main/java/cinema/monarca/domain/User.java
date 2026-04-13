package cinema.monarca.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "cine_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String dni;

    private String telefono;

    @Column(unique = true, nullable = false)
    private String email;

    private String role = "USER";

    public User() {}

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; } // <-- USAREMOS ESTE
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Agrega los demás (dni, apellido, telefono) igual que estos...
}