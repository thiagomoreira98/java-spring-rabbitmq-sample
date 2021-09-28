package com.sample.amqp.producer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueueProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queue.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.queue.dead-letter.routing-key}")
    private String deadLetterRoutingKey;

    @Value("${spring.rabbitmq.queue.parking-lot.routing-key}")
    private String parkingLotRoutingKey;

    public void sendToDeadLetter(Message message) {
        this.rabbitTemplate.send(this.exchange, this.deadLetterRoutingKey, message);
    }

    public void sendToParkingLot(Message message) {
        this.rabbitTemplate.send(this.exchange, this.parkingLotRoutingKey, message);
    }
}
