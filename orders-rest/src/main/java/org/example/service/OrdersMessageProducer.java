package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.example.model.Order;

@ApplicationScoped
@Slf4j
public class OrdersMessageProducer {

    @Inject
    @Channel("orders-out")
    Emitter<Order> orderEmitter;

    // We need a @Transactional annotation here
    // because in this particular case this method is called from
    // the method with its own @Transactional annotation
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void sendOrder(Order order) {
        log.info("Sending order to the message broker");
        orderEmitter.send(order);
        log.info("Order was successfully sent to the message broker");
    }
}
