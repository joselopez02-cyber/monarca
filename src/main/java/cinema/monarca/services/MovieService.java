package cinema.monarca.services;

import cinema.monarca.domain.Movie;
import cinema.monarca.domain.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MovieService {
    private static final Logger log = LoggerFactory.getLogger(MovieService.class);
    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public List<Movie> listar() {
        log.info("Cinema Monarca: Listando cartelera");
        return repository.findAll();
    }

    public Movie guardar(Movie movie) {
        return repository.save(movie);
    }

    public Movie actualizar(Long id, Movie movie) {
        return repository.findById(id).map(existing -> {
            existing.setTitulo(movie.getTitulo());
            existing.setGenero(movie.getGenero());
            existing.setDuracionMinutos(movie.getDuracionMinutos());
            existing.setPrecioBoleto(movie.getPrecioBoleto());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("No encontrado"));
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}