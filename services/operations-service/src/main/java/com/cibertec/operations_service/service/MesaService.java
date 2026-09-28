package com.cibertec.operations_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.operations_service.model.Mesa;
import com.cibertec.operations_service.repository.MesaRepository;
import com.cibertec.operations_service.utilsEnum.EstadoMesa;

/**
 * 📌 Servicio encargado de la lógica de negocio de las mesas
 * 
 * Responsabilidades:
 * - Gestionar CRUD de mesas
 * - Validar reglas de negocio (ej: no eliminar mesas con pedidos)
 * - Controlar estados de mesa (LIBRE, OCUPADO, etc.)
 * 
 * NOTA:
 * Este servicio actúa como intermediario entre el Controller y el Repository
 */
@Service
public class MesaService {

	@Autowired
	private MesaRepository mesaRepository;
	
    /**
     * ✅ Lista todas las mesas del sistema
     * 
     * @return List<Mesa>
     */
	public List<Mesa> listar(){
		return mesaRepository.findAll();
	}
	
    /**
     * ✅ Obtiene una mesa por su ID
     * 
     * 🔥 Mejora: lanza excepción si no existe
     */
	public Mesa obtenerPorId(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
    }
	
    /**
     * ✅ Guarda una nueva mesa o actualiza una existente
     * 
     * 🔥 Reglas aplicadas:
     * - Si no tiene estado → se asigna LIBRE por defecto
     */
	public Mesa guardar(Mesa mesa) {

	    // 🔥 SI ES UPDATE (ya tiene ID)
	    if (mesa.getIdMesa() != null) {

	        Mesa existente = mesaRepository.findById(mesa.getIdMesa())
	                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

	        // 🔥 MANTENER estado actual si no viene en el request
	        if (mesa.getEstado_mesa() == null) {
	            mesa.setEstado_mesa(existente.getEstado_mesa());
	        }
	    } else {
	        // 🔥 SI ES NUEVA
	        if (mesa.getEstado_mesa() == null) {
	            mesa.setEstado_mesa(EstadoMesa.LIBRE);
	        }
	    }

	    return mesaRepository.save(mesa);
	}
	
    /**
     * ✅ Elimina una mesa
     * 
     * 🔥 Mejora:
     * - Verifica que exista antes de eliminar
     */
	public void eliminar(Long id) {
	    Mesa mesa = mesaRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Mesa no existe"));

	    // O Opción A: Borrado físico directo
	    mesaRepository.deleteById(id);

	    // O Opción B: Borrado lógico (recomendado para no romper historial)
	    // mesa.setEstadoMesa(EstadoMesa.NO_DISPONIBLE);
	    // mesaRepository.save(mesa);
	}
    
    /**
     * ✅ Cambiar estado de la mesa manualmente
     * 
     * @param id ID de la mesa
     * @param estado nuevo estado (LIBRE, OCUPADO, etc.)
     * @return Mesa actualizada
     */
    public Mesa cambiarEstado(Long id, EstadoMesa estado) {

        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        mesa.setEstado_mesa(estado);

        return mesaRepository.save(mesa);
    }
}
