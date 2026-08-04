package com.cibertec.SistemaWebRestaurante.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cibertec.SistemaWebRestaurante.dto.CierreCajaDTO;
import com.cibertec.SistemaWebRestaurante.dto.ComprobanteDTO;
import com.cibertec.SistemaWebRestaurante.dto.ComprobanteResponseDTO;
import com.cibertec.SistemaWebRestaurante.dto.DetalleComprobanteDTO;
import com.cibertec.SistemaWebRestaurante.model.Cliente;
import com.cibertec.SistemaWebRestaurante.model.ComprobanteDePago;
import com.cibertec.SistemaWebRestaurante.model.Estado;
import com.cibertec.SistemaWebRestaurante.model.EstadoComprobante;
import com.cibertec.SistemaWebRestaurante.model.Mesa;
import com.cibertec.SistemaWebRestaurante.model.Pedido;
import com.cibertec.SistemaWebRestaurante.model.Usuario;
import com.cibertec.SistemaWebRestaurante.repository.ClienteRepository;
import com.cibertec.SistemaWebRestaurante.repository.ComprobantePagoRepository;
import com.cibertec.SistemaWebRestaurante.repository.EstadoComprobanteRepository;
import com.cibertec.SistemaWebRestaurante.repository.EstadoRepository;
import com.cibertec.SistemaWebRestaurante.repository.MesaRepository;
import com.cibertec.SistemaWebRestaurante.repository.PedidoRepository;
import com.cibertec.SistemaWebRestaurante.repository.UsuarioRepository;
import com.cibertec.SistemaWebRestaurante.utilsEnum.EstadoMesa;
import com.cibertec.SistemaWebRestaurante.utilsEnum.MetodoPago;
import com.cibertec.SistemaWebRestaurante.utilsEnum.TipoComprobante;

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
    private ComprobantePagoRepository comprobanteRepo;

    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private EstadoComprobanteRepository estadoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private MesaRepository mesaRepo;

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
    public ComprobanteDePago crearComprobante(
            Long pedidoId,
            Long clienteId,
            TipoComprobante tipo,
            MetodoPago metodoPago
    ) {

        // 🔍 1. VALIDAR PEDIDO
        Pedido pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no existe"));

        // ❌ 2. VALIDAR QUE NO EXISTA COMPROBANTE
        if (comprobanteRepo.existsByPedido(pedido)) {
            throw new RuntimeException("El pedido ya tiene comprobante");
        }

        // 🔍 3. VALIDAR CLIENTE
        Cliente cliente = null;

        if (tipo == TipoComprobante.FACTURA) {
            if (clienteId == null) {
                throw new RuntimeException("Factura requiere cliente");
            }

            cliente = clienteRepo.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no existe"));

            // 🔥 Validar RUC
            if (!cliente.getTipoDocumento().name().equals("RUC")) {
                throw new RuntimeException("Factura solo permite clientes con RUC");
            }
        } else {
            // BOLETA → cliente opcional
            if (clienteId != null) {
                cliente = clienteRepo.findById(clienteId)
                        .orElseThrow(() -> new RuntimeException("Cliente no existe"));
            }
        }

        // 🔥 4. GENERAR NUMERO
        String numero = generarNumeroComprobante(tipo);

        // 🔥 5. OBTENER CAJERO LOGUEADO
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario cajero = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 🔥 6. ESTADO POR DEFECTO (PAGADO)
        EstadoComprobante estado = estadoRepo.findByNombre("PAGADO")
                .orElseThrow(() -> new RuntimeException("Estado no configurado"));

        // 🔥 7. CREAR COMPROBANTE
        ComprobanteDePago comp = new ComprobanteDePago();

        comp.setPedido(pedido);
        comp.setCliente(cliente);
        comp.setFechaEmision(LocalDateTime.now());
        comp.setTotal(pedido.getTotal());
        comp.setTipoComprobante(tipo);
        comp.setNumero(numero);
        comp.setMetodoPago(metodoPago);
        comp.setEstado(estado);
        comp.setCajero(cajero);

        // 💾 GUARDAR
        ComprobanteDePago guardado = comprobanteRepo.save(comp);

        // 🔥 8. CAMBIAR ESTADO DEL PEDIDO
        Estado estadoPedido = estadoPedidoRepo
        	    .findByNombre("PAGADO")
        	    .orElseThrow(() -> new RuntimeException("Estado PAGADO no existe"));

        	pedido.setEstado(estadoPedido);
        	pedidoRepo.save(pedido);

        // 🔥 9. LIBERAR MESA
        Mesa mesa = pedido.getMesa();
        mesa.setEstado_mesa(EstadoMesa.LIBRE);
        mesaRepo.save(mesa);

        return guardado;
    }
    
    @Transactional
    public ComprobanteDePago crearDesdeDTO(ComprobanteDTO dto) {

        Pedido pedido = pedidoRepo.findById(dto.getPedidoId())
            .orElseThrow(() -> new RuntimeException("Pedido no existe"));

        if (comprobanteRepo.existsByPedido(pedido)) {
            throw new RuntimeException("El pedido ya tiene comprobante");
        }

        Cliente cliente = null;
        if (dto.getClienteId() != null) {
            cliente = clienteRepo.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no existe"));
        }

        EstadoComprobante estado = estadoRepo.findByNombre("PAGADO")
            .orElseThrow(() -> new RuntimeException("Estado PAGADO no configurado"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario cajero = usuarioRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // =========================
        // 🧾 CREAR COMPROBANTE
        // =========================
        ComprobanteDePago comp = new ComprobanteDePago();

        comp.setPedido(pedido);
        comp.setCliente(cliente);
        comp.setTipoComprobante(dto.getTipoComprobante());
        comp.setMetodoPago(dto.getMetodoPago());
        comp.setEstado(estado);
        comp.setFechaEmision(LocalDateTime.now());
        comp.setCajero(cajero); // 🔥 TE FALTABA ESTO

        String numero = generarNumeroComprobante(dto.getTipoComprobante());
        comp.setNumero(numero);

        comp.setTotal(pedido.getTotal());

        ComprobanteDePago guardado = comprobanteRepo.save(comp);

        // =========================
        // 🔥 AHORA RECIÉN ACTUALIZAS
        // =========================

        // ✅ pedido → PAGADO
        Estado estadoPedido = new Estado();
        estadoPedido.setIdEstado(4L); // asegúrate que sea correcto
        pedido.setEstado(estadoPedido);
        pedidoRepo.save(pedido);

        // ✅ mesa → LIBRE
        Mesa mesa = pedido.getMesa();
        mesa.setEstado_mesa(EstadoMesa.LIBRE);
        mesaRepo.save(mesa);

        return guardado;
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
    
    //buscar por cliente
    public List<ComprobanteDePago> buscarPorCliente(Long idCliente) {

        Cliente cliente = clienteRepo.findById(idCliente)
            .orElseThrow(() -> new RuntimeException("Cliente no existe"));

        return comprobanteRepo.findByCliente(cliente);
    }
    
    //buscar por metodo
    public List<ComprobanteDePago> buscarPorMetodo(MetodoPago metodo) {
        return comprobanteRepo.findByMetodoPago(metodo);
    }
    
    public ComprobanteDePago anular(Long id) {

        ComprobanteDePago comp = comprobanteRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Comprobante no existe"));

        // ❌ evitar doble anulación
        if (comp.getEstado().getNombre().equals("ANULADO")) {
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

        // 👤 cliente
        if (c.getCliente() != null) {
            dto.setClienteNombre(c.getCliente().getNombre());
            dto.setClienteDocumento(c.getCliente().getDocumento());
        }

        // 🍽️ DETALLES (LO IMPORTANTE)
        List<DetalleComprobanteDTO> detalles = c.getPedido().getDetallePedido()
            .stream()
            .map(d -> new DetalleComprobanteDTO(
                d.getComida().getNombre(),  // 👈 asegúrate que exista
                d.getCantidad(),
                d.getPrecioUni()
            ))
            .toList();

        dto.setDetalles(detalles);

        return dto;
    }
}