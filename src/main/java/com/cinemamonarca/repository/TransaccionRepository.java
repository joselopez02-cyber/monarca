package com.cinemamonarca.repository;
import com.cinemamonarca.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByCliente_CustId(Long custId);
    List<Transaccion> findByReserva_ResCode(Long resCode);
}
