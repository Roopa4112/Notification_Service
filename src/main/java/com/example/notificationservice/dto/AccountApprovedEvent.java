package com.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountApprovedEvent {
    private Long accountId;
    private Long userId;         // ✅ Add this
    private String email;
    private String accountNumber;
    private String employeeId;
    private String accountType;
    private String message;
}
