package com.sample.amqp.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.model.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageConverter {

    @Autowired
    private ObjectMapper mapper;

    public Payload map(Message message) throws Exception {
        var messageStr = new String(message.getBody());
        log.info(String.format("RabbitMQ message received on queue: %s", messageStr));
        return this.mapper.readValue(messageStr, Payload.class);
    }
}
