package com.example.notificationservice.service;

import com.example.notificationservice.dto.UserRegisteredEvent;
import com.example.notificationservice.entity.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface NotificationConsumer {
    void consumeUserRegistered(String event) throws JsonProcessingException;
    void consumeAccountCreated(String event)  throws JsonProcessingException;
    void consumeAccountApproved(String event) throws JsonProcessingException;
    void consumeLoanApplied(String event) throws JsonProcessingException;
    void consumeLoanApproved(String event) throws JsonProcessingException;
     void consumeTransaction(String event) throws JsonProcessingException;
}
