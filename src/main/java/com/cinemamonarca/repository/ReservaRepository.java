package com.cinemamonarca.repository;

import com.cinemamonarca.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Carga funcion → sala → sucursal para que el frontend pueda filtrar
    @Query("SELECT r FROM Reserva r " +
           "LEFT JOIN FETCH r.cliente " +
           "LEFT JOIN FETCH r.funcion f " +
           "LEFT JOIN FETCH f.pelicula " +
           "LEFT JOIN FETCH f.sala s " +
           "LEFT JOIN FETCH s.sucursal su " +
           "LEFT JOIN FETCH su.cine")
    List<Reserva> findAllWithDetails();

    List<Reserva> findByCliente_CustId(Long custId);
    List<Reserva> findByFuncion_FuncionId(Long funcionId);
}
