package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.AccountCreatedEvent;
import com.example.notificationservice.dto.AccountApprovedEvent;
import com.example.notificationservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AccountEventConsumer {

    private final EmailService emailService;

    public AccountEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "account-created", groupId = "notification-group")
    public void consumeAccountCreated(AccountCreatedEvent event) {
        String subject = "Your Account Application Received";
        String message = "Hi User,\n\nYour account request with number " + event.getAccountNumber()
                + " has been submitted and is pending approval.";
        emailService.sendNormalEmail(event.getUserEmail(), subject, message);
    }

    @KafkaListener(topics = "account-approved", groupId = "notification-group")
    public void consumeAccountApproved(AccountApprovedEvent event) {
        String subject = "Your Account has been Approved";
        String message = "Hi User,\n\nCongratulations! Your account (ID: " + event.getAccountId() + ") has been approved.";
        emailService.sendNormalEmail(event.getUserEmail(), subject, message);
    }
}
