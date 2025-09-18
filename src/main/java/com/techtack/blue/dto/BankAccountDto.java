package com.techtack.blue.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class BankAccountDto {
    
    private Long id;
    
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    @NotBlank(message = "Bank name is required")
    private String bankName;
    
    private String accountHolderName;
    
    private boolean isPrimary;
    
    private boolean isVerified;
}
