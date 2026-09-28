package com.cibertec.order_service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.order_service.model.DetallePedido;
import com.cibertec.order_service.model.Pedido;
import com.cibertec.order_service.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * 🔹 Guarda un pedido directo (no recomendado si usas DTO)
     */
    public Pedido guardarPedido(Pedido pedido) {

        if (pedido.getDetallePedido() == null ||
            pedido.getDetallePedido().isEmpty()) {

            throw new RuntimeException(
                "El pedido debe tener al menos un detalle"
            );
        }

        return pedidoRepository.save(pedido);
    }

    /**
     * 🔹 Lista todos los pedidos
     */
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    /**
     * 🔹 Obtiene un pedido por ID
     */
    public Pedido obtenerPorId(Long id) {

        return pedidoRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Pedido no encontrado"));
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
    public List<Pedido> buscarPorEstado(Long estadoId) {

        return pedidoRepository.findByEstadoid(estadoId);
    }

    /**
     * 🔹 Busca pedidos por mesa
     */
    public List<Pedido> buscarPorMesa(Long mesaId) {

        return pedidoRepository.findByMesaid(mesaId);
    }

    /**
     * 🔹 Filtro avanzado (fecha + mesa)
     */
    public List<Pedido> buscarPedidos(
            LocalDate fecha,
            String mesa) {

    	Long mesaFil = (mesa == null || mesa.trim().isEmpty())
    	        ? null
    	        : Long.valueOf(mesa.trim());

    	return pedidoRepository.buscarPedidosAdmin(
    	        fecha,
    	        mesaFil
    	);
    }

    /**
     * 🔥 MÉTODO PRINCIPAL (CREACIÓN DESDE FRONT)
     *
     * Convierte un PedidoDTO en entidad Pedido
     * y lo guarda en la base de datos.
     */
    public Pedido guardarDesdeDTO(Pedido dto) {

        Pedido pedido = new Pedido();

        /*
         * En este microservicio Pedido ahora guarda
         * directamente los IDs.
         */

        pedido.setEstadoid(dto.getEstadoid());
        pedido.setMesaid(dto.getMesaid());
        pedido.setMeseroid(dto.getMeseroid());
        pedido.setObservaciones(dto.getObservaciones());

        /*
         * 🔥 DETALLE DEL PEDIDO
         */
        List<DetallePedido> detalles = new ArrayList<>();

        for (DetallePedido detDTO : dto.getDetallePedido()) {

            DetallePedido det = new DetallePedido();

            det.setPedido(pedido);

            // Ahora comida es Long
            det.setComida(detDTO.getComida());

            det.setCantidad(detDTO.getCantidad());
            det.setPrecioUni(detDTO.getPrecioUni());
            /*
             * El precio deberá recibirse del DTO
             * o consultarse al microservicio de comidas.
             *
             * Por ahora se conserva la estructura.
             */

            detalles.add(det);
        }

        pedido.setDetallePedido(detalles);

        // Fecha del pedido
        pedido.setFechaRegistrada(LocalDate.now());

        return pedidoRepository.save(pedido);
    }

    /**
     * 🔥 Cambia el estado de un pedido
     */
    public Pedido cambiarEstado(
            Long idPedido,
            Long estadoId) {

        Pedido pedido = pedidoRepository
            .findById(idPedido)
            .orElseThrow(() ->
                new RuntimeException("Pedido no encontrado"));

        pedido.setEstadoid(estadoId);

        return pedidoRepository.save(pedido);
    }

    /**
     * Obtener los pedidos del mesero
     */
    public List<Pedido> obtenerPedidosDelMesero() {

        /*
         * Como ahora meseroid es Long,
         * este método necesita recibir/obtener el ID
         * del mesero desde autenticación.
         *
         * Por ahora devuelve todos los pedidos
         * hasta conectar Security/JWT.
         */

        return pedidoRepository.findAll();
    }

    /**
     * Actualizar pedido por ID
     */
    public Pedido actualizarPedido(
            Long id,
            Pedido dto) {

        Pedido pedido = pedidoRepository
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("Pedido no existe"));

        /*
         * Actualizar estado
         */
        if (dto.getEstadoid() != null) {
            pedido.setEstadoid(dto.getEstadoid());
        }

        /*
         * Actualizar mesa
         */
        pedido.setMesaid(dto.getMesaid());
        pedido.setMeseroid(dto.getMeseroid());

        /*
         * Observaciones
         */
        pedido.setObservaciones(
            dto.getObservaciones()
        );

        /*
         * Rehacer detalle completo
         */
        List<DetallePedido> detalles =
            new ArrayList<>();

        for (DetallePedido detDTO :
            dto.getDetallePedido()) {

            DetallePedido det =
                new DetallePedido();

            det.setPedido(pedido);

            det.setComida(
            	    detDTO.getComida()
            	);

            	det.setCantidad(
            	    detDTO.getCantidad()
            	);

            	det.setPrecioUni(
            	    detDTO.getPrecioUni()
            	);

            	detalles.add(det);
        }

        pedido.setDetallePedido(detalles);

        return pedidoRepository.save(pedido);
    }

    /**
     * Listar pedidos por estado
     */
    public List<Pedido> listarPorEstado(
            String estado) {

        try {

            Long estadoId =
                Long.valueOf(estado);

            return pedidoRepository
                .findByEstadoid(estadoId);

        } catch (NumberFormatException e) {

            throw new RuntimeException(
                "El estado debe ser un ID numérico"
            );
        }
    }
}