package cinema.monarca.infrastructure;

import cinema.monarca.domain.User;
import cinema.monarca.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "REGISTRO DE USUARIO - PÚBLICO")
    @PostMapping("/registro")
    public User registrar(@RequestBody User user) {
        return userService.registrar(user);
    }

    @Operation(summary = "LISTAR USUARIOS - SOLO ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> listar() {
        return userService.listar();
    }

    @Operation(summary = "ACTUALIZAR USUARIO - SOLO ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public User actualizar(@PathVariable Long id, @RequestBody User userDetalles) {
        return userService.actualizar(id, userDetalles);
    }

    @Operation(summary = "ELIMINAR USUARIO - SOLO ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        userService.borrar(id);
    }
}