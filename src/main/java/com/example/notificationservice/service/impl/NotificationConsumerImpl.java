package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.LoanApprovedEvent;
import com.example.notificationservice.dto.LoanAppliedEvent;
import com.example.notificationservice.dto.AccountApprovedEvent;
import com.example.notificationservice.dto.AccountCreatedEvent;
import com.example.notificationservice.dto.TransactionEvent;
import com.example.notificationservice.dto.UserRegisteredEvent;
import com.example.notificationservice.service.NotificationConsumer;
import com.example.notificationservice.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumerImpl implements NotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @KafkaListener(
            topics = "user-registered-topic",
            groupId = "notification-service-group"
    )
    public void consumeUserRegistered(String message) {
        try {
            UserRegisteredEvent event = objectMapper.readValue(message, UserRegisteredEvent.class);
            System.out.println("Received UserRegisteredEvent: " + event);
            notificationService.processUserRegisteredEvent(event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public void consumeUserRegistered(String event) throws JsonProcessingException {
//        System.out.println("📩 Received UserRegisteredEvent: " + event);
//        UserRegisteredEvent userRegisteredEvent = objectMapper.readValue(event, UserRegisteredEvent.class);
//        notificationService.processUserRegisteredEvent(userRegisteredEvent);
//    }


     //2️⃣ Account Created
    @KafkaListener(topics = "account-created-topic",
            groupId = "notification-service")
    public void consumeAccountCreated(String jsonEvent) {
        try {
            AccountCreatedEvent event = objectMapper.readValue(jsonEvent, AccountCreatedEvent.class);
            System.out.println("Received AccountCreatedEvent: " + event);
            notificationService.processAccountCreatedEvent(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 3️⃣ Account Approved
    @KafkaListener(topics = "account-approved-topic",
            groupId = "notification-service")
    public void consumeAccountApproved(String jsonEvent) {
        try {
            AccountApprovedEvent event = objectMapper.readValue(jsonEvent, AccountApprovedEvent.class);
            System.out.println("Received AccountApprovedEvent: " + event);
            notificationService.processAccountApprovedEvent(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 4️⃣ Loan Applied
    @KafkaListener(topics = "loan-applied-topic", groupId = "notification-service")
    public void consumeLoanApplied(String jsonEvent) {
        try {
            LoanAppliedEvent event = objectMapper.readValue(jsonEvent, LoanAppliedEvent.class);
            System.out.println("Received LoanAppliedEvent: " + event);
            notificationService.processLoanAppliedEvent(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 5️⃣ Loan Approved
    @KafkaListener(topics = "loan-approved-topic", groupId = "notification-service")
    public void consumeLoanApproved(String jsonEvent) throws JsonProcessingException {
        try {
            LoanApprovedEvent event = objectMapper.readValue(jsonEvent, LoanApprovedEvent.class);
            System.out.println("Received LoanApprovedEvent: " + event);
            notificationService.processLoanApprovedEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }

    // 6️⃣ Transaction Event
    @KafkaListener(topics = "transaction-topic", groupId = "notification-service")
    public void consumeTransaction(String jsonEvent) {
        try {
            TransactionEvent event = objectMapper.readValue(jsonEvent, TransactionEvent.class);
            System.out.println("Received TransactionEvent: " + event);
            notificationService.processTransactionEvent(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}