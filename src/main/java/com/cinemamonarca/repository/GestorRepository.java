package com.cinemamonarca.repository;
import com.cinemamonarca.model.Gestor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface GestorRepository extends JpaRepository<Gestor, Long> {
    List<Gestor> findBySucursal_BranId(Long sucursalId);
}
