package com.cibertec.catalog_service.servic;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.catalog_service.Repository.CategoriaRepository;
import com.cibertec.catalog_service.Repository.ComidaRepository;
import com.cibertec.catalog_service.model.Categoria;
import com.cibertec.catalog_service.model.Comida;

/**
 * 📌 SERVICIO: ComidaService
 * 
 * Contiene la lógica de negocio para la entidad Comida.
 * 
 * 🔥 Responsabilidades: - Gestión CRUD de comidas - Validaciones (nombre único,
 * existencia, etc.) - Control de disponibilidad
 * 
 * 🔥 Usado por: - Controlador (API REST) - Cocina (gestión de menú) - Mesero
 * (visualización de productos disponibles)
 */
@Service
public class ComidaService {
	@Autowired
	private ComidaRepository repocom;

	@Autowired
	private CategoriaRepository categoriaRepository;

	/**
	 * 📋 Listar todas las comidas
	 */
	public List<Comida> listar() {
		return repocom.findAll();
	}

	/**
	 * 🔍 Obtener comida por ID
	 * 
	 * 🔥 MEJORA: lanza excepción si no existe
	 */
	public Comida obtenerPorId(Long id) {
		return repocom.findById(id).orElseThrow(() -> new RuntimeException("Comida no encontrada"));
	}

	/**
	 * 💾 Guardar o actualizar comida
	 * 
	 * 🔥 MEJORAS: - Validar nombre duplicado - Validar precio
	 */
	public Comida guardar(Comida comida) {

		if (comida.getCategoriaid() == null || comida.getCategoriaid().getIdCat() == null) {
			throw new RuntimeException("Debe enviar una categoria válida");
		}

		// 🔍 Buscar categoría real
		Categoria categoria = categoriaRepository.findById(comida.getCategoriaid().getIdCat())
				.orElseThrow(() -> new RuntimeException("Categoria no existe"));

		// 🔥 Asignar categoría real
		comida.setCategoriaid(categoria);

		// 🔥 Validaciones
		if (comida.getNombre() == null || comida.getNombre().trim().isEmpty()) {
			throw new RuntimeException("El nombre es obligatorio");
		}

		if (comida.getPrecioUni() == null || comida.getPrecioUni().doubleValue() <= 0) {
			throw new RuntimeException("El precio debe ser mayor a 0");
		}

		Comida guardada = repocom.save(comida);

		// 🔥 PRO: forzar carga de categoría
		Hibernate.initialize(guardada.getCategoriaid());

		return guardada;
	}

	/**
	 * ❌ Eliminar comida
	 */
	public void eliminar(Long id) {

		if (!repocom.existsById(id)) {
			throw new RuntimeException("La comida no existe");
		}

		repocom.deleteById(id);
	}

	/**
	 * 🔥 Cambiar disponibilidad (cocina)
	 */
	public Comida cambiarDisponible(Long id, boolean estado) {

		Comida comida = obtenerPorId(id);

		comida.setDisponible(estado);

		return repocom.save(comida);
	}

	/**
	 * 🔥 Listar Comidas disponibles (cocina)
	 */
	public List<Comida> listarDisponibles() {
		return repocom.findByDisponibleTrue();
	}

	/**
	 * 🔥 Listar Comidas disponibles (cocina)
	 */
	public List<Comida> listarNoDisponibles() {
		return repocom.findByDisponibleFalse();
	}

	/**
	 * 🔍 Buscar comidas por nombre
	 * 
	 * Permite búsqueda parcial (LIKE)
	 */
	public List<Comida> buscarPorNombre(String nombre) {
		return repocom.findByNombreContainingIgnoreCase(nombre);
	}

	/**
	 * 🏷️ Buscar comidas por categoría
	 */
	public List<Comida> buscarPorCategoria(Long idCat) {

		// 🔥 Validar que exista la categoría
		Categoria categoria = categoriaRepository.findById(idCat)
				.orElseThrow(() -> new RuntimeException("Categoria no existe"));

		return repocom.findByCategoriaid(categoria);
	}
}
