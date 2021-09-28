package com.sample.amqp.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitQueueConfig {

    @Value("${spring.rabbitmq.queue.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.queue.name}")
    private String queueName;

    @Value("${spring.rabbitmq.queue.routing-key}")
    private String routingKey;

    @Value("${spring.rabbitmq.queue.dead-letter.name}")
    private String deadLetter;

    @Value("${spring.rabbitmq.queue.dead-letter.routing-key}")
    private String deadLetterRoutingKey;

    @Value("${spring.rabbitmq.queue.parking-lot.name}")
    private String parkingLot;

    @Value("${spring.rabbitmq.queue.parking-lot.routing-key}")
    private String parkingLotRoutingKey;

    private static final String QUEUE_TYPE = "x-queue-type";
    private static final String QUEUE_RESULT_TYPE = "classic";

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(this.exchange);
    }

    @Bean
    Queue queue() {
        return QueueBuilder
            .durable(this.queueName)
            .withArgument(QUEUE_TYPE, QUEUE_RESULT_TYPE)
            .deadLetterExchange(this.exchange)
            .deadLetterRoutingKey(this.deadLetterRoutingKey)
            .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder
            .durable(this.deadLetter)
            .withArgument(QUEUE_TYPE, QUEUE_RESULT_TYPE)
            .build();
    }

    @Bean
    Queue parkingLotQueue() {
        return QueueBuilder
                .durable(this.parkingLot)
                .withArgument(QUEUE_TYPE, QUEUE_RESULT_TYPE)
                .build();
    }

    @Bean
    public Binding bindingQueue() {
        return BindingBuilder
            .bind(this.queue())
            .to(this.directExchange())
            .with(this.routingKey);
    }

    @Bean
    public Binding bindingDeadLetter() {
        return BindingBuilder
            .bind(this.deadLetterQueue())
            .to(this.directExchange())
            .with(this.deadLetterRoutingKey);
    }

    @Bean
    public Binding bindingParkingLot() {
        return BindingBuilder
            .bind(this.parkingLotQueue())
            .to(this.directExchange())
            .with(this.parkingLotRoutingKey);
    }
}
