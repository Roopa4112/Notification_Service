package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.*;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.entity.enums.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.EmailService;
import com.example.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private  final EmailService emailService;

    @Override
    public Notification sendNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(NotificationStatus.PENDING);
        Notification saved = notificationRepository.save(notification);

        try {
            emailService.sendNormalEmail(saved.getEmail(), saved.getSubject(), saved.getMessage());
            saved.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            saved.setStatus(NotificationStatus.FAILED);
        }
        return notificationRepository.save(saved);
    }

    @Override
    public Notification sendWelcomeNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(NotificationStatus.PENDING);
        Notification saved = notificationRepository.save(notification);

        try {
            emailService.sendWelcomeEmail(saved.getEmail(), saved.getSubject(), saved.getMessage());
            saved.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            saved.setStatus(NotificationStatus.FAILED);
        }
        return notificationRepository.save(saved);
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public void processUserRegisteredEvent(UserRegisteredEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getEmail());
        notification.setSubject("Welcome to Our Bank");
        notification.setMessage("Hello " + event.getName() + ", you have successfully created the user account in our bank next step is to open a Account");
        sendWelcomeNotification(notification);
    }
    @Override
    public void processAccountCreatedEvent(AccountCreatedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getUserEmail());
        notification.setSubject("Account Request Submitted");
        notification.setMessage("Hello, your account request (Number: " + event.getAccountNumber() +
                ", ID: " + event.getAccountId() + ") has been submitted successfully. " +
                "It is currently waiting for manager approval.");
        sendNotification(notification);
    }

    @Override
    public void processAccountApprovedEvent(AccountApprovedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getUserEmail());
        notification.setSubject("Account Approved");
        notification.setMessage("Congratulations! Your account with ID: " + event.getAccountId() +
                " has been approved successfully.");
        sendNotification(notification);
    }

    @Override
    public void processTransactionEvent(TransactionEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getUserEmail());
        notification.setSubject("Transaction Alert");
        notification.setMessage("Your account " + event.getAccountId() + " has a " + event.getType() +
                " transaction of amount " + event.getAmount() + ". Description: " + event.getDescription() +
                ". Transaction ID: " + event.getTransactionId());
        sendNotification(notification);
    }

    @Override
//    public void processLoanAppliedEvent(LoanAppliedEvent event) {
//        Notification notification = new Notification();
//        notification.setUserId(event.getUserId());
//        notification.setEmail(event.getUserEmail());
//        notification.setSubject("Loan Application Submitted");
//        notification.setMessage("Your loan application for amount " + event.getAmount() +
//                " has been submitted successfully and is pending approval. Loan ID: " + event.getLoanId());
//        sendNotification(notification);
//    }
    public void processLoanAppliedEvent(LoanAppliedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getUserEmail());
        notification.setSubject("Your Loan Application Has Been Successfully Submitted");

        String message = String.format(
                "Dear Customer,\n\n" +
                        "We are pleased to inform you that your loan application has been successfully submitted and is currently pending approval.\n\n" +
                        "Loan Details:\n" +
                        "• Loan ID: %d\n" +
                        "• Loan Amount: %.2f\n\n" +
                        "Our team will carefully review your application, and you will be notified once a decision has been made.\n\n" +
                        "Thank you for choosing our banking services.\n\n" +
                        "Warm regards,\n" +
                        "Banking Services Team",
                event.getLoanId(),
                event.getAmount()
        );

        notification.setMessage(message);
        sendNotification(notification);
    }


    @Override
    public void processLoanApprovedEvent(LoanApprovedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getUserEmail());
        notification.setSubject("Your Loan Application Has Been Approved");

        String message = String.format(
                "Dear Customer,\n\n" +
                        "We are pleased to inform you that your loan application has been successfully approved.\n\n" +
                        "Loan Details:\n" +
                        "• Loan ID: %s\n" +
                        "• Approved Amount: %.2f\n\n" +
                        "The sanctioned loan amount will be disbursed to your account shortly. Please keep this Loan ID for your future reference.\n\n" +
                        "If you have any questions or require further assistance, feel free to contact our support team.\n\n" +
                        "Thank you for choosing our banking services.\n\n" +
                        "Warm regards,\n" +
                        "Banking Services Team",
                event.getLoanId(),
                event.getAmount()
        );

        notification.setMessage(message);
        sendNotification(notification);
    }


}

