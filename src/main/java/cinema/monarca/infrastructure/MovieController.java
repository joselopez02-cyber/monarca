package cinema.monarca.infrastructure;

import cinema.monarca.domain.Movie;
import cinema.monarca.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
public class MovieController {
    private static final Logger log = LoggerFactory.getLogger(MovieController.class);
    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @Operation(summary = "LISTAR")
    @GetMapping
    public List<Movie> listar() {
        log.info("Cinema Monarca: Consultando cartelera pública");
        return service.listar();
    }

    @Operation(summary = "CREAR")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Movie crear(@RequestBody Movie movie) {
        log.info("Cinema Monarca: Creando película: {}", movie.getTitulo());
        return service.guardar(movie);
    }

    @Operation(summary = "EDITAR")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Movie editar(@PathVariable Long id, @RequestBody Movie movie) {
        return service.actualizar(id, movie);
    }

    @Operation(summary = "BORRAR")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        service.eliminar(id);
    }
}