package com.cinemamonarca.repository;

import com.cinemamonarca.model.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    // Carga película, sala, sucursal y cine para que el frontend pueda filtrar por sucursal
    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala s LEFT JOIN FETCH s.sucursal su LEFT JOIN FETCH su.cine")
    List<Funcion> findAllWithDetails();

    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala s LEFT JOIN FETCH s.sucursal su LEFT JOIN FETCH su.cine WHERE f.funcionId = :id")
    Optional<Funcion> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala s LEFT JOIN FETCH s.sucursal su LEFT JOIN FETCH su.cine WHERE f.pelicula.movieId = :id")
    List<Funcion> findByPelicula_MovieId(@Param("id") Long movieId);

    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala s LEFT JOIN FETCH s.sucursal su LEFT JOIN FETCH su.cine WHERE f.sala.salaId = :id")
    List<Funcion> findBySala_SalaId(@Param("id") Long salaId);

    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala s LEFT JOIN FETCH s.sucursal su LEFT JOIN FETCH su.cine WHERE f.fecha = :fecha")
    List<Funcion> findByFecha(@Param("fecha") String fecha);

    @Query("SELECT f FROM Funcion f JOIN FETCH f.pelicula JOIN FETCH f.sala s LEFT JOIN FETCH s.sucursal su LEFT JOIN FETCH su.cine WHERE f.pelicula.movieId = :movieId AND f.fecha = :fecha")
    List<Funcion> findByPelicula_MovieIdAndFecha(@Param("movieId") Long movieId, @Param("fecha") String fecha);
}
