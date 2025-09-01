package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.LoanAppliedEvent;
import com.example.notificationservice.dto.LoanApprovedEvent;
import com.example.notificationservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LoanEventConsumer {

    private final EmailService emailService;

    public LoanEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    // Loan Application → Notify employee
    @KafkaListener(topics = "loan-applied-events", groupId = "notification-group")
    public void consumeLoanAppliedEvent(LoanAppliedEvent event) {
        String subject = "New Loan Application Pending Approval";
        String message = "A user with email " + event.getUserEmail() +
                " has applied for a loan of ₹" + event.getAmount() +
                ". Loan ID: " + event.getLoanId();
        // Here you send to employee's email, e.g. "employee@bank.com"
        emailService.sendNormalEmail("employee@bank.com", subject, message);
    }

    // Loan Approval → Notify user
    @KafkaListener(topics = "loan-approved-events", groupId = "notification-group")
    public void consumeLoanApprovedEvent(LoanApprovedEvent event) {
        String subject = "Your Loan Has Been Approved!";
        String message = "Dear User, your loan of ₹" + event.getAmount() +
                " (Loan ID: " + event.getLoanId() + ") has been approved.";
        emailService.sendNormalEmail(event.getUserEmail(), subject, message);
    }
}
