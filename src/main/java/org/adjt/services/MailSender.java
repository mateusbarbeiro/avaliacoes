package org.adjt.services;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MailSender {

    @Inject
    Mailer mailer;

    public void sendEmail(String to, String subject, String body) {
        // Monta e envia o e-mail em apenas uma linha
        mailer.send(Mail.withText(to, subject, body));
    }

    public void sendEmailHtml(String to, String subject, String body) {
        // Monta e envia o e-mail em apenas uma linha
        mailer.send(Mail.withHtml(to, subject, body));
    }
}
