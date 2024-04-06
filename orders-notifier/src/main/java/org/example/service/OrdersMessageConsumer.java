package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.example.model.Order;

@ApplicationScoped
@Slf4j
public class OrdersMessageConsumer {

    @Inject
    MailSender mailSender;

    @Inject
    ObjectMapper objectMapper;

    @Incoming("orders-in")
    public void receiveMessageFromBrokerAndSendNotification(String message) throws JsonProcessingException {
        log.info("Message received: '{}'", message);
        var order = readOrderFromJson(message);
        sendNotification(order);
    }

    private Order readOrderFromJson(String message) throws JsonProcessingException {
        return objectMapper.readValue(message, Order.class);
    }

    private void sendNotification(Order order) {
        mailSender.notifyAboutOrderCreation(order);
    }
}
