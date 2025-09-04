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
    private static final String USER_SERVICE_URL = "http://localhost:8081/users";

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
        notification.setEmail(getUserEmail(event.getUserId())); // fetch via REST call to UserService
        notification.setSubject("🎉 Welcome to Our Bank");
        notification.setMessage("Hello " + event.getName() +
                ",\n\nYour user account has been created successfully.\n" +
                "Next step: Open your bank account to start using our services.");

        sendWelcomeNotification(notification); // this method already calls EmailService
    }

    @Override
    public void processAccountCreatedEvent(AccountCreatedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(getUserEmail(event.getUserId())); // ✅ fetch directly
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
        notification.setEmail(getUserEmail(event.getUserId())); // ✅ fetch directly
        notification.setSubject("Account Approved");
        notification.setMessage("Congratulations! Your account with ID: " + event.getAccountId() +
                " has been approved successfully.");
        sendNotification(notification);
    }

    @Override
    public void processTransactionEvent(TransactionEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(getUserEmail(event.getUserId())); // ✅ fetch directly

        String subject;
        String message;

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
        notification.setEmail(getUserEmail(event.getUserId())); // ✅ fetch directly
        notification.setSubject("Your Loan Application Has Been Submitted");

        notification.setMessage(String.format(
                "Dear Customer,\n\nYour loan application for amount ₹%.2f has been submitted " +
                        "and is pending approval.\n\nLoan ID: %d\n\nThank you for choosing our bank.",
                event.getAmount(), event.getLoanId()
        ));

        sendNotification(notification);
    }

    @Override
    public void processLoanApprovedEvent(LoanApprovedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(getUserEmail(event.getUserId())); // ✅ fetch directly
        notification.setSubject("Your Loan Has Been Approved");

        notification.setMessage(String.format(
                "Dear Customer,\n\nYour loan application has been approved.\n\n" +
                        "Loan ID: %d\nApproved Amount: ₹%.2f\n\nThe sanctioned amount will be " +
                        "disbursed shortly.\n\nThank you for banking with us.",
                event.getLoanId(), event.getAmount()
        ));

        sendNotification(notification);
    }

    private String getUserEmail(Long userId) {
        // Assuming your UserService has an endpoint: GET /api/users/{id}/email
        return restTemplate.getForObject(
                USER_SERVICE_URL + "/" + userId + "/email",
                String.class
        );
    }







//
//    @Override
//    public void processUserRegisteredEvent(UserRegisteredEvent event) {
//        Notification notification = new Notification();
//        notification.setUserId(event.getUserId());
//        notification.setEmail(event.getEmail());
//        notification.setSubject("Welcome to Our Bank");
//        notification.setMessage("Hello " + event.getName() + ", you have successfully created the user account in our bank next step is to open a Account");
//        sendWelcomeNotification(notification);
//    }
//    @Override
//    public void processAccountCreatedEvent(AccountCreatedEvent event) {
//        Notification notification = new Notification();
//        notification.setUserId(event.getUserId());
//        notification.setEmail(event.getUserEmail());
//        notification.setSubject("Account Request Submitted");
//        notification.setMessage("Hello, your account request (Number: " + event.getAccountNumber() +
//                ", ID: " + event.getAccountId() + ") has been submitted successfully. " +
//                "It is currently waiting for manager approval.");
//        sendNotification(notification);
//    }
//
//    @Override
//    public void processAccountApprovedEvent(AccountApprovedEvent event) {
//        Notification notification = new Notification();
//        notification.setUserId(event.getUserId());
//        notification.setEmail(event.getUserEmail());
//        notification.setSubject("Account Approved");
//        notification.setMessage("Congratulations! Your account with ID: " + event.getAccountId() +
//                " has been approved successfully.");
//        sendNotification(notification);
//    }
//
//    @Override
////    public void processTransactionEvent(TransactionEvent event) {
////        Notification notification = new Notification();
////        notification.setUserId(event.getUserId());
////        notification.setEmail(event.getUserEmail());
////        notification.setSubject("Transaction Alert");
////        notification.setMessage("Your account " + event.getAccountId() + " has a " + event.getType() +
////                " transaction of amount " + event.getAmount() + ". Description: " + event.getDescription() +
////                ". Transaction ID: " + event.getTransactionId());
////        sendNotification(notification);
////    }
//
//    public void processTransactionEvent(TransactionEvent event) {
//        Notification notification = new Notification();
//        notification.setUserId(event.getUserId());
//        notification.setEmail(getUserEmail(event.getUserId())); // ✅ fetch directly
//
//        String subject;
//        String message;
//
//        switch (event.getType()) {
//            case "DEPOSIT":
//                subject = "Deposit Alert - Account " + event.getAccountId();
//                message = "Your account " + event.getAccountId() +
//                        " has been credited with ₹" + event.getAmount() +
//                        ". Description: " + event.getDescription();
//                break;
//
//            case "WITHDRAW":
//                subject = "Withdrawal Alert - Account " + event.getAccountId();
//                message = "Your account " + event.getAccountId() +
//                        " has been debited with ₹" + event.getAmount() +
//                        ". Description: " + event.getDescription();
//                break;
//
//            case "TRANSFER_IN":
//                subject = "Fund Transfer Alert - Amount Credited";
//                message = "You have received ₹" + event.getAmount() +
//                        " in your account " + event.getAccountId();
//                break;
//
//            case "TRANSFER_OUT":
//                subject = "Fund Transfer Alert - Amount Debited";
//                message = "You have transferred ₹" + event.getAmount() +
//                        " from your account " + event.getAccountId();
//                break;
//
//            default:
//                subject = "Transaction Alert";
//                message = "A transaction has occurred on your account.";
//        }
//
//        notification.setSubject(subject);
//        notification.setMessage(message);
//        sendNotification(notification);
//    }
//
//    @Override
////    public void processLoanAppliedEvent(LoanAppliedEvent event) {
////        Notification notification = new Notification();
////        notification.setUserId(event.getUserId());
////        notification.setEmail(event.getUserEmail());
////        notification.setSubject("Loan Application Submitted");
////        notification.setMessage("Your loan application for amount " + event.getAmount() +
////                " has been submitted successfully and is pending approval. Loan ID: " + event.getLoanId());
////        sendNotification(notification);
////    }
//    public void processLoanAppliedEvent(LoanAppliedEvent event) {
//        Notification notification = new Notification();
//        notification.setUserId(event.getUserId());
//        notification.setEmail(event.getUserEmail());
//        notification.setSubject("Your Loan Application Has Been Successfully Submitted");
//
//        String message = String.format(
//                "Dear Customer,\n\n" +
//                        "We are pleased to inform you that your loan application has been successfully submitted and is currently pending approval.\n\n" +
//                        "Loan Details:\n" +
//                        "• Loan ID: %d\n" +
//                        "• Loan Amount: %.2f\n\n" +
//                        "Our team will carefully review your application, and you will be notified once a decision has been made.\n\n" +
//                        "Thank you for choosing our banking services.\n\n" +
//                        "Warm regards,\n" +
//                        "Banking Services Team",
//                event.getLoanId(),
//                event.getAmount()
//        );
//
//        notification.setMessage(message);
//        sendNotification(notification);
//    }
//
//
//    @Override
//    public void processLoanApprovedEvent(LoanApprovedEvent event) {
//        Notification notification = new Notification();
//        notification.setUserId(event.getUserId());
//        notification.setEmail(event.getUserEmail());
//        notification.setSubject("Your Loan Application Has Been Approved");
//
//        String message = String.format(
//                "Dear Customer,\n\n" +
//                        "We are pleased to inform you that your loan application has been successfully approved.\n\n" +
//                        "Loan Details:\n" +
//                        "• Loan ID: %s\n" +
//                        "• Approved Amount: %.2f\n\n" +
//                        "The sanctioned loan amount will be disbursed to your account shortly. Please keep this Loan ID for your future reference.\n\n" +
//                        "If you have any questions or require further assistance, feel free to contact our support team.\n\n" +
//                        "Thank you for choosing our banking services.\n\n" +
//                        "Warm regards,\n" +
//                        "Banking Services Team",
//                event.getLoanId(),
//                event.getAmount()
//        );
//
//        notification.setMessage(message);
//        sendNotification(notification);
//    }
//    private String getUserEmail(Long userId) {
//        // Assuming your UserService has an endpoint: GET /api/users/{id}/email
//        return restTemplate.getForObject(
//                USER_SERVICE_URL + "/" + userId + "/email",
//                String.class
//        );
//    }
}




