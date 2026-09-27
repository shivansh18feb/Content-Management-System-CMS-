package com.portfolio.cms.contact.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@portfolio.com}")
    private String fromEmail;

    @Value("${app.mail.notify-to:admin@portfolio.com}")
    private String notifyToEmail;

    @Override
    public void sendContactNotification(String senderName, String senderEmail, String subject, String messageContent) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(notifyToEmail);
            mailMessage.setSubject("[Portfolio Inquiry] " + subject);
            mailMessage.setText(String.format(
                    "You received a new inquiry on your portfolio website!\n\n" +
                    "Name: %s\n" +
                    "Email: %s\n" +
                    "Subject: %s\n\n" +
                    "Message:\n%s\n\n" +
                    "You can view and reply from your CMS admin dashboard.",
                    senderName, senderEmail, subject, messageContent
            ));

            mailSender.send(mailMessage);
            log.info("Dispatched contact notification email to: {}", notifyToEmail);
        } catch (Exception ex) {
            log.warn("Could not send email notification via SMTP (Local development mode active): {}. Inquiry logged safely.", ex.getMessage());
        }
    }
}
