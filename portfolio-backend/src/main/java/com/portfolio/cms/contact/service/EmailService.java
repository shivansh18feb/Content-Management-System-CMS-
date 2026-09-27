package com.portfolio.cms.contact.service;

public interface EmailService {
    void sendContactNotification(String senderName, String senderEmail, String subject, String messageContent);
}
