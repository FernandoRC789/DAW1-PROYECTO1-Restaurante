package com.cibertec.SistemaWebRestaurante.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cibertec.SistemaWebRestaurante.dto.ComprobanteResponseDTO;
import com.cibertec.SistemaWebRestaurante.dto.DetalleComprobanteDTO;
import com.cibertec.SistemaWebRestaurante.dto.DetallePedidoDTO;
import com.cibertec.SistemaWebRestaurante.dto.PedidoDTO;
import com.cibertec.SistemaWebRestaurante.model.Comida;
import com.cibertec.SistemaWebRestaurante.model.ComprobanteDePago;
import com.cibertec.SistemaWebRestaurante.model.DetallePedido;
import com.cibertec.SistemaWebRestaurante.model.Estado;
import com.cibertec.SistemaWebRestaurante.model.Mesa;
import com.cibertec.SistemaWebRestaurante.model.Pedido;
import com.cibertec.SistemaWebRestaurante.model.Usuario;
import com.cibertec.SistemaWebRestaurante.repository.ComidaRepository;
import com.cibertec.SistemaWebRestaurante.repository.EstadoRepository;
import com.cibertec.SistemaWebRestaurante.repository.MesaRepository;
import com.cibertec.SistemaWebRestaurante.repository.PedidoRepository;
import com.cibertec.SistemaWebRestaurante.repository.UsuarioRepository;
import com.cibertec.SistemaWebRestaurante.utilsEnum.EstadoMesa;

/**
 * ============================================================
 * 📦 PedidoService
 * ============================================================
 * 
 * Servicio encargado de manejar toda la lógica de negocio
 * relacionada a los pedidos dentro del sistema.
 * 
 * Funcionalidades principales:
 * ✔ Crear pedidos desde DTO (flujo mesero)
 * ✔ Asociar pedido con usuario logueado (mesero)
 * ✔ Gestionar detalle del pedido automáticamente
 * ✔ Calcular precios desde comida (evita manipulación)
 * ✔ Cambiar estado del pedido (flujo cocina/caja)
 * ✔ Consultas por filtros (estado, mesa, fecha)
 * 
 * Flujo típico:
 * Mesero crea pedido → Cocina procesa → Caja cobra
 * 
 * ============================================================
 */

@Service
public class PedidoService {

	@Autowired
	private EstadoRepository estadoRepository;

	@Autowired
	private MesaRepository mesaRepository;

	@Autowired
	private ComidaRepository comidaRepository;
	
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * 🔹 Guarda un pedido directo (no recomendado si usas DTO)
     
    public Pedido guardarPedido(Pedido pedido) {

        if (pedido.getDetallePedido() == null || pedido.getDetallePedido().isEmpty()) {
            throw new RuntimeException("El pedido debe tener al menos un detalle");
        }

        return pedidoRepository.save(pedido);
    }*/
    
    /**
     * 🔹 Lista todos los pedidos
     */
	public List<Pedido> listar(){
		return pedidoRepository.findAll();
	}
	
    /**
     * 🔹 Obtiene un pedido por ID
     */
	public Pedido obtenerPorId(Long id) {
	    return pedidoRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
	}

    /**
     * 🔹 Elimina un pedido por ID
     */
	public void eliminar(Long id) {
	    if (!pedidoRepository.existsById(id)) {
	        throw new RuntimeException("Pedido no existe");
	    }
	    pedidoRepository.deleteById(id);
	}
	
    /**
     * 🔹 Busca pedidos por estado
     */
	public List<Pedido> buscarPorEstado(Estado estado){
	    return pedidoRepository.findByEstado(estado);
	}
	
    /**
     * 🔹 Busca pedidos por mesa
     */
	public List<Pedido> buscarPorMesa(Mesa mesa){
	    return pedidoRepository.findByMesa(mesa);
	}
	
	
    /**
     * 🔹 Filtro avanzado (fecha + mesa)
     */
	public List<Pedido> buscarPedidos(LocalDate fecha, String mesa){
		String mesaFil = (mesa == null || mesa.trim().isEmpty()) ? null : mesa.trim();
		return pedidoRepository.buscarPedidosAdmin(fecha, mesaFil);
	}
	
    /**
     * 🔥 MÉTODO PRINCIPAL (CREACIÓN DESDE FRONT)
     * 
     * Convierte un PedidoDTO en entidad Pedido
     * y lo guarda en la base de datos.
     */
    public Pedido guardarDesdeDTO(PedidoDTO dto) {

        Pedido pedido = new Pedido();

        // 🔥 OBTENER USUARIO LOGUEADO (MESERO)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 🔥 MEJORA: Validación de seguridad
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String username = auth.getName();

        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        pedido.setMesero(usuario); // 🔥 RELACIÓN PEDIDO → MESERO

        // 🔹 Buscar estado
        Estado estado = estadoRepository.findById(dto.getEstadoId())
            .orElseThrow(() -> new RuntimeException("Estado no existe"));


        // 🔹 Buscar mesa
        Mesa mesa = mesaRepository.findById(dto.getMesaId())
            .orElseThrow(() -> new RuntimeException("Mesa no existe"));
        
        //validar mesa
        if (mesa.getEstado_mesa() == EstadoMesa.OCUPADO) {
            throw new RuntimeException("La mesa ya está ocupada");
        }
        
     // 🔥 AGREGADO: marcar mesa como OCUPADA
        mesa.setEstado_mesa(EstadoMesa.OCUPADO);
        mesaRepository.save(mesa);
        
        pedido.setEstado(estado);
        pedido.setMesa(mesa);
        pedido.setObservaciones(dto.getObservaciones());

        // 🔥 DETALLE DEL PEDIDO
        List<DetallePedido> detalles = new ArrayList<>();

        for (DetallePedidoDTO detDTO : dto.getDetalle()) {

            Comida comida = comidaRepository.findById(detDTO.getComidaId())
                .orElseThrow(() -> new RuntimeException("Comida no existe"));

            // 🔥 MEJORA: Validar disponibilidad
            if (!comida.isDisponible()) {
                throw new RuntimeException("La comida " + comida.getNombre() + " no está disponible");
            }

            DetallePedido det = new DetallePedido();
            det.setPedido(pedido); // 🔥 RELACIÓN INVERSA
            det.setComida(comida);
            det.setCantidad(detDTO.getCantidad());

            // 🔥 IMPORTANTE: precio viene de BD (seguridad)
            det.setPrecioUni(comida.getPrecioUni());

            detalles.add(det);
        }

        pedido.setDetallePedido(detalles);

        // 🔥 MEJORA: fecha explícita (por si PrePersist falla)
        pedido.setFechaRegistrada(LocalDate.now());

        return pedidoRepository.save(pedido);
    }
	
    /**
     * 🔥 Cambia el estado de un pedido
     * 
     * Usado por cocina, mesero o caja
     */
    public Pedido cambiarEstado(Long idPedido, Long estadoId) {

    	
        Pedido pedido = pedidoRepository.findById(idPedido)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        Estado estado = estadoRepository.findById(estadoId)
            .orElseThrow(() -> new RuntimeException("Estado no existe"));

        if (estado.getNombre().equalsIgnoreCase("PAGADO")) {
        	Mesa mesa = pedido.getMesa();
    	    mesa.setEstado_mesa(EstadoMesa.LIBRE);
    	    mesaRepository.save(mesa);
    	}
    	
        pedido.setEstado(estado);

        return pedidoRepository.save(pedido);
    }
    
    /*
     * Obtener los pedidos del mesero logueado
     * usado por el mesero/cocina recibe la lista
     * */
    public List<Pedido> obtenerPedidosDelMesero() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return pedidoRepository.findByMesero_IdUser(usuario.getIdUser());
    }
    
    
    //actualizar pedido por id
    public Pedido actualizarPedido(Long id, PedidoDTO dto) {

        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido no existe"));

        // 🔥 VALIDACIÓN IMPORTANTE
        if (pedido.getEstado().getNombre().equals("PAGADO")) {
            throw new RuntimeException("No se puede editar un pedido pagado");
        }

        // 🔹 Actualizar mesa
        Mesa mesa = mesaRepository.findById(dto.getMesaId())
            .orElseThrow(() -> new RuntimeException("Mesa no existe"));

        pedido.setMesa(mesa);

        // 🔹 Observaciones
        pedido.setObservaciones(dto.getObservaciones());

        // 🔥 Rehacer detalle completo
        List<DetallePedido> detalles = new ArrayList<>();

        for (DetallePedidoDTO detDTO : dto.getDetalle()) {

            Comida comida = comidaRepository.findById(detDTO.getComidaId())
                .orElseThrow(() -> new RuntimeException("Comida no existe"));

            DetallePedido det = new DetallePedido();
            det.setPedido(pedido);
            det.setComida(comida);
            det.setCantidad(detDTO.getCantidad());
            det.setPrecioUni(comida.getPrecioUni());

            detalles.add(det);
        }

        pedido.setDetallePedido(detalles);

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPorEstado(String estado) {
        return pedidoRepository.findByEstadoNombre(estado);
    }
    

}
