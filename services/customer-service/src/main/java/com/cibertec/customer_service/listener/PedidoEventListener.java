package com.cibertec.customer_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cibertec.customer_service.config.RabbitMQConfig;
import com.cibertec.customer_service.event.PedidoCreadoEvento;
import com.cibertec.customer_service.model.PedidoCliente;
import com.cibertec.customer_service.repository.PedidoClienteRepository;

/**
 * Listener de eventos de pedidos para customer-service.
 *
 * Consume 'pedido.creado' y mantiene un historial local de pedidos por
 * cliente sin llamar a order-service por HTTP.
 */
@Component
public class PedidoEventListener {

    @Autowired
    private PedidoClienteRepository pedidoClienteRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PEDIDO_CREADO)
    public void onPedidoCreado(PedidoCreadoEvento evento) {
        if (evento != null && evento.getIdPedido() != null && evento.getClienteId() != null) {
            try {
                PedidoCliente pedidoCliente = new PedidoCliente(
                        evento.getIdPedido(),
                        evento.getClienteId(),
                        evento.getMesaId(),
                        evento.getMeseroId(),
                        evento.getTotal(),
                        evento.getEstado(),
                        evento.getFechaEvento()
                );
                pedidoClienteRepository.save(pedidoCliente);
                System.out.println("👥 customer-service: historial de pedido guardado -> idPedido=" + evento.getIdPedido()
                        + " clienteId=" + evento.getClienteId());
            } catch (Exception e) {
                System.err.println("⚠️ Error al guardar historial de pedido en customer-service: " + e.getMessage());
            }
        }
    }
}
