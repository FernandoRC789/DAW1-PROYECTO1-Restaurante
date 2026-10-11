package com.cibertec.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cibertec.order_service.client.ClienteFeignClient;
import com.cibertec.order_service.client.ComidaFeignClient;
import com.cibertec.order_service.client.MesaFeignClient;
import com.cibertec.order_service.client.UsuarioFeignClient;
import com.cibertec.order_service.dto.ClienteDTO;
import com.cibertec.order_service.dto.DetallePedidoRequestDTO;
import com.cibertec.order_service.dto.DetallePedidoResponseDTO;
import com.cibertec.order_service.dto.PedidoRequestDTO;
import com.cibertec.order_service.dto.PedidoResponseDTO;
import com.cibertec.order_service.model.DetallePedido;
import com.cibertec.order_service.model.Pedido;
import com.cibertec.order_service.repository.PedidoRepository;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private MesaFeignClient mesaFeignClient;
    @Autowired
    private ComidaFeignClient comidaFeignClient;
    @Autowired
    private UsuarioFeignClient usuarioFeignClient;
    @Autowired
    private ClienteFeignClient clienteFeignClient;

    /**
     * Guarda un pedido directo (no recomendado si usas DTO)
     */
    @Transactional
    public Pedido guardarPedido(Pedido pedido) {
        if (pedido.getDetallePedido() == null || pedido.getDetallePedido().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un detalle");
        }
        return pedidoRepository.save(pedido);
    }

    /**
     * Lista todos los pedidos
     */

    @Transactional(readOnly = true)
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    /**
     * Obtiene un pedido por ID
     */

    @Transactional(readOnly = true)
    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
    }

    /**
     * Elimina un pedido por ID
     */
    @Transactional
    public void eliminar(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("Pedido no existe con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    /**
     * Busca pedidos por estado
     */
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorEstado(Long estadoId) {
        return pedidoRepository.findByEstadoId(estadoId);
    }

    /**
     * Busca pedidos por mesa
     */

    @Transactional(readOnly = true)
    public List<Pedido> buscarPorMesa(Long mesaId) {
        return pedidoRepository.findByMesaId(mesaId);
    }

    /**
     * Filtro avanzado (fecha + mesa)
     */

    @Transactional(readOnly = true)
    public List<Pedido> buscarPedidos(LocalDate fecha, String mesa) {
        Long mesaFil = (mesa == null || mesa.trim().isEmpty()) ? null : Long.valueOf(mesa.trim());
        return pedidoRepository.buscarPedidosAdmin(fecha, mesaFil);
    }

    /**
     * MÉTODO PRINCIPAL (CREACIÓN DESDE FRONT)
     * 
     * Convierte un PedidoDTO en entidad Pedido y lo guarda en la base de datos.
     * 
     * INTEGRACIÓN CON OTROS MICROSERVICIOS VIA OPENFEIGN:
     * - Valida y obtiene información de la Mesa desde operations-service
     * - Valida y obtiene información de la Comida desde catalog-service
     * - Valida y obtiene información del Usuario (mesero) desde auth-service
     * - Valida y obtiene información del Cliente desde customer-service
     */

    @Transactional
    public Pedido guardarDesdeDTO(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();

        // 1. VALIDAR MESA DESDE OPERATIONS-SERVICE
        try {
            Map<String, Object> mesa = mesaFeignClient.obtenerMesaPorId(dto.getMesaId());
            if (mesa == null) {
                throw new IllegalArgumentException("Mesa no encontrada en operations-service");
            }
            String estadoMesa = (String) mesa.get("estado_mesa");
            if ("OCUPADO".equals(estadoMesa)) {
                throw new IllegalArgumentException("La mesa ya está ocupada");
            }
        } catch (IllegalArgumentException iae) {
            throw iae; // Propagamos la excepción de negocio limpia (400)
        } catch (Exception e) {
            throw new RuntimeException("Error al validar mesa con operations-service: " + e.getMessage(), e);
        }

        // 2. VALIDAR USUARIO (MESERO) DESDE AUTH-SERVICE (Con fallback seguro unificado)
        Long meseroIdAUsar = dto.getMeseroId() != null ? dto.getMeseroId() : 1L; 
        try {
            Map<String, Object> usuario = usuarioFeignClient.obtenerUsuarioPorId(meseroIdAUsar);
            if (usuario == null && dto.getMeseroId() != null) {
                throw new IllegalArgumentException("Usuario no encontrado en auth-service");
            }
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            System.err.println("Advertencia usuario (auth-service no disponible, usando ID por defecto): " + e.getMessage());
        }

        // 3. VALIDAR CLIENTE DESDE CUSTOMER-SERVICE (si se proporciona)
        if (dto.getClienteId() != null) {
            try {
                ClienteDTO cliente = clienteFeignClient.obtenerClientePorId(dto.getClienteId());
                if (cliente == null) {
                    throw new IllegalArgumentException("Cliente no encontrado en customer-service");
                }
            } catch (IllegalArgumentException iae) {
                throw iae;
            } catch (Exception e) {
                throw new RuntimeException("Error al validar cliente con customer-service: " + e.getMessage(), e);
            }
        }

        pedido.setEstadoId(dto.getEstadoId() != null ? dto.getEstadoId() : 1L); // Fallback por defecto si no viene estado
        pedido.setMesaId(dto.getMesaId());
        pedido.setMeseroId(meseroIdAUsar); 
        pedido.setClienteId(dto.getClienteId());
        pedido.setObservaciones(dto.getObservaciones());

        // 4. DETALLE DEL PEDIDO CON VALIDACIÓN DE COMIDAS Y CÁLCULO SEGURO DE TOTALES
        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal totalGeneral = BigDecimal.ZERO; 

        if (dto.getDetallePedido() == null || dto.getDetallePedido().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un detalle de comida.");
        }

        for (DetallePedidoRequestDTO detDTO : dto.getDetallePedido()) {
            BigDecimal precioUni = BigDecimal.ZERO;

            // VALIDAR COMIDA DESDE CATALOG-SERVICE
            try {
                Map<String, Object> comida = comidaFeignClient.obtenerComidaPorId(detDTO.getComidaId());
                if (comida == null) {
                    throw new IllegalArgumentException("Comida con ID " + detDTO.getComidaId() + " no encontrada en catalog-service");
                }
                Boolean disponible = (Boolean) comida.get("disponible");
                if (disponible != null && !disponible) {
                    throw new IllegalArgumentException("La comida " + comida.get("nombre") + " no está disponible actualmente");
                }
                Object objPrecio = comida.get("precioUni");
                if (objPrecio != null) {
                    precioUni = new BigDecimal(objPrecio.toString());
                }
            } catch (IllegalArgumentException iae) {
                throw iae;
            } catch (Exception e) {
                throw new RuntimeException("Error al comunicarse con catalog-service: " + e.getMessage(), e);
            }

            DetallePedido det = new DetallePedido();
            det.setPedido(pedido);
            det.setComida(detDTO.getComidaId());
            det.setCantidad(detDTO.getCantidad());
            det.setPrecioUni(precioUni);
            
            // 🔥 CÁLCULO BLINDADO DEL SUBTOTAL Y ACUMULADO
            BigDecimal subtotal = precioUni.multiply(BigDecimal.valueOf(detDTO.getCantidad()));
            det.setSubtotal(subtotal);
            
            totalGeneral = totalGeneral.add(subtotal); 
            detalles.add(det);
        }

        pedido.setDetallePedido(detalles);
        pedido.setTotal(totalGeneral); 
        pedido.setFechaRegistrada(LocalDate.now());

        // 5. GUARDAR EN BASE DE DATOS
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        // 6. ACTUALIZAR ESTADO DE MESA A OCUPADO
        try {
            mesaFeignClient.actualizarEstadoMesa(dto.getMesaId(), "OCUPADO");
        } catch (Exception e) {
            System.err.println("Error al actualizar estado de mesa: " + e.getMessage());
        }

        return pedidoGuardado;
    }

    /**
     * Cambia el estado de un pedido
     * 
     * Si el estado es PAGADO, libera la mesa asociada
     */

    @Transactional
    public Pedido cambiarEstado(Long idPedido, Long estadoId) {
        Pedido pedido = pedidoRepository.findById(idPedido)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + idPedido));

        pedido.setEstadoId(estadoId);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // SI EL ESTADO ES PAGADO, LIBERAR LA MESA
        if (estadoId == 4L) {
            try {
                mesaFeignClient.actualizarEstadoMesa(pedido.getMesaId(), "LIBRE");
            } catch (Exception e) {
                System.err.println("Error al liberar mesa: " + e.getMessage());
            }
        }
        return pedidoGuardado;
    }

    /**
     * Obtener los pedidos del mesero
     */

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosDelMesero() {
        return pedidoRepository.findAll();
    }

    /**
     * Actualizar pedido por ID
     */

    @Transactional
    public Pedido actualizarPedido(Long id, Pedido dto) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no existe con ID: " + id));

        if (dto.getEstadoId() != null) {
            pedido.setEstadoId(dto.getEstadoId());
        }
        pedido.setMesaId(dto.getMesaId());
        pedido.setMeseroId(dto.getMeseroId());
        pedido.setObservaciones(dto.getObservaciones());

        List<DetallePedido> detalles = new ArrayList<>();
        for (DetallePedido detDTO : dto.getDetallePedido()) {
            DetallePedido det = new DetallePedido();
            det.setPedido(pedido);
            det.setComida(detDTO.getComida());
            det.setCantidad(detDTO.getCantidad());
            det.setPrecioUni(detDTO.getPrecioUni());
            detalles.add(det);
        }

        pedido.setDetallePedido(detalles);
        return pedidoRepository.save(pedido);
    }

    /**
     * Listar pedidos por estado
     */

    @Transactional(readOnly = true)
    public List<Pedido> listarPorEstado(String estado) {
        try {
            Long estadoId = Long.valueOf(estado);
            return pedidoRepository.findByEstadoId(estadoId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El estado debe ser un ID numérico");
        }
    }
    
 // Método para convertir entidad Pedido a PedidoResponseDTO enriquecido
    /**
     * Convierte una entidad Pedido a PedidoResponseDTO enriquecido con Feign Clients
     */
    public PedidoResponseDTO convertirAResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaRegistrada(pedido.getFechaRegistrada());
        dto.setObservaciones(pedido.getObservaciones());
        dto.setTotal(pedido.getTotal());
        dto.setClienteId(pedido.getClienteId());
        dto.setEstadoId(pedido.getEstadoId());
        dto.setMesaId(pedido.getMesaId());
        dto.setMeseroId(pedido.getMeseroId());

        // 1. Enriquecer Estado
        dto.setEstadoNombre(obtenerNombreEstado(pedido.getEstadoId()));

        // 2. Enriquecer Mesa desde operations-service
        try {
            Map<String, Object> mesa = mesaFeignClient.obtenerMesaPorId(pedido.getMesaId());
            if (mesa != null) {
                dto.setMesaNumero(String.valueOf(mesa.getOrDefault("numero", "Mesa " + pedido.getMesaId())));
            } else {
                dto.setMesaNumero("Mesa #" + pedido.getMesaId());
            }
        } catch (Exception e) {
            dto.setMesaNumero("Mesa #" + pedido.getMesaId());
        }

        // 3. Enriquecer Mesero desde auth-service
        try {
            Map<String, Object> usuario = usuarioFeignClient.obtenerUsuarioPorId(pedido.getMeseroId());
            if (usuario != null) {
                dto.setMeseroNombre(String.valueOf(usuario.getOrDefault("nombre", "Mesero")));
            } else {
                dto.setMeseroNombre("Mesero #" + pedido.getMeseroId());
            }
        } catch (Exception e) {
            dto.setMeseroNombre("Mesero #" + pedido.getMeseroId());
        }
        
     // 4. Mapear detalles y enriquecer con el nombre de la comida desde catalog-service
        if (pedido.getDetallePedido() != null) {
            List<DetallePedidoResponseDTO> listaDetalles = pedido.getDetallePedido().stream().map(d -> {
                DetallePedidoResponseDTO detDto = new DetallePedidoResponseDTO();
                detDto.setIdDetP(d.getIdDetP());
                detDto.setComidaId(d.getComida());
                detDto.setCantidad(d.getCantidad());
                detDto.setPrecioUni(d.getPrecioUni());
                detDto.setSubtotal(d.getSubtotal());

                // 🔍 Enriquecer nombre de la comida vía Feign Client
                try {
                    Map<String, Object> comidaInfo = comidaFeignClient.obtenerComidaPorId(d.getComida());
                    if (comidaInfo != null) {
                        detDto.setComidaNombre(String.valueOf(comidaInfo.getOrDefault("nombre", "Plato #" + d.getComida())));
                    } else {
                        detDto.setComidaNombre("Plato #" + d.getComida());
                    }
                } catch (Exception e) {
                    detDto.setComidaNombre("Plato #" + d.getComida());
                }

                return detDto;
            }).toList();
            dto.setDetallePedido(listaDetalles);
        }

        // 4. Mapear detalles utilizando el DTO independiente
        if (pedido.getDetallePedido() != null) {
            List<DetallePedidoResponseDTO> listaDetalles = pedido.getDetallePedido().stream().map(d -> {
                DetallePedidoResponseDTO detDto = new DetallePedidoResponseDTO();
                detDto.setIdDetP(d.getIdDetP());
                detDto.setComidaId(d.getComida());
                detDto.setCantidad(d.getCantidad());
                detDto.setPrecioUni(d.getPrecioUni());
                detDto.setSubtotal(d.getSubtotal());
                return detDto;
            }).toList();
            dto.setDetallePedido(listaDetalles);
        }

        return dto;
    }

    private String obtenerNombreEstado(Long estadoId) {
        if (estadoId == null) return "DESCONOCIDO";
        switch (estadoId.intValue()) {
            case 1: return "PENDIENTE";
            case 2: return "EN_COCINA";
            case 3: return "LISTO";
            case 4: return "PAGADO";
            case 5: return "CANCELADO";
            default: return "PENDIENTE";
        }
    }

    /**
     * Método para listar el historial enriquecido
     */
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarConDetalle() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream()
                      .map(this::convertirAResponseDTO)
                      .toList();
    }
}
