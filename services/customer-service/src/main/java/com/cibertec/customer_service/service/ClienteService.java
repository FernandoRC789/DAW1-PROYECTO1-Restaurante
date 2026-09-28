package com.cibertec.customer_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.customer_service.exception.RecursoDuplicadoException;
import com.cibertec.customer_service.exception.RecursoNoEncontradoException;
import com.cibertec.customer_service.model.Cliente;
import com.cibertec.customer_service.repository.ClienteRepository;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorDocumento(String documento) {
        return clienteRepository.findByDocumento(documento)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Cliente no encontrado con documento: " + documento));
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Cliente crear(Cliente cliente) {
        if (clienteRepository.existsByDocumento(cliente.getDocumento())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un cliente con documento: " + cliente.getDocumento());
        }

        cliente.setIdCliente(null);
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente datos) {
        Cliente actual = obtenerPorId(id);

        if (clienteRepository.findByDocumento(datos.getDocumento())
                .filter(existente -> !existente.getIdCliente().equals(id))
                .isPresent()) {
            throw new RecursoDuplicadoException(
                    "Ya existe otro cliente con documento: " + datos.getDocumento());
        }

        actual.setNombre(datos.getNombre());
        actual.setDocumento(datos.getDocumento());
        actual.setTipoDocumento(datos.getTipoDocumento());
        actual.setDireccion(datos.getDireccion());
        actual.setCorreo(datos.getCorreo());
        actual.setTelefono(datos.getTelefono());

        return clienteRepository.save(actual);
    }

    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    "Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    /**
     * Mantiene el comportamiento del monolito:
     * si el documento existe, devuelve el cliente;
     * si no existe, lo crea.
     */
    public Cliente obtenerOCrear(Cliente cliente) {
        return clienteRepository.findByDocumento(cliente.getDocumento())
                .orElseGet(() -> clienteRepository.save(cliente));
    }
}
