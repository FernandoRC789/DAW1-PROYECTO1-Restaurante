package com.cibertec.billing_service.config;

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
 * Configuración de RabbitMQ para billing-service.
 *
 * Publica eventos de comprobantes de pago en el exchange
 * 'restaurante.topic' para que otros microservicios se enteren de forma
 * asíncurna cuando se genera o anula un comprobante.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "restaurante.topic";

    public static final String ROUTING_KEY_COMPROBANTE_EMITIDO = "comprobante.emitido";
    public static final String ROUTING_KEY_COMPROBANTE_ANULADO = "comprobante.anulado";

    public static final String QUEUE_COMPROBANTE_EMITIDO = "comprobante.emitido.cola";
    public static final String QUEUE_COMPROBANTE_ANULADO = "comprobante.anulado.cola";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue comprobanteEmitidoQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable(QUEUE_COMPROBANTE_EMITIDO).build();
    }

    @Bean
    public Queue comprobanteAnuladoQueue() {
        return org.springframework.amqp.core.QueueBuilder.durable(QUEUE_COMPROBANTE_ANULADO).build();
    }

    @Bean
    public Binding comprobanteEmitidoBinding(Queue comprobanteEmitidoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(comprobanteEmitidoQueue).to(exchange).with(ROUTING_KEY_COMPROBANTE_EMITIDO);
    }

    @Bean
    public Binding comprobanteAnuladoBinding(Queue comprobanteAnuladoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(comprobanteAnuladoQueue).to(exchange).with(ROUTING_KEY_COMPROBANTE_ANULADO);
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
