package cinema.monarca.infrastructure;

import cinema.monarca.domain.Movie;
import cinema.monarca.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
@Slf4j
public class MovieController {
    private final MovieService service;

    public MovieController(MovieService service) { this.service = service; }

    @Operation(summary = "LISTAR - Acceso: CUALQUIERA")
    @GetMapping
    public List<Movie> listar() {
        return service.listar();
    }

    @Operation(summary = "CREAR - Acceso: SOLO ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Movie crear(@RequestBody Movie movie) {
        return service.guardar(movie);
    }

    @Operation(summary = "EDITAR - Acceso: SOLO ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Movie editar(@PathVariable Long id, @RequestBody Movie movie) {
        return service.actualizar(id, movie);
    }

    @Operation(summary = "BORRAR - Acceso: SOLO ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        service.eliminar(id);
    }
}