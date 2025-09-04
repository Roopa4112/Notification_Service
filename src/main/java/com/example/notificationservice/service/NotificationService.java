package com.example.notificationservice.service;

import com.example.notificationservice.dto.*;
import com.example.notificationservice.entity.Notification;

import java.util.List;

public interface NotificationService {

    Notification sendNotification(Notification notification);

    Notification sendWelcomeNotification(Notification notification);

    Notification getNotificationById(Long id);

    List<Notification> getNotificationsByUserId(Long userId);

    void processUserRegisteredEvent(UserRegisteredEvent event);

    void processAccountCreatedEvent(AccountCreatedEvent event);

    void processAccountApprovedEvent(AccountApprovedEvent event);
     void processTransactionEvent(TransactionEvent event);
    void processLoanAppliedEvent(LoanAppliedEvent event);
    void processLoanApprovedEvent(LoanApprovedEvent event);


}
