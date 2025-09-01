package com.example.notificationservice.controller;


import com.example.notificationservice.dto.*;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    // 1. POST /notifications/send
    @PostMapping("/send")
    public Notification sendNotification(@RequestBody Notification notification) {
        return notificationService.sendNotification(notification);
    }

    // 2. GET /notifications/{id}
    @GetMapping("/{id}")
    public Notification getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id);
    }

    // 3. GET /notifications/user/{userId}
    @GetMapping("/user/{userId}")
    public List<Notification> getNotificationsByUserId(@PathVariable Long userId) {
        return notificationService.getNotificationsByUserId(userId);
    }

    // Test endpoint for UserRegisteredEvent
    @PostMapping("/test/userRegistered")
    public void testUserRegistered(@RequestBody UserRegisteredEvent event) {
        notificationService.processUserRegisteredEvent(event);
    }

    // Test endpoint for AccountCreatedEvent
    @PostMapping("/test/accountCreated")
    public void testAccountCreated(@RequestBody AccountCreatedEvent event) {
        notificationService.processAccountCreatedEvent(event);
    }

    // Test endpoint for AccountApprovedEvent
    @PostMapping("/test/accountApproved")
    public void testAccountApproved(@RequestBody AccountApprovedEvent event) {
        notificationService.processAccountApprovedEvent(event);
    }

    // Test endpoint for TransactionEvent
    @PostMapping("/test/transaction")
    public void testTransaction(@RequestBody TransactionEvent event) {
        notificationService.processTransactionEvent(event);
    }

    // Test endpoint for LoanAppliedEvent
    @PostMapping("/test/loanApplied")
    public void testLoanApplied(@RequestBody LoanAppliedEvent event) {
        notificationService.processLoanAppliedEvent(event);
    }

    // Test endpoint for LoanApprovedEvent
    @PostMapping("/test/loanApproved")
    public void testLoanApproved(@RequestBody LoanApprovedEvent event) {
        notificationService.processLoanApprovedEvent(event);
    }
}
