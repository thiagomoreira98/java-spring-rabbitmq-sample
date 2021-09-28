package com.sample.amqp.consumer;

import com.sample.amqp.converter.MessageConverter;
import com.sample.amqp.producer.QueueProducer;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

    @Autowired
    private MessageConverter converter;

    @Autowired
    private QueueProducer producer;

    @Value("${spring.rabbitmq.queue.max-retries}")
    private Integer maxRetries;

    static final String HEADER_X_RETRIES_COUNT = "x-retries-count";

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public void listen(Message message) {
        try {
            this.converter.map(message);
        } catch (Exception ex) {
            throw new AmqpRejectAndDontRequeueException(ex.getMessage());
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.dead-letter.name}")
    public void listenDeadLetter(Message message) {
        try {
            this.converter.map(message);
        } catch (Exception ex) {
            var retries = (Integer) message.getMessageProperties().getHeaders().get(HEADER_X_RETRIES_COUNT);

            if (retries == null) {
                retries = 1;
            }

            if(this.maxRetries.equals(retries)) {
                this.producer.sendToParkingLot(message);
                throw new AmqpRejectAndDontRequeueException("error - 3 retries --> " + ex.getMessage());
            }

            message.getMessageProperties().setHeader(HEADER_X_RETRIES_COUNT, ++retries);
            this.producer.sendToDeadLetter(message);
        }
    }
}
