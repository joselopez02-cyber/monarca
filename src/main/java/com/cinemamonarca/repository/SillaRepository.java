package com.cinemamonarca.repository;

import com.cinemamonarca.model.Silla;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SillaRepository extends JpaRepository<Silla, Long> {

    List<Silla> findByFuncion_FuncionIdOrderByFilaAscNumeroAsc(Long funcionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Silla s WHERE s.funcion.funcionId = :funcionId AND s.fila = :fila AND s.numero = :numero")
    Optional<Silla> findByFuncion_FuncionIdAndFilaAndNumero(
            @Param("funcionId") Long funcionId,
            @Param("fila") String fila,
            @Param("numero") Integer numero);

    long countByFuncion_FuncionIdAndEstado(Long funcionId, Silla.Estado estado);

    @Query("SELECT s FROM Silla s WHERE s.reserva.resCode = :reservaId")
    List<Silla> findByReservaId(@Param("reservaId") Long reservaId);

    @Query("SELECT s FROM Silla s " +
           "WHERE s.estado IN (com.cinemamonarca.model.Silla.Estado.OCUPADA, " +
           "                   com.cinemamonarca.model.Silla.Estado.RESERVADA) " +
           "AND s.reserva IS NULL")
    List<Silla> findSillasHuerfanas();
}