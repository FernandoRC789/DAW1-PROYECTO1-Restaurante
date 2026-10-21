package com.cibertec.auth_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para auth-service.
 *
 * Publica eventos de auditoría de usuarios en el exchange
 * 'restaurante.topic' para que otros servicios se enteren de forma
 * asíncrona cuando se crea, actualiza o elimina un usuario.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "restaurante.topic";

    public static final String ROUTING_KEY_USUARIO_CREADO = "usuario.creado";
    public static final String ROUTING_KEY_USUARIO_ACTUALIZADO = "usuario.actualizado";
    public static final String ROUTING_KEY_USUARIO_ELIMINADO = "usuario.eliminado";

    public static final String QUEUE_USUARIO_CREADO = "usuario.creado.cola";
    public static final String QUEUE_USUARIO_ACTUALIZADO = "usuario.actualizado.cola";
    public static final String QUEUE_USUARIO_ELIMINADO = "usuario.eliminado.cola";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue usuarioCreadoQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable(QUEUE_USUARIO_CREADO).build();
    }

    @Bean
    public Queue usuarioActualizadoQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable(QUEUE_USUARIO_ACTUALIZADO).build();
    }

    @Bean
    public Queue usuarioEliminadoQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable(QUEUE_USUARIO_ELIMINADO).build();
    }

    @Bean
    public Binding usuarioCreadoBinding(Queue usuarioCreadoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(usuarioCreadoQueue).to(exchange).with(ROUTING_KEY_USUARIO_CREADO);
    }

    @Bean
    public Binding usuarioActualizadoBinding(Queue usuarioActualizadoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(usuarioActualizadoQueue).to(exchange).with(ROUTING_KEY_USUARIO_ACTUALIZADO);
    }

    @Bean
    public Binding usuarioEliminadoBinding(Queue usuarioEliminadoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(usuarioEliminadoQueue).to(exchange).with(ROUTING_KEY_USUARIO_ELIMINADO);
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
}
