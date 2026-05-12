package com.cinemamonarca.repository;

import com.cinemamonarca.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByNombreClienteContainingIgnoreCase(String nombre);

    Optional<Cliente> findByDireccionCliente(String direccionCliente);
}
