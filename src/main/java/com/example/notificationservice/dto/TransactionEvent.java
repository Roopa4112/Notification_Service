package com.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private Long transactionId;
    private Long accountId;
    private Long userId;
    private String userEmail;
    private Double amount;
    private String type;  // "CREDIT" or "DEBIT"
    private String description;
}
