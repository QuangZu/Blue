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

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDepositMethod(String depositMethod) {
        this.depositMethod = depositMethod;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }
}
