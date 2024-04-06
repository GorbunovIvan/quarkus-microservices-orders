package org.example.service;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.example.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.*;

@QuarkusTest
class OrdersMessageProducerTest {

    @InjectMocks
    OrdersMessageProducer ordersMessageProducer;

    @Mock
    Emitter<Order> orderEmitter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendOrder() {

        var order = new Order(
                2L,
                "test address",
                LocalDateTime.now().minusDays(15).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(15).truncatedTo(ChronoUnit.MINUTES)
        );

        ordersMessageProducer.sendOrder(order);

        verify(orderEmitter, only()).send(order);
        verify(orderEmitter, times(1)).send(order);
    }
}