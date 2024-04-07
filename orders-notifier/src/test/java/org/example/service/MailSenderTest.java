package org.example.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.test.junit.QuarkusTest;
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
class MailSenderTest {

    @InjectMocks
    MailSender mailSender;

    @Mock
    ReactiveMailer mailer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotifyAboutOrderCreation() {

        var order = new Order(
                2L,
                "test address",
                LocalDateTime.now().minusDays(15).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(15).truncatedTo(ChronoUnit.MINUTES)
        );

        mailSender.notifyAboutOrderCreation(order);
        verify(mailer, times(1)).send(any(Mail.class));
    }
}