package com.cibertec.customer_service.config;

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
 * Configuración de RabbitMQ para customer-service.
 *
 * Consume el evento 'pedido.creado' publicado por order-service para mantener
 * un historial local de pedidos por cliente sin depender de llamadas HTTP
 * síncronas a order-service.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "restaurante.topic";

    public static final String ROUTING_KEY_PEDIDO_CREADO = "pedido.creado";

    public static final String QUEUE_PEDIDO_CREADO = "pedido.creado.cola";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue pedidoCreadoQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable(QUEUE_PEDIDO_CREADO).build();
    }

    @Bean
    public Binding pedidoCreadoBinding(Queue pedidoCreadoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(pedidoCreadoQueue).to(exchange).with(ROUTING_KEY_PEDIDO_CREADO);
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
