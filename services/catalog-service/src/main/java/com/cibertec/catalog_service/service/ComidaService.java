package com.cibertec.catalog_service.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.catalog_service.Repository.CategoriaRepository;
import com.cibertec.catalog_service.Repository.ComidaRepository;
import com.cibertec.catalog_service.client.PedidoFeignClient;
import com.cibertec.catalog_service.dto.PedidoDTO;
import com.cibertec.catalog_service.model.Categoria;
import com.cibertec.catalog_service.model.Comida;

/**
 * SERVICIO: ComidaService
 * 
 * Contiene la lógica de negocio para la entidad Comida.
 * Responsabilidades: Gestión CRUD de comidas, Validaciones, Control de disponibilidad
 */
@Service
public class ComidaService {
    @Autowired
    private ComidaRepository repocom;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PedidoFeignClient pedidoFeignClient;

    /**
     * Listar todas las comidas
     */
    public List<Comida> listar() {
        return repocom.findAll();
    }

    /**
     * Obtener comida por ID
     */
    public Comida obtenerPorId(Long id) {
        return repocom.findById(id).orElseThrow(() -> new IllegalArgumentException("Comida no encontrada con ID: " + id));
    }

    /**
     * Guardar o actualizar comida
     */
    @Transactional
    public Comida guardar(Comida comida) {
        if (comida.getCategoria() == null || comida.getCategoria().getIdCat() == null) {
            throw new IllegalArgumentException("Debe enviar una categoria válida");
        }

        Categoria categoria = categoriaRepository.findById(comida.getCategoria().getIdCat())
                .orElseThrow(() -> new IllegalArgumentException("Categoria no existe"));

        comida.setCategoria(categoria);

        if (comida.getNombre() == null || comida.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (comida.getPrecioUni() == null || comida.getPrecioUni().doubleValue() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        Comida guardada = repocom.save(comida);
        Hibernate.initialize(guardada.getCategoria());

        return guardada;
    }

    /**
     * Eliminar comida
     */
    @Transactional
    public void eliminar(Long id) {
        if (!repocom.existsById(id)) {
            throw new IllegalArgumentException("La comida no existe con ID: " + id);
        }
        repocom.deleteById(id);
    }

    /**
     * Cambiar disponibilidad (cocina)
     */
    @Transactional
    public Comida cambiarDisponible(Long id, boolean estado) {
        Comida comida = obtenerPorId(id);
        comida.setDisponible(estado);
        return repocom.save(comida);
    }

    /**
     * Listar Comidas disponibles
     */
    public List<Comida> listarDisponibles() {
        return repocom.findByDisponibleTrue();
    }

    /**
     * Listar Comidas no disponibles
     */
    public List<Comida> listarNoDisponibles() {
        return repocom.findByDisponibleFalse();
    }

    /**
     * Buscar comidas por nombre
     */
    public List<Comida> buscarPorNombre(String nombre) {
        return repocom.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Buscar comidas por categoría
     */
    public List<Comida> buscarPorCategoria(Long idCat) {
        Categoria categoria = categoriaRepository.findById(idCat)
                .orElseThrow(() -> new IllegalArgumentException("Categoria no existe con ID: " + idCat));
        return repocom.findByCategoria(categoria);
    }

    /**
     * Obtiene los pedidos que contienen una comida específica desde order-service
     */
    public List<PedidoDTO> obtenerPedidosPorComida(Long comidaId) {
        try {
            return pedidoFeignClient.obtenerPedidosPorComida(comidaId);
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar pedidos con order-service: " + e.getMessage(), e);
        }
    }
}
