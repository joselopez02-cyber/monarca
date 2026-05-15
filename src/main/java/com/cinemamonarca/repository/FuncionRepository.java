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

    // ── Carga pelicula + sala en una sola query ────────────────────────────

    @Query("SELECT DISTINCT f FROM Funcion f " +
            "JOIN FETCH f.pelicula " +
            "JOIN FETCH f.sala s " +
            "LEFT JOIN FETCH s.sucursal")
    List<Funcion> findAllWithDetails();

    @Query("SELECT f FROM Funcion f " +
            "JOIN FETCH f.pelicula " +
            "JOIN FETCH f.sala s " +
            "LEFT JOIN FETCH s.sucursal " +
            "WHERE f.funcionId = :id")
    Optional<Funcion> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT f FROM Funcion f " +
            "JOIN FETCH f.pelicula " +
            "JOIN FETCH f.sala " +
            "WHERE f.pelicula.movieId = :id")
    List<Funcion> findByPelicula_MovieId(@Param("id") Long movieId);

    @Query("SELECT DISTINCT f FROM Funcion f " +
            "JOIN FETCH f.pelicula " +
            "JOIN FETCH f.sala " +
            "WHERE f.sala.salaId = :id")
    List<Funcion> findBySala_SalaId(@Param("id") Long salaId);

    @Query("SELECT DISTINCT f FROM Funcion f " +
            "JOIN FETCH f.pelicula " +
            "JOIN FETCH f.sala " +
            "WHERE f.fecha = :fecha")
    List<Funcion> findByFecha(@Param("fecha") String fecha);

    @Query("SELECT DISTINCT f FROM Funcion f " +
            "JOIN FETCH f.pelicula " +
            "JOIN FETCH f.sala " +
            "WHERE f.pelicula.movieId = :movieId AND f.fecha = :fecha")
    List<Funcion> findByPelicula_MovieIdAndFecha(@Param("movieId") Long movieId,
                                                 @Param("fecha") String fecha);

    /**
     * Usado en FuncionService.programar() para detectar duplicados:
     * misma sala, misma fecha, misma hora.
     */
    @Query("SELECT COUNT(f) > 0 FROM Funcion f " +
            "WHERE f.sala.salaId = :salaId " +
            "AND f.fecha = :fecha " +
            "AND f.horaInicio = :hora")
    boolean existsBySalaFechaHora(@Param("salaId") Long salaId,
                                  @Param("fecha")  String fecha,
                                  @Param("hora")   String hora);

    /**
     * Funciones activas dentro de un rango de fechas — útil para
     * mostrar cartelera por período.
     */
    @Query("SELECT DISTINCT f FROM Funcion f " +
            "JOIN FETCH f.pelicula " +
            "JOIN FETCH f.sala " +
            "WHERE f.fecha BETWEEN :inicio AND :fin " +
            "ORDER BY f.fecha ASC, f.horaInicio ASC")
    List<Funcion> findByFechaEntreFechas(@Param("inicio") String inicio,
                                         @Param("fin")    String fin);
}