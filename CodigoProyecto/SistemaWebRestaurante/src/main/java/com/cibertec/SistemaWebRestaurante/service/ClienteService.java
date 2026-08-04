package com.cibertec.SistemaWebRestaurante.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.SistemaWebRestaurante.model.Cliente;
import com.cibertec.SistemaWebRestaurante.repository.ClienteRepository;

/**
 * 📌 SERVICIO: ClienteService
 * 
 * 🔥 RESPONSABILIDAD:
 * Contiene la lógica de negocio relacionada a clientes
 * 
 * 🔥 IMPORTANTE:
 * Aquí NO solo llamamos al repository,
 * también validamos reglas del negocio
 */
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * 📋 Listar todos los clientes
     */
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    /**
     * 🔍 Obtener cliente por ID
     */
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    /**
     * 💾 Guardar cliente (crear o actualizar)
     * 
     * 🔥 REGLAS:
     * - No permitir documentos duplicados
     */
    public Cliente guardar(Cliente cliente) {

        // 🔥 VALIDACIÓN: documento único
        if (cliente.getIdCliente() == null) { // solo al crear
            if (clienteRepository.existsByDocumento(cliente.getDocumento())) {
                throw new RuntimeException("Ya existe un cliente con ese documento");
            }
        }

        return clienteRepository.save(cliente);
    }

    /**
     * ❌ Eliminar cliente
     */
    public void eliminar(Long id) {

        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no existe");
        }

        clienteRepository.deleteById(id);
    }

    /**
     * 🔍 Buscar cliente por documento
     * 
     * 🔥 Muy usado en caja (cajero)
     */
    public Cliente buscarPorDocumento(String documento) {
        return clienteRepository.findByDocumento(documento)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    /**
     * 🔍 Buscar clientes por nombre (tipo búsqueda en vivo)
     */
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * 🔥 MÉTODO CLAVE (PRO)
     * 
     * 👉 Si el cliente existe → lo retorna
     * 👉 Si NO existe → lo crea automáticamente
     * 
     * 🔥 Esto se usa cuando:
     * el cajero registra un comprobante
     */
    public Cliente obtenerOCrear(Cliente cliente) {

        return clienteRepository.findByDocumento(cliente.getDocumento())
                .orElseGet(() -> clienteRepository.save(cliente));
    }
}
