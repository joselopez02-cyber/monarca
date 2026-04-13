package cinema.monarca.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de usuarios en Cinema Monarca.
 * Hereda de JpaRepository para proporcionar CRUD automático en la base de datos H2.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Aquí podrías añadir búsquedas personalizadas en el futuro, por ejemplo:
    // Optional<User> findByEmail(String email);
}