package com.cinemamonarca.repository;

import com.cinemamonarca.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // LEFT JOIN en funcion — si la función fue borrada, la reserva igual aparece
    @Query("SELECT r FROM Reserva r " +
            "LEFT JOIN FETCH r.cliente " +
            "LEFT JOIN FETCH r.funcion f " +
            "LEFT JOIN FETCH f.pelicula " +
            "LEFT JOIN FETCH f.sala")
    List<Reserva> findAllWithDetails();

    List<Reserva> findByCliente_CustId(Long custId);
    List<Reserva> findByFuncion_FuncionId(Long funcionId);

    @Query("SELECT r FROM Reserva r " +
            "LEFT JOIN FETCH r.cliente c " +
            "LEFT JOIN FETCH r.funcion f " +
            "LEFT JOIN FETCH f.pelicula " +
            "LEFT JOIN FETCH f.sala " +
            "WHERE LOWER(c.nombreCliente) = LOWER(:username) " +
            "OR LOWER(c.numeroCliente) = LOWER(:username)")
    List<Reserva> findByUsername(@Param("username") String username);

    @Query("SELECT r FROM Reserva r " +
            "LEFT JOIN FETCH r.cliente c " +
            "LEFT JOIN FETCH r.funcion f " +
            "LEFT JOIN FETCH f.pelicula " +
            "LEFT JOIN FETCH f.sala " +
            "WHERE LOWER(c.direccionCliente) = LOWER(:email)")
    List<Reserva> findByClienteEmail(@Param("email") String email);
}