package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.example.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
class OrdersMessageConsumerTest {

    @InjectMocks
    OrdersMessageConsumer ordersMessageConsumer;

    @Mock
    MailSender mailSender;

    @Spy
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper.findAndRegisterModules();
    }

    @Test
    void testReceiveMessageFromBrokerAndSendNotification() throws JsonProcessingException {

        var order = new Order(
                2L,
                "test address",
                LocalDateTime.now().minusDays(15).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(15).truncatedTo(ChronoUnit.MINUTES)
        );

        var orderJson = objectMapper.writeValueAsString(order);
        ordersMessageConsumer.receiveMessageFromBrokerAndSendNotification(orderJson);

        verify(mailSender, times(1)).notifyAboutOrderCreation(any(Order.class));
        verify(mailSender, times(1)).notifyAboutOrderCreation(order);
    }
}