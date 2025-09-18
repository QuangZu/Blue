package com.techtack.blue.dto;

import lombok.Data;

@Data
public class QRCodeResponse {
    
    private String qrCodeBase64;
    private String transactionReference;
    private Double amount;
    private Long userId;
    private String message;
}
