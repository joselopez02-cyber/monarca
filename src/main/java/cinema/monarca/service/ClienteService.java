package cinema.monarca.service;

import cinema.monarca.model.Cliente;
import cinema.monarca.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreClienteContainingIgnoreCase(nombre);
    }

    public java.util.Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByDireccionCliente(email);
    }

    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente datos) {
        Cliente cliente = obtenerPorId(id);
        cliente.setNombreCliente(datos.getNombreCliente());
        cliente.setCustAge(datos.getCustAge());
        cliente.setDireccionCliente(datos.getDireccionCliente());
        cliente.setNumeroCliente(datos.getNumeroCliente());
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }
}
