package com.techtack.blue.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssetDTO {

    @Data
    @Builder
    public static class DepositRequest {
        private Double amount;
        private String method;
        private Long bankAccountId;
        private String reference;
    }

    @Data
    @Builder
    public static class TransactionResponse {
        private Long transactionId;
        private Double amount;
        private String status;
        private String method;
        private String reference;
        private Double newBalance;
        private Double newBuyingPower;
        private LocalDateTime timestamp;
        private String message;
        private String qrCodeBase64;
        private String qrData;
    }

    @Data
    @Builder
    public static class BankAccount {
        private Long id;
        private String accountNumber;
        private String bankName;
        private String accountHolderName;
        private boolean isPrimary;
        private boolean isVerified;
    }
}
