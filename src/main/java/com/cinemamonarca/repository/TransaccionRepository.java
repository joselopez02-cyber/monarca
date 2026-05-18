package com.cinemamonarca.repository;

import com.cinemamonarca.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    // LEFT JOIN FETCH reserva para que los snap_* estén disponibles
    // aunque la función ya haya sido eliminada
    @Query("SELECT t FROM Transaccion t " +
           "LEFT JOIN FETCH t.cliente " +
           "LEFT JOIN FETCH t.reserva r " +
           "LEFT JOIN FETCH r.funcion f " +
           "LEFT JOIN FETCH f.pelicula " +
           "LEFT JOIN FETCH f.sala")
    List<Transaccion> findAllWithDetails();

    @Query("SELECT t FROM Transaccion t " +
           "LEFT JOIN FETCH t.cliente " +
           "LEFT JOIN FETCH t.reserva r " +
           "LEFT JOIN FETCH r.funcion f " +
           "LEFT JOIN FETCH f.pelicula " +
           "LEFT JOIN FETCH f.sala " +
           "WHERE t.cliente.custId = :custId")
    List<Transaccion> findByCliente_CustId(@Param("custId") Long custId);

    @Query("SELECT t FROM Transaccion t " +
           "LEFT JOIN FETCH t.cliente " +
           "LEFT JOIN FETCH t.reserva r " +
           "LEFT JOIN FETCH r.funcion f " +
           "LEFT JOIN FETCH f.pelicula " +
           "LEFT JOIN FETCH f.sala " +
           "WHERE t.reserva.resCode = :resCode")
    List<Transaccion> findByReserva_ResCode(@Param("resCode") Long resCode);
}