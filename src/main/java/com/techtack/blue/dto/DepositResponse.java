package com.techtack.blue.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DepositResponse {
    
    private Long transactionId;
    private Double amount;
    private String status;
    private String depositMethod;
    private String transactionReference;
    private Double newBalance;
    private Double newBuyingPower;
    private LocalDateTime createdAt;
    private String message;
}
