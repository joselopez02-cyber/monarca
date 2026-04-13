package cinema.monarca.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "cine_users") // Usamos un nombre específico para evitar errores en SQL 🗄️
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

    // El rol por defecto será USER. Solo el ADMIN podrá cambiarlo después. 👤
    private String role = "USER";

    // Constructor vacío (obligatorio para Spring Boot)
    public User() {
    }

    // --- Getters y Setters ---
    // Permiten que el sistema lea y escriba los datos correctamente

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}