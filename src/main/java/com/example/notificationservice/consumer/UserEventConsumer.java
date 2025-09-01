package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.UserRegisteredEvent;
import com.example.notificationservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {

    private final EmailService emailService;

    public UserEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    // Listen to user registration topic
    @KafkaListener(topics = "user-registered", groupId = "notification-service")
    public void consume(UserRegisteredEvent event) {
        System.out.println("📩 Received UserRegisteredEvent: " + event);

        String subject = "Welcome to Our Banking System!";
        String body = "Hello " + event.getName() + ",\n\n" +
                "Your user account has been successfully created with email: " + event.getEmail() + ".\n" +
                "You can now log in and start using our services.\n\n" +
                "Regards,\nBanking Team";

        // Send email
        emailService.sendEmail(event.getEmail(), subject, body);
    }
}
