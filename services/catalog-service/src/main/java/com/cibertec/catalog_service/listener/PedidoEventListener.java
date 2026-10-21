package com.cibertec.catalog_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cibertec.catalog_service.config.RabbitMQConfig;
import com.cibertec.catalog_service.event.PedidoCreadoEvento;
import com.cibertec.catalog_service.model.PedidoEstadistica;
import com.cibertec.catalog_service.Repository.PedidoEstadisticaRepository;

/**
 * Listener de eventos de pedidos para catalog-service.
 *
 * Consume 'pedido.creado' y mantiene una proyección local de cada pedido
 * para estadísticas de ventas sin llamar a order-service por HTTP.
 */
@Component
public class PedidoEventListener {

    @Autowired
    private PedidoEstadisticaRepository estadisticaRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PEDIDO_CREADO)
    public void onPedidoCreado(PedidoCreadoEvento evento) {
        if (evento != null && evento.getIdPedido() != null) {
            try {
                PedidoEstadistica estadistica = new PedidoEstadistica(
                        evento.getIdPedido(),
                        evento.getMesaId(),
                        evento.getMeseroId(),
                        evento.getClienteId(),
                        evento.getTotal(),
                        evento.getEstado(),
                        evento.getFechaEvento()
                );
                estadisticaRepository.save(estadistica);
                System.out.println("📊 catalog-service: estadística de pedido guardada -> idPedido=" + evento.getIdPedido());
            } catch (Exception e) {
                System.err.println("⚠️ Error al guardar estadística de pedido en catalog-service: " + e.getMessage());
            }
        }
    }
}
