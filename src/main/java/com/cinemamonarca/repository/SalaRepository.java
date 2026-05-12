package com.cinemamonarca.repository;

import com.cinemamonarca.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {

    @Query("SELECT s FROM Sala s LEFT JOIN FETCH s.sucursal su LEFT JOIN FETCH su.cine")
    List<Sala> findAllWithSucursal();

    List<Sala> findByTipo(Sala.TipoSala tipo);
    List<Sala> findBySucursal_BranId(Long branId);
}