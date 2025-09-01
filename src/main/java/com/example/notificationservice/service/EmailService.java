package com.example.notificationservice.service;

public interface EmailService {

    void sendWelcomeEmail(String to, String subject, String message);
    void sendNormalEmail(String to, String subject, String message);
    void sendEmail(String to, String subject, String message);

}
