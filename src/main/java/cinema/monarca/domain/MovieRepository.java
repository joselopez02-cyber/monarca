package cinema.monarca.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de películas en Cinema Monarca.
 * Permite realizar operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * automáticamente sobre la entidad Movie.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Hereda todos los métodos necesarios como findAll(), save(), deleteById(), etc.
}