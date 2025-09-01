package com.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanAppliedEvent {
    private Long loanId;
    private Long userId;
    private String userEmail;
    private Double amount;
}
