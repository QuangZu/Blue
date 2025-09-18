package com.techtack.blue.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetDTO {
    private Long id;
    private String symbol; // For stocks
    private String name;
    private double quantity; // For stocks
    private double averagePrice; // For stocks
    private double currentPrice;
    private double totalValue;
    private String type; // STOCK, CASH, etc.

    // Watchlist-specific
    private Long watchlistId;

    // Transaction-specific
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TransactionResponse {
        private Long transactionId;
        private double amount;
        private String status;
        private String method;
        private String reference;
        private double newBalance;
        private double newBuyingPower;
        private LocalDateTime timestamp;
        private String message;
        private String qrCodeBase64;
        private String qrData;
    }

    @Data
    @Builder
    public static class DepositRequest {
        private double amount;
        private String method; // e.g., BANK_TRANSFER, QR_CODE
        private Long bankAccountId; // For BANK_TRANSFER
        private String reference; // Optional
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