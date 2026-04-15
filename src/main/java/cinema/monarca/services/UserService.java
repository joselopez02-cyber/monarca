package cinema.monarca.services;

import cinema.monarca.domain.User;
import cinema.monarca.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> listar() {
        log.info("Cinema Monarca (Service): Consultando lista de usuarios");
        return userRepository.findAll();
    }

    public User registrar(User user) {
        log.info("Cinema Monarca (Service): Registrando nuevo usuario con email: {}", user.getEmail());
        return userRepository.save(user);
    }

    public User actualizar(Long id, User userDetalles) {
        log.info("Cinema Monarca (Service): Actualizando usuario ID: {}", id);
        return userRepository.findById(id).map(user -> {
            user.setNombre(userDetalles.getNombre());
            user.setEmail(userDetalles.getEmail());
            // Si tienes más campos en tu entidad User (ej. password), agrégalos aquí
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public void borrar(Long id) {
        log.warn("Cinema Monarca (Service): Eliminando usuario ID: {}", id);
        userRepository.deleteById(id);
    }
}