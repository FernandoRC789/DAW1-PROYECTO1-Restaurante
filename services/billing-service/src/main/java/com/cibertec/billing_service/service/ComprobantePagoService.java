package com.cibertec.billing_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.billing_service.client.ClienteRestConsumer;
import com.cibertec.billing_service.client.OrderRestConsumer;
import com.cibertec.billing_service.dto.CierreCajaDTO;
import com.cibertec.billing_service.dto.ClienteDTO;
import com.cibertec.billing_service.dto.ComprobanteDTO;
import com.cibertec.billing_service.dto.ComprobanteResponseDTO;
import com.cibertec.billing_service.dto.DetalleComprobanteDTO;
import com.cibertec.billing_service.dto.external.PedidoDTO;
import com.cibertec.billing_service.model.ComprobanteDePago;
import com.cibertec.billing_service.model.EstadoComprobante;
import com.cibertec.billing_service.repository.ComprobanteDePagoRepository;
import com.cibertec.billing_service.repository.EstadoComprobanteRepository;
import com.cibertec.billing_service.utilsEnum.MetodoPago;
import com.cibertec.billing_service.utilsEnum.TipoComprobante;

import jakarta.transaction.Transactional;

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
    private ClienteRestConsumer clienteRestConsumer; // 👈 Inyección de Feign Client
    
    @Autowired
    private OrderRestConsumer orderRestConsumer;

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

        return comprobanteRepo.save(comp);
    }

    /**
     * CREAR DESDE DTO
     */
    @Transactional
    public ComprobanteDePago crearDesdeDTO(ComprobanteDTO dto) {

        if (comprobanteRepo.existsByOrderId(dto.getPedidoId())) {
            throw new RuntimeException("El pedido con ID " + dto.getPedidoId() + " ya tiene comprobante");
        }

        // 🔍 Validar cliente consultando por Feign Client si se envió un clienteId
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
        
     // 🔍 1. Validar pedido y obtener su TOTAL real desde order-service
        PedidoDTO pedidoDTO;
        try {
            pedidoDTO = orderRestConsumer.obtenerPedidoPorId(dto.getPedidoId());
            if (pedidoDTO == null) {
                throw new RuntimeException("Pedido no encontrado en order-service");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con order-service: " + e.getMessage());
        }
        

        EstadoComprobante estado = estadoRepo.findByNombre("PAGADO")
                .orElseThrow(() -> new RuntimeException("Estado PAGADO no configurado"));

        ComprobanteDePago comp = new ComprobanteDePago();
        comp.setOrderId(dto.getPedidoId());
        comp.setCliente(dto.getClienteId());
        comp.setTipoComprobante(dto.getTipoComprobante());
        comp.setMetodoPago(dto.getMetodoPago());
        comp.setEstado(estado);
        comp.setFechaEmision(LocalDateTime.now());
        //comp.setUserID(dto.getUserId() != null ? dto.getUserId() : 1L);
        //comp.setTotal(dto.getTotal());
     // 💰 Asignar el total que viene directamente del order-service
        comp.setTotal(pedidoDTO.getTotal());
        String numero = generarNumeroComprobante(dto.getTipoComprobante());
        comp.setNumero(numero);

        return comprobanteRepo.save(comp);
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
        if (comp.getOrderId() != null) {
            try {
                PedidoDTO pedidoDTO = orderRestConsumer.obtenerPedidoPorId(comp.getOrderId());
                if (pedidoDTO != null && pedidoDTO.getDetalles() != null) {
                    List<DetalleComprobanteDTO> detalles = pedidoDTO.getDetalles().stream()
                            .map(d -> new DetalleComprobanteDTO(
                                    d.getNombreProducto(),
                                    d.getCantidad(),
                                    d.getPrecioUnitario()
                            )).toList();
                    dto.setDetalles(detalles);
                }
            } catch (Exception e) {
                // Si falla la consulta de detalles
            }
        }
        

        return dto;
        
   
    }

    /**
     * 🔢 Generador de número SUNAT
     */
    public String generarNumeroComprobante(TipoComprobante tipo) {
        String prefijo = (tipo == TipoComprobante.BOLETA) ? "B001" : "F001";

        ComprobanteDePago ultimo = comprobanteRepo.findTopByTipoComprobanteOrderByIdComprobantePagoDesc(tipo);

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

        return dto;
    }
}