
package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.*;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.entity.enums.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.EmailService;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private  final EmailService emailService;

    private final RestTemplate restTemplate;  // ✅ inject here

    // URL of User Service (adjust to your actual endpoint)
    //private static final String USER_SERVICE_URL = "http://localhost:8081/users";

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
//    @Override
//    public void processUserRegisteredEvent(UserRegisteredEvent event) {
//        Notification notification = new Notification();
//        notification.setUserId(event.getUserId());
//        notification.setEmail(getUserEmail(event.getUserId())); // fetch via REST call to UserService
//        notification.setSubject("🎉 Welcome to Our Bank");
//        notification.setMessage("Hello " + event.getName() +
//                ",\n\nYour user account has been created successfully.\n" +
//                "Next step: Open your bank account to start using our services.");
//
//        sendWelcomeNotification(notification); // this method already calls EmailService
//    }
    @Override
    public void processUserRegisteredEvent(UserRegisteredEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getEmail()); // ✅ use directly
        notification.setSubject("🎉 Welcome to Our Bank");
        notification.setMessage("Dear " + event.getName() + ",\n\n"
                + "We are delighted to welcome you to the [Your Bank Name] family! "
                + "Your user account has been successfully created. We are excited to have you on board.\n\n"
                + "Your account gives you access to a suite of secure and convenient financial services.\n\n"
                + "What's next?\n\n"
                + "To get started, please log in to your account and complete the application to open your first bank account. You can do this by following the link below:\n\n"
                + "[Link to account creation page, e.g., https://yourbank.com/open-account]\n\n"
                + "If you have any questions, please do not hesitate to contact our customer support team at [Support Email Address] or by phone at [Support Phone Number].\n\n"
                + "Thank you for choosing [Your Bank Name]. We look forward to serving you.\n\n"
                + "Sincerely,\n"
                + "The [Your Bank Name] Team.");

        sendWelcomeNotification(notification);
    }

//    @Override
    public void processAccountCreatedEvent(AccountCreatedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getEmail()); // ✅ use directly
        notification.setSubject("Account Request Submitted");
        notification.setMessage("Dear Valued Customer,\n\n"
                + "Thank you for your application to open a bank account with us. We are pleased to confirm that your request has been successfully submitted and is now under review.\n\n"
                + "For your records, the details of your application are as follows:\n"
                + "Account Number: " + event.getAccountNumber() + "\n"
                + "Reference ID: " + event.getAccountId() + "\n\n"
                + "Our team is working diligently to process your application. You will receive a separate notification via email once a decision has been made by the manager.\n\n"
                + "Thank you for your patience and for choosing [Your Bank Name].\n\n"
                + "Sincerely,\n"
                + "The [Your Bank Name] Team");
        sendNotification(notification);
    }

    @Override
    public void processAccountApprovedEvent(AccountApprovedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getEmail()); // ✅ use directly        notification.setSubject("Account Approved");
        notification.setMessage("Dear Valued Customer,\n\n"
                + "We are pleased to inform you that your account application has been approved. Your new account is now active.\n\n"
                + "Account Number: " + event.getAccountNumber() + "\n"
                + "Reference ID: " + event.getAccountId() + "\n\n"
                + "You can now log in to your dashboard to get started with our services.\n\n"
                + "Sincerely,\n"
                + "The [Your Bank Name] Team");
        sendNotification(notification);
    }


    @Override
    public void processTransactionEvent(TransactionEvent event) {
        String subject;
        String message;
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getUserEmail()); // ✅ use directly        String message;

        switch (event.getType()) {
            case "DEPOSIT":
                subject = "Deposit Alert - Account " + event.getAccountId();
                message = "Your account " + event.getAccountId() +
                        " has been credited with ₹" + event.getAmount() +
                        ". Description: " + event.getDescription();
                break;
            case "WITHDRAW":
                subject = "Withdrawal Alert - Account " + event.getAccountId();
                message = "Your account " + event.getAccountId() +
                        " has been debited with ₹" + event.getAmount() +
                        ". Description: " + event.getDescription();
                break;
            case "TRANSFER_IN":
                subject = "Fund Transfer Alert - Amount Credited";
                message = "You have received ₹" + event.getAmount() +
                        " in your account " + event.getAccountId();
                break;
            case "TRANSFER_OUT":
                subject = "Fund Transfer Alert - Amount Debited";
                message = "You have transferred ₹" + event.getAmount() +
                        " from your account " + event.getAccountId();
                break;
            default:
                subject = "Transaction Alert";
                message = "A transaction has occurred on your account.";
        }

        notification.setSubject(subject);
        notification.setMessage(message);
        sendNotification(notification);
    }

    @Override
    public void processLoanAppliedEvent(LoanAppliedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getUserEmail()); // ✅ use directly        notification.setSubject("Your Loan Has Been Approved");
        notification.setSubject("Your Loan Application Has Been Submitted");

        notification.setMessage(String.format(
                "Dear Valued Customer,\n\n" +
                        "Thank you for submitting your loan application with [Your Bank Name]. We are pleased to confirm that we have received your request for a loan of ₹%.2f.\n\n" +
                        "Your application has been assigned the following ID for your records:\n" +
                        "Loan ID: %d\n\n" +
                        "Our team is now carefully reviewing your application. We will contact you via email within [e.g., 2-3 business days] to provide you with an update on your status.\n\n" +
                        "We appreciate your patience and look forward to assisting you.\n\n" +
                        "Sincerely,\n" +
                        "The [Your Bank Name] Team",
                event.getAmount(), event.getLoanId()
        ));

        sendNotification(notification);
    }

    @Override
    public void processLoanApprovedEvent(LoanApprovedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getUserEmail()); // ✅ use directly        notification.setSubject("Your Loan Has Been Approved");

        notification.setMessage(String.format(
                "Dear Valued Customer,\n\n" +
                        "Great news! We are pleased to inform you that your loan application has been successfully approved.\n\n" +
                        "**Loan Details:**\n" +
                        "Loan ID: %d\n" +
                        "Approved Amount: ₹%.2f\n\n" +
                        "The sanctioned amount will be disbursed to your account within [e.g., 24 hours]. You will receive a separate notification once the funds have been credited.\n\n" +
                        "Thank you for banking with [Your Bank Name]. We look forward to supporting your financial goals.\n\n" +
                        "Sincerely,\n" +
                        "The [Your Bank Name] Team",
                event.getLoanId(), event.getAmount()
        ));

        sendNotification(notification);
    }


}
