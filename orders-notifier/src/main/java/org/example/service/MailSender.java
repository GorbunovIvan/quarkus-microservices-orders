package org.example.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.example.model.Order;

@ApplicationScoped
@Slf4j
public class MailSender {

    @Inject
    ReactiveMailer mailer;

    @ConfigProperty(name = "mail.receiver-email")
    String emailOfReceiver;

    public void notifyAboutOrderCreation(Order order) {
        var subject = composeOrderCreationSubjectForMail(order);
        var text = composeOrderCreationTextForMail(order);
        sendEmail(subject, text);
    }

    private String composeOrderCreationSubjectForMail(Order order) {
        return String.format("New order was created to address '%s'",
                order.getAddress());
    }

    private String composeOrderCreationTextForMail(Order order) {
        return String.format("Id: %d\nAddress: %s\n Time to arrive: %s\n\n (order was created at: %s)",
                order.getId(),
                order.getAddress(),
                order.getTimeToArrive(),
                order.getCreatedAt());
    }

    private void sendEmail(String subject, String text) {
        log.info("Sending email message to '{}': '{}'", emailOfReceiver, subject);
        var mail = Mail.withText(emailOfReceiver, subject, text);
        mailer.send(mail);
        log.info("Email message successfully sent to '{}': '{}'", emailOfReceiver, subject);
    }
}
