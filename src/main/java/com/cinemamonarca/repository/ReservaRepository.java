package com.cinemamonarca.repository;

import com.cinemamonarca.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT r FROM Reserva r " +
            "LEFT JOIN FETCH r.cliente " +
            "LEFT JOIN FETCH r.funcion f " +
            "LEFT JOIN FETCH f.pelicula " +
            "LEFT JOIN FETCH f.sala")
    List<Reserva> findAllWithDetails();

    List<Reserva> findByCliente_CustId(Long custId);
    List<Reserva> findByFuncion_FuncionId(Long funcionId);

    // ✅ Busca por nombre o número de contacto del cliente
    @Query("SELECT r FROM Reserva r " +
            "LEFT JOIN FETCH r.cliente c " +
            "LEFT JOIN FETCH r.funcion f " +
            "LEFT JOIN FETCH f.pelicula " +
            "LEFT JOIN FETCH f.sala " +
            "WHERE LOWER(c.nombreCliente) LIKE LOWER(CONCAT('%', :username, '%')) " +
            "OR LOWER(c.numeroCliente) = LOWER(:username)")
    List<Reserva> findByUsername(@Param("username") String username);

    // ✅ Busca correctamente por número de contacto (antes buscaba por dirección)
    @Query("SELECT r FROM Reserva r " +
            "LEFT JOIN FETCH r.cliente c " +
            "LEFT JOIN FETCH r.funcion f " +
            "LEFT JOIN FETCH f.pelicula " +
            "LEFT JOIN FETCH f.sala " +
            "WHERE LOWER(c.numeroCliente) = LOWER(:contacto) " +
            "OR LOWER(c.nombreCliente) LIKE LOWER(CONCAT('%', :contacto, '%'))")
    List<Reserva> findByClienteEmail(@Param("contacto") String contacto);
}