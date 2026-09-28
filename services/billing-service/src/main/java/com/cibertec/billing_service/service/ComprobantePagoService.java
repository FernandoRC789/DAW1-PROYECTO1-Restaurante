package com.cibertec.billing_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.billing_service.client.ClienteRestConsumer;
import com.cibertec.billing_service.dto.CierreCajaDTO;
import com.cibertec.billing_service.dto.ClienteDTO;
import com.cibertec.billing_service.dto.ComprobanteDTO;
import com.cibertec.billing_service.dto.ComprobanteResponseDTO;
import com.cibertec.billing_service.model.ComprobanteDePago;
import com.cibertec.billing_service.model.EstadoComprobante;
import com.cibertec.billing_service.repository.ComprobanteDePagoRepository;
import com.cibertec.billing_service.repository.EstadoComprobanteRepository;
import com.cibertec.billing_service.repository.EstadoRepository;
import com.cibertec.billing_service.utilsEnum.MetodoPago;
import com.cibertec.billing_service.utilsEnum.TipoComprobante;

import jakarta.transaction.Transactional;

/**
 * 📌 SERVICIO: ComprobantePagoService
 * 
 * 🔥 RESPONSABILIDAD:
 * Manejar la lógica de negocio de comprobantes
 * 
 * 🔥 FUNCIONES PRINCIPALES:
 * - Generar número tipo SUNAT
 * - Validaciones de negocio (más adelante)
 */
@Service
public class ComprobantePagoService {

    @Autowired
    private ComprobanteDePagoRepository comprobanteRepo;

    
    @Autowired
    private EstadoComprobanteRepository estadoRepo;

    
    @Autowired
    private ClienteRestConsumer clienteRestConsumer; // 👈 Inyectamos nuestro Feign Client
    

    @Autowired
    private EstadoRepository estadoPedidoRepo;
    
    /**
     * 🔥 PROCESO PRINCIPAL: GENERAR COMPROBANTE
     * 
     * Flujo real:
     * 1. Validar pedido
     * 2. Validar que no tenga comprobante
     * 3. Validar cliente según tipo
     * 4. Generar número SUNAT
     * 5. Asignar cajero
     * 6. Guardar comprobante
     * 7. Cambiar estado del pedido a PAGADO
     * 8. Liberar mesa
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

    	// 1. VALIDAR QUE EL PEDIDO NO TENGA COMPROBANTE
        if (comprobanteRepo.existsByOrderId(orderId)) {
            throw new RuntimeException("El pedido con ID " + orderId + " ya tiene un comprobante registrado.");
        }

     

        // 🔥 2. GENERAR NUMERO CORRELATIVO DE LA SUNAT
        String numero = generarNumeroComprobante(tipo);

        

        

        // 🔥 3. ESTADO POR DEFECTO (PAGADO)
        EstadoComprobante estado = estadoRepo.findByNombre("PAGADO")
                .orElseThrow(() -> new RuntimeException("Estado no configurado"));

        // 🔥 4. CREAR COMPROBANTE O REGISTRO
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

        // 💾 GUARDAR
        //ComprobanteDePago guardado = comprobanteRepo.save(comp);
        return comprobanteRepo.save(comp);

        // 🔥5. CAMBIAR ESTADO DEL PEDIDO
    }
    
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
        

        EstadoComprobante estado = estadoRepo.findByNombre("PAGADO")
                .orElseThrow(() -> new RuntimeException("Estado PAGADO no configurado"));

        ComprobanteDePago comp = new ComprobanteDePago();
        comp.setOrderId(dto.getPedidoId());
        comp.setCliente(dto.getClienteId());
        comp.setTipoComprobante(dto.getTipoComprobante());
        comp.setMetodoPago(dto.getMetodoPago());
        comp.setEstado(estado);
        comp.setFechaEmision(LocalDateTime.now());
        
        // Asignación limpia sin depender de Spring Security
        comp.setUserID(dto.getClienteId());
        //comp.setTotal(dto.getTotal());

        String numero = generarNumeroComprobante(dto.getTipoComprobante());
        comp.setNumero(numero);

        return comprobanteRepo.save(comp);
    }
    /**
     * 🔢 Generador de número SUNAT
     */
    public String generarNumeroComprobante(TipoComprobante tipo) {

        String prefijo = (tipo == TipoComprobante.BOLETA) ? "B001" : "F001";

        ComprobanteDePago ultimo =
                comprobanteRepo.findTopByTipoComprobanteOrderByIdComprobantePagoDesc(tipo);

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
    
    /**
     * 📌 Listar todos los comprobantes
     */
    public List<ComprobanteDePago> listar() {
        return comprobanteRepo.findAll();
    }
    
    /**
     * 📌 Buscar comprobante por número
     */
    public ComprobanteDePago buscarPorNumero(String numero) {
        return comprobanteRepo.findByNumero(numero)
            .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));
    }
    
    /**
     * BUSCAR POR CLIENTE ID
     */
    public List<ComprobanteDePago> buscarPorCliente(Long idCliente) {
        return comprobanteRepo.findByCliente(idCliente);
    }
    
    //buscar por metodo de pago
    public List<ComprobanteDePago> buscarPorMetodo(MetodoPago metodo) {
        return comprobanteRepo.findByMetodoPago(metodo);
    }
    
    public ComprobanteDePago anular(Long id) {

        ComprobanteDePago comp = comprobanteRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Comprobante no existe"));

        // ❌ evitar doble anulación
        if ("ANULADO".equalsIgnoreCase(comp.getEstado().getNombre())) {
            throw new RuntimeException("El comprobante ya está anulado");
        }

        EstadoComprobante estado = estadoRepo
            .findByNombre("ANULADO")
            .orElseThrow(() -> new RuntimeException("Estado ANULADO no existe"));

        comp.setEstado(estado);

        // 🧠 trazabilidad
        comp.setFechaAnulacion(LocalDateTime.now());

        return comprobanteRepo.save(comp);
    }
    public CierreCajaDTO cierrePorFecha(LocalDate fecha) {

        List<ComprobanteDePago> lista =
                comprobanteRepo.findByFecha(fecha);

        CierreCajaDTO dto = new CierreCajaDTO();

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal efectivo = BigDecimal.ZERO;
        BigDecimal tarjeta = BigDecimal.ZERO;
        BigDecimal yape = BigDecimal.ZERO;

        for (ComprobanteDePago c : lista) {

            BigDecimal monto = c.getTotal();
            total = total.add(monto);

            switch (c.getMetodoPago()) {

                case EFECTIVO -> efectivo = efectivo.add(monto);
                case TARJETA -> tarjeta = tarjeta.add(monto);
                case YAPE -> yape = yape.add(monto);
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
        dto.setMetodoPago(c.getMetodoPago().name());
        dto.setTipoComprobante(c.getTipoComprobante().name());

        return dto;
        
        
    }
    
    
    
}