package cinema.monarca.services;

// CORRECCIÓN: Apuntar a cinema.monarca para evitar errores de compilación
import cinema.monarca.domain.Movie;
import cinema.monarca.domain.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service // Esta anotación permite que el MovieController lo reconozca
public class MovieService {
    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public List<Movie> listar() {
        log.info("USER/ADMIN: Consultando cartelera de Cinema Monarca");
        return repository.findAll();
    }

    public Movie guardar(Movie movie) {
        log.info("ADMIN: Creando película: {}", movie.getTitulo());
        return repository.save(movie);
    }

    public Movie actualizar(Long id, Movie movie) {
        log.info("ADMIN: Editando película ID: {}", id);
        // Verificamos que la película exista antes de actualizar
        return repository.findById(id).map(existingMovie -> {
            existingMovie.setTitulo(movie.getTitulo());
            existingMovie.setGenero(movie.getGenero());
            existingMovie.setDuracionMinutos(movie.getDuracionMinutos());
            existingMovie.setPrecioBoleto(movie.getPrecioBoleto());
            return repository.save(existingMovie);
        }).orElseThrow(() -> new RuntimeException("Película no encontrada con ID: " + id));
    }

    public void eliminar(Long id) {
        log.warn("ADMIN: Eliminando película ID: {}", id);
        repository.deleteById(id);
    }
}