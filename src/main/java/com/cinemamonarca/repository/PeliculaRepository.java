package com.cinemamonarca.repository;

import com.cinemamonarca.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByNombreContainingIgnoreCase(String nombre);
}
