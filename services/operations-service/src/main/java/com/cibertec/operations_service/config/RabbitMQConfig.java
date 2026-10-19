package com.cibertec.operations_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para operations-service.
 *
 * Consume los eventos publicados por order-service en el exchange
 * 'restaurante.topic' para actualizar el estado de las mesas de forma
 * asíncrona y desacoplada.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "restaurante.topic";

    public static final String ROUTING_KEY_PEDIDO_CREADO = "pedido.creado";
    public static final String ROUTING_KEY_ESTADO_CAMBIADO = "pedido.estado.cambiado";

    public static final String QUEUE_PEDIDO_CREADO = "pedido.creado.cola";
    public static final String QUEUE_ESTADO_CAMBIADO = "pedido.estado.cambiado.cola";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue pedidoCreadoQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable(QUEUE_PEDIDO_CREADO).build();
    }

    @Bean
    public Queue pedidoEstadoCambiadoQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable(QUEUE_ESTADO_CAMBIADO).build();
    }

    @Bean
    public Binding pedidoCreadoBinding(Queue pedidoCreadoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(pedidoCreadoQueue).to(exchange).with(ROUTING_KEY_PEDIDO_CREADO);
    }

    @Bean
    public Binding pedidoEstadoCambiadoBinding(Queue pedidoEstadoCambiadoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(pedidoEstadoCambiadoQueue).to(exchange).with(ROUTING_KEY_ESTADO_CAMBIADO);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
