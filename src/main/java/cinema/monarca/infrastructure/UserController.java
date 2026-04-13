package cinema.monarca.infrastructure;

import cinema.monarca.domain.User;
import cinema.monarca.domain.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(summary = "ACTUALIZAR USUARIO - SOLO ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public User actualizar(@PathVariable Long id, @RequestBody User userDetalles) {
        log.info("Cinema Monarca: Actualizando usuario ID: {}", id);

        return userRepository.findById(id).map(user -> {
            // Actualiza los campos necesarios
            user.setNombre(userDetalles.getNombre());
            user.setEmail(userDetalles.getEmail());
            // No olvides actualizar otros campos que tengas en tu entidad User
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Operation(summary = "LISTAR USUARIOS - SOLO ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> listar() {
        log.info("Cinema Monarca: Consultando lista de usuarios");
        return userRepository.findAll();
    }

    @Operation(summary = "REGISTRO DE USUARIO - PÚBLICO")
    @PostMapping("/registro")
    public User registrar(@RequestBody User user) {
        // CORRECCIÓN: Se usa getEmail() porque en tu User.java
        // definiste el campo como 'email', no 'username'.
        log.info("Cinema Monarca: Registrando nuevo usuario con email: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Operation(summary = "ELIMINAR USUARIO - SOLO ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        log.warn("Cinema Monarca: Eliminando usuario ID: {}", id);
        userRepository.deleteById(id);
    }
}