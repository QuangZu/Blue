package com.techtack.blue.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class DepositRequest {
    
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amount;
    
    @NotNull(message = "Deposit method is required")
    private String depositMethod; // "BANK_TRANSFER" or "QR_CODE"
    
    private Long bankAccountId; // Required for bank transfer
    
    private String transactionReference;
}
