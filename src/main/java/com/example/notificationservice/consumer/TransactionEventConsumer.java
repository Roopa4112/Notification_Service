package com.example.notificationservice.consumer;


import com.example.notificationservice.dto.TransactionEvent;
import com.example.notificationservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionEventConsumer {

    private final EmailService emailService;

    public TransactionEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "transaction-events", groupId = "notification-group")
    public void consumeTransactionEvent(TransactionEvent event) {
        String subject;
        String message;

        if ("CREDIT".equalsIgnoreCase(event.getType())) {
            subject = "Amount Credited to Your Account";
            message = "Dear User, ₹" + event.getAmount() + " has been credited to your account. "
                    + "Transaction ID: " + event.getTransactionId()
                    + ". Description: " + event.getDescription();
        } else {
            subject = "Amount Debited from Your Account";
            message = "Dear User, ₹" + event.getAmount() + " has been debited from your account. "
                    + "Transaction ID: " + event.getTransactionId()
                    + ". Description: " + event.getDescription();
        }

        emailService.sendNormalEmail(event.getUserEmail(), subject, message);
    }
}
