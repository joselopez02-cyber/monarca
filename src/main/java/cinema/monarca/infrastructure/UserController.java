package cinema.monarca.infrastructure;

// CORRECCIÓN DE PAQUETES:
import cinema.monarca.domain.User;
import cinema.monarca.domain.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j; // Uso de Slf4j de Lombok para simplificar el log
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j // Esto reemplaza la línea del LoggerFactory
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "REGISTRO - Libre para todos 🆕")
    @PostMapping
    public User register(@RequestBody User newUser) {
        log.info("Cinema Monarca: Intentando registrar usuario DNI: {}", newUser.getDni());
        newUser.setRole("USER");
        return userRepository.save(newUser);
    }

    @Operation(summary = "LISTAR - Solo ADMIN 📋")
    @GetMapping
    public List<User> getAll() {
        log.info("Cinema Monarca: ADMIN consultando lista de usuarios.");
        return userRepository.findAll();
    }

    @Operation(summary = "ACTUALIZAR - Solo ADMIN 🔄")
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User userDetails) {
        log.info("Cinema Monarca: ADMIN actualizando usuario ID: {}", id);

        return userRepository.findById(id).map(user -> {
            user.setNombre(userDetails.getNombre());
            user.setApellido(userDetails.getApellido());
            user.setDni(userDetails.getDni());
            user.setTelefono(userDetails.getTelefono());
            user.setEmail(userDetails.getEmail());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado en Monarca"));
    }

    @Operation(summary = "ELIMINAR - Solo ADMIN 🗑️")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.warn("Cinema Monarca: ADMIN eliminando usuario ID: {}", id);
        userRepository.deleteById(id);
    }

    @Operation(summary = "Cerrar sesión 🚪")
    @PostMapping("/logout-manual")
    public void logoutManual() {
        log.info("Cinema Monarca: Cierre de sesión solicitado.");
    }
}