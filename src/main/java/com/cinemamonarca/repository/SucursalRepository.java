package com.cinemamonarca.repository;

import com.cinemamonarca.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    @Query("SELECT s FROM Sucursal s JOIN FETCH s.cine")
    List<Sucursal> findAllWithCine();

    @Query("SELECT s FROM Sucursal s JOIN FETCH s.cine WHERE s.cine.cineId = :cineId")
    List<Sucursal> findByCine_CineId(@Param("cineId") Long cineId);
}