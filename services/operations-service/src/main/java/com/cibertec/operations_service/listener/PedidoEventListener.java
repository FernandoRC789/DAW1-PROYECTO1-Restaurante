package com.cibertec.operations_service.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cibertec.operations_service.config.RabbitMQConfig;
import com.cibertec.operations_service.event.PedidoCreadoEvento;
import com.cibertec.operations_service.event.PedidoEstadoCambiadoEvento;
import com.cibertec.operations_service.service.MesaService;
import com.cibertec.operations_service.utilsEnum.EstadoMesa;

/**
 * Listener de eventos de pedidos para operations-service.
 *
 * Escucha las colas de RabbitMQ y actualiza el estado de las mesas
 * sin depender de llamadas HTTP síncronas a order-service o billing-service.
 */
@Component
public class PedidoEventListener {

    @Autowired
    private MesaService mesaService;

    /**
     * Al recibir un pedido creado, marca la mesa como OCUPADA.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PEDIDO_CREADO)
    public void onPedidoCreado(PedidoCreadoEvento evento) {
        if (evento != null && evento.getMesaId() != null) {
            try {
                mesaService.cambiarEstado(evento.getMesaId(), EstadoMesa.OCUPADO);
            } catch (Exception e) {
                System.err.println("⚠️ Error al ocupar mesa desde evento pedido.creado: " + e.getMessage());
            }
        }
    }

    /**
     * Al recibir un cambio de estado, si el nuevo estado es PAGADO (id 4),
     * libera la mesa asociada.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_ESTADO_CAMBIADO)
    public void onEstadoCambiado(PedidoEstadoCambiadoEvento evento) {
        if (evento != null && evento.getMesaId() != null && evento.getEstadoIdNuevo() != null) {
            // Estado 4 = PAGADO (según lógica de order-service)
            if (evento.getEstadoIdNuevo() == 4L) {
                try {
                    mesaService.cambiarEstado(evento.getMesaId(), EstadoMesa.LIBRE);
                } catch (Exception e) {
                    System.err.println("⚠️ Error al liberar mesa desde evento pedido.estado.cambiado: " + e.getMessage());
                }
            }
        }
    }
}
