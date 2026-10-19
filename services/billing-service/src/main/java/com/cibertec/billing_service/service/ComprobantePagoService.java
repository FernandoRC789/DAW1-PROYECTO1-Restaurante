package com.cibertec.billing_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.billing_service.client.CatalogoFeignClient;
import com.cibertec.billing_service.client.ClienteRestConsumer;
import com.cibertec.billing_service.client.MesaFeignClient;
import com.cibertec.billing_service.client.OrderRestConsumer;
import com.cibertec.billing_service.client.UsuarioFeignClient;
import com.cibertec.billing_service.dto.CierreCajaDTO;
import com.cibertec.billing_service.dto.ClienteDTO;
import com.cibertec.billing_service.dto.ComidaDTO;
import com.cibertec.billing_service.dto.ComprobanteDTO;
import com.cibertec.billing_service.dto.ComprobanteResponseDTO;
import com.cibertec.billing_service.dto.DetalleComprobanteDTO;
import com.cibertec.billing_service.dto.external.PedidoDTO;
import com.cibertec.billing_service.model.ComprobanteDePago;
import com.cibertec.billing_service.model.EstadoComprobante;
import com.cibertec.billing_service.repository.ComprobanteDePagoRepository;
import com.cibertec.billing_service.repository.EstadoComprobanteRepository;
import com.cibertec.billing_service.config.RabbitMQConfig;
import com.cibertec.billing_service.event.ComprobanteAnuladoEvento;
import com.cibertec.billing_service.event.ComprobanteEmitidoEvento;
import com.cibertec.billing_service.utilsEnum.MetodoPago;
import com.cibertec.billing_service.utilsEnum.TipoComprobante;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 📌 SERVICIO: ComprobantePagoService
 * 
 * Maneja la lógica de negocio de comprobantes e integra Feign Client
 * para consultar el microservicio 'customer-service'.
 */
@Service
public class ComprobantePagoService {

    @Autowired
    private ComprobanteDePagoRepository comprobanteRepo;

    @Autowired
    private EstadoComprobanteRepository estadoRepo;

    @Autowired
    private ClienteRestConsumer clienteRestConsumer;
    
    @Autowired
    private OrderRestConsumer orderRestConsumer;

    @Autowired
    private MesaFeignClient mesaFeignClient;

    @Autowired
    private UsuarioFeignClient usuarioFeignClient;

    @Autowired
    private CatalogoFeignClient catalogoFeignClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 🔥 PROCESO PRINCIPAL: GENERAR COMPROBANTE
     */

    @Transactional
    public ComprobanteDePago crearComprobante(
            Long orderId,
            Long clienteId,
            BigDecimal total,
            TipoComprobante tipo,
            MetodoPago metodoPago,
            Long userId
    ) {
        if (comprobanteRepo.existsByOrderId(orderId)) {
            throw new RuntimeException("El pedido con ID " + orderId + " ya tiene un comprobante registrado.");
        }

        String numero = generarNumeroComprobante(tipo);

        EstadoComprobante estado = estadoRepo.findByNombre("PAGADO")
                .orElseThrow(() -> new RuntimeException("Estado PAGADO no configurado"));

        ComprobanteDePago comp = new ComprobanteDePago();
        comp.setOrderId(orderId);
        comp.setCliente(clienteId);
        comp.setFechaEmision(LocalDateTime.now());
        comp.setTotal(total);
        comp.setTipoComprobante(tipo);
        comp.setNumero(numero);
        comp.setMetodoPago(metodoPago);
        comp.setEstado(estado);
        comp.setUserID(userId);

        ComprobanteDePago comprobanteGuardado = comprobanteRepo.save(comp);

        // 📩 Publicar evento de comprobante emitido a RabbitMQ
        try {
            ComprobanteEmitidoEvento evento = new ComprobanteEmitidoEvento();
            evento.setIdComprobante(comprobanteGuardado.getIdComprobantePago());
            evento.setOrderId(comprobanteGuardado.getOrderId());
            evento.setClienteId(comprobanteGuardado.getCliente());
            evento.setTotal(comprobanteGuardado.getTotal());
            evento.setTipoComprobante(comprobanteGuardado.getTipoComprobante() != null ? comprobanteGuardado.getTipoComprobante().name() : null);
            evento.setMetodoPago(comprobanteGuardado.getMetodoPago() != null ? comprobanteGuardado.getMetodoPago().name() : null);
            evento.setNumero(comprobanteGuardado.getNumero());
            evento.setFechaEmision(comprobanteGuardado.getFechaEmision());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_COMPROBANTE_EMITIDO, evento);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo publicar evento comprobante.emitido: " + e.getMessage());
        }

        /*
         * 🔥 LIBERAR MESA DESDE OPERATIONS-SERVICE
         * 
         * Cuando se genera un comprobante de pago, se debe liberar
         * la mesa asociada al pedido para que pueda ser usada por otros clientes.
         */
        try {
            PedidoDTO pedidoDTO = orderRestConsumer.obtenerPedidoPorId(orderId);
            if (pedidoDTO != null && pedidoDTO.getMesaId() != null) {
            	mesaFeignClient.actualizarEstadoMesa(pedidoDTO.getMesaId(), "LIBRE");
            	}
        } catch (Exception e) {
            System.err.println("Error al liberar mesa: " + e.getMessage());
        }

        return comprobanteGuardado;
    }

    /**
     * CREAR DESDE DTO (Optimizado para evitar bloqueos de red en la BD)
     */
    @Transactional
    public ComprobanteDePago crearDesdeDTO(ComprobanteDTO dto) {
    	try {
    	// 1. Validaciones rápidas de negocio (Sin abrir transacción de BD aún)
        if (comprobanteRepo.existsByOrderId(dto.getPedidoId())) {
            throw new RuntimeException("El pedido con ID " + dto.getPedidoId() + " ya tiene comprobante");
        }

     // 2. Consultas externas por red (Feign Clients) - FUERA de la transacción pesada
        if (dto.getClienteId() != null) {
            try {
                ClienteDTO clienteDTO = clienteRestConsumer.obtenerClientePorId(dto.getClienteId());
                if (clienteDTO == null) {
                    throw new RuntimeException("El cliente con ID " + dto.getClienteId() + " no fue encontrado en customer-service");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error al comunicarse con customer-service: " + e.getMessage());
            }
        }
        
        PedidoDTO pedidoDTO;
        try {
            pedidoDTO = orderRestConsumer.obtenerPedidoPorId(dto.getPedidoId());
            if (pedidoDTO == null) {
                throw new RuntimeException("Pedido no encontrado en order-service");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con order-service: " + e.getMessage());
        }

        // 3. Con los datos ya listos, llamamos a un método interno ultra-rápido que SÍ es transaccional
        return ejecutarGuardadoYLiberacionMesa(dto, pedidoDTO);
    	} catch (Exception e) {
    		// 🔥 IMPRIME EL ERROR COMPLETO EN ROJO EN LA CONSOLA DE ECLIPSE
            e.printStackTrace(); 
            throw new RuntimeException("Error interno al generar comprobante: " + e.getMessage(), e);
        }
    }
    
    /**
     * Método auxiliar privado puramente transaccional para la Base de Datos
     */
    @Transactional
    private ComprobanteDePago ejecutarGuardadoYLiberacionMesa(ComprobanteDTO dto, PedidoDTO pedidoDTO) {
        EstadoComprobante estado = estadoRepo.findByNombre("PAGADO")
                .orElseThrow(() -> new RuntimeException("Estado PAGADO no configurado"));

        ComprobanteDePago comp = new ComprobanteDePago();
        comp.setOrderId(dto.getPedidoId());
        comp.setCliente(dto.getClienteId());
        comp.setTipoComprobante(dto.getTipoComprobante());
        comp.setMetodoPago(dto.getMetodoPago());
        comp.setEstado(estado);
        comp.setFechaEmision(LocalDateTime.now());
        comp.setTotal(pedidoDTO.getTotal());
        
        // Generación de número usando nuestro bloqueo seguro (Bug 1)
        String numero = generarNumeroComprobante(dto.getTipoComprobante());
        comp.setNumero(numero);

        ComprobanteDePago comprobanteGuardado = comprobanteRepo.save(comp);

        // Liberar mesa en operations-service (llamada final rápida)
        // 🛡️ LIBERACIÓN SEGURA AISLADA (Evita bucles y bloqueos de red)
        try {
            if (pedidoDTO != null && pedidoDTO.getMesaId() != null) {
                // Hacemos la llamada directa sin reintentos infinitos
                mesaFeignClient.actualizarEstadoMesa(pedidoDTO.getMesaId(), "LIBRE");
            }
        }catch (Exception e) {
            // Si operations-service no responde, el pago YA SE GUARDÓ con éxito. 
            // Solo dejamos un log en consola para auditoría, pero no rompemos la transacción del cajero.
            System.err.println("⚠️ Advertencia: No se pudo liberar la mesa automáticamente: " + e.getMessage());
        }

     // 2. 🔥 ACTUALIZAR EL ESTADO DEL PEDIDO A PAGADO (ID 4) EN EL ORDER-SERVICE
        try {
            if (dto.getPedidoId() != null) {
                // Asegúrate de tener este método en tu OrderRestConsumer (o OrderFeignClient)
                orderRestConsumer.actualizarEstadoPedido(dto.getPedidoId(), 4L); 
                System.out.println("✅ Pedido " + dto.getPedidoId() + " actualizado a estado PAGADO (4) exitosamente.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Advertencia: No se pudo actualizar el estado del pedido a PAGADO: " + e.getMessage());
        }
        
        return comprobanteGuardado;
    }

    /**
     * 🚀 VERIFICACIÓN DE FEIGN CLIENT: Obtener comprobante enriquecido con cliente remoto
     */
    public ComprobanteResponseDTO obtenerComprobanteConCliente(Long idComprobante) {
        ComprobanteDePago comp = comprobanteRepo.findById(idComprobante)
                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado con ID: " + idComprobante));

        ComprobanteResponseDTO dto = convertir(comp);

        if (comp.getCliente() != null) {
            try {
                // Consulta sincrónica vía Feign Client a customer-service
                ClienteDTO clienteDTO = clienteRestConsumer.obtenerClientePorId(comp.getCliente());
                if (clienteDTO != null) {
                    dto.setClienteNombre(clienteDTO.getNombre());
                    dto.setClienteDocumento(clienteDTO.getDocumento());
                }
            } catch (Exception e) {
                dto.setClienteNombre("Error al consultar cliente en customer-service: " + e.getMessage());
            }
        } else {
            dto.setClienteNombre("CLIENTE VARIOS / SIN REGISTRO");
        }
        
     // 🍽️ B. Obtener platos/detalles del Pedido desde order-service
        // y enriquecer con datos actualizados desde catalog-service (Blindado contra nulos)
        if (comp.getOrderId() != null) {
            try {
                PedidoDTO pedidoDTO = orderRestConsumer.obtenerPedidoPorId(comp.getOrderId());
                if (pedidoDTO != null && pedidoDTO.getDetalles() != null) {
                    List<DetalleComprobanteDTO> detalles = pedidoDTO.getDetalles().stream()
                            .map(d -> {
                            	// 🔥 IMPRIME ESTO EN LA CONSOLA DE ECLIPSE
                                System.err.println("🔍 DEBUG DETALLE -> comidaId: " + d.getComidaId() + " | nombreTraido: " + d.getNombreProducto());
                                
                                // Valores por defecto seguros obtenidos del pedido original
                                String nombreProducto = "Producto";
                                BigDecimal precioUnitario = d.getPrecioUnitario() != null ? d.getPrecioUnitario() : BigDecimal.ZERO;
                                int cantidad = d.getCantidad() > 0 ? d.getCantidad() : 1;
                                
                             // 2. Intentar enriquecer opcionalmente con catalog-service (sin romper si falla)
                                try {
                                    if (d.getComidaId() != null) {
                                        ComidaDTO comidaDTO = catalogoFeignClient.obtenerComidaPorId(d.getComidaId());
                                        if (comidaDTO != null) {
                                            if (comidaDTO.getNombre() != null && !comidaDTO.getNombre().isBlank()) {
                                                nombreProducto = comidaDTO.getNombre();
                                            }
                                            if (comidaDTO.getPrecioUni() != null) {
                                                precioUnitario = comidaDTO.getPrecioUni();
                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                    // Si el catálogo falla, conservamos con seguridad los datos que ya traía el pedido
                                    System.err.println("Aviso: No se pudo consultar catalog-service, usando datos del pedido: " + ex.getMessage());
                                }
                                
                                return new DetalleComprobanteDTO(
                                        nombreProducto,
                                        cantidad,
                                        precioUnitario
                                );
                            }).toList();
                    dto.setDetalles(detalles);
                }
            } catch (Exception e) {
                System.err.println("Error al obtener detalles del pedido para el comprobante: " + e.getMessage());
            }
        }
        return dto;
    }

    /**
     * 🔢 Generador de número SUNAT
     */
    @Transactional
    public String generarNumeroComprobante(TipoComprobante tipo) {
        String prefijo = (tipo == TipoComprobante.BOLETA) ? "B001" : "F001";

        // 🔥 Aquí usamos el método con bloqueo pesimista para evitar colisiones entre cajeros
        ComprobanteDePago ultimo = comprobanteRepo.findUltimoPorTipoConBloqueo(tipo);

        int numero = 1;
        if (ultimo != null && ultimo.getNumero() != null) {
            try {
                String[] partes = ultimo.getNumero().split("-");
                numero = Integer.parseInt(partes[1]) + 1;
            } catch (Exception e) {
                numero = 1;
            }
        }

        return prefijo + "-" + String.format("%06d", numero);
    }

    public List<ComprobanteDePago> listar() {
        return comprobanteRepo.findAll();
    }

    public ComprobanteDePago buscarPorNumero(String numero) {
        return comprobanteRepo.findByNumero(numero)
            .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));
    }

    public List<ComprobanteDePago> buscarPorCliente(Long idCliente) {
        return comprobanteRepo.findByCliente(idCliente);
    }

    public List<ComprobanteDePago> buscarPorMetodo(MetodoPago metodo) {
        return comprobanteRepo.findByMetodoPago(metodo);
    }

    @Transactional
    public ComprobanteDePago anular(Long id) {
        ComprobanteDePago comp = comprobanteRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Comprobante no existe"));

        if ("ANULADO".equalsIgnoreCase(comp.getEstado().getNombre())) {
            throw new RuntimeException("El comprobante ya está anulado");
        }

        EstadoComprobante estado = estadoRepo.findByNombre("ANULADO")
            .orElseThrow(() -> new RuntimeException("Estado ANULADO no existe"));

        comp.setEstado(estado);
        comp.setFechaAnulacion(LocalDateTime.now());

        // 📩 Publicar evento de comprobante anulado a RabbitMQ
        try {
            ComprobanteAnuladoEvento evento = new ComprobanteAnuladoEvento();
            evento.setIdComprobante(comp.getIdComprobantePago());
            evento.setOrderId(comp.getOrderId());
            evento.setMotivo("Comprobante anulado por usuario");
            evento.setFechaAnulacion(comp.getFechaAnulacion());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_COMPROBANTE_ANULADO, evento);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo publicar evento comprobante.anulado: " + e.getMessage());
        }

        return comprobanteRepo.save(comp);
    }

    public CierreCajaDTO cierrePorFecha(LocalDate fecha) {
        List<ComprobanteDePago> lista = comprobanteRepo.findByFecha(fecha);

        CierreCajaDTO dto = new CierreCajaDTO();
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal efectivo = BigDecimal.ZERO;
        BigDecimal tarjeta = BigDecimal.ZERO;
        BigDecimal yape = BigDecimal.ZERO;

        for (ComprobanteDePago c : lista) {
            BigDecimal monto = c.getTotal() != null ? c.getTotal() : BigDecimal.ZERO;
            total = total.add(monto);

            if (c.getMetodoPago() != null) {
                switch (c.getMetodoPago()) {
                    case EFECTIVO -> efectivo = efectivo.add(monto);
                    case TARJETA -> tarjeta = tarjeta.add(monto);
                    case YAPE -> yape = yape.add(monto);
                }
            }
        }

        dto.setTotalGeneral(total);
        dto.setTotalEfectivo(efectivo);
        dto.setTotalTarjeta(tarjeta);
        dto.setTotalYape(yape);
        dto.setCantidadComprobantes(lista.size());

        return dto;
    }

    public ComprobanteResponseDTO convertir(ComprobanteDePago c) {
        ComprobanteResponseDTO dto = new ComprobanteResponseDTO();
        dto.setNumero(c.getNumero());
        dto.setFechaEmision(c.getFechaEmision());
        dto.setTotal(c.getTotal());
        dto.setMetodoPago(c.getMetodoPago() != null ? c.getMetodoPago().name() : null);
        dto.setTipoComprobante(c.getTipoComprobante() != null ? c.getTipoComprobante().name() : null);

     // 🔥 Agrega esta asignación para que el estado viaje en el JSON
        dto.setEstado(c.getEstado());

        // 👤 Consultar y asignar el nombre del cliente mediante Feign Client
        if (c.getCliente() != null) {
            try {
                ClienteDTO clienteDTO = clienteRestConsumer.obtenerClientePorId(c.getCliente());
                if (clienteDTO != null) {
                    dto.setClienteNombre(clienteDTO.getNombre());
                    dto.setClienteDocumento(clienteDTO.getDocumento());
                } else {
                    dto.setClienteNombre("Sin cliente");
                }
            } catch (Exception e) {
                dto.setClienteNombre("Sin cliente");
            }
        } else {
            dto.setClienteNombre("Sin cliente");
        }

        return dto;
    }
}