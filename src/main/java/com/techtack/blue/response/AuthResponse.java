package com.techtack.blue.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private String message;
    private Long id;
    private String email;
    private String username;
    private String phoneNumber;
    private Double accountBalance;
    private Double buyingPower;
    private boolean verified;
    private String primaryAccountNumber;
    private String primaryAccountType;
    
    public AuthResponse() {
    }
    
    public AuthResponse(String jwt, String message, Long id) {
        this.jwt = jwt;
        this.message = message;
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public Double getAccountBalance() {
        return accountBalance;
    }
    
    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }
    
    public Double getBuyingPower() {
        return buyingPower;
    }
    
    public void setBuyingPower(Double buyingPower) {
        this.buyingPower = buyingPower;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    
    public String getPrimaryAccountNumber() {
        return primaryAccountNumber;
    }
    
    public void setPrimaryAccountNumber(String primaryAccountNumber) {
        this.primaryAccountNumber = primaryAccountNumber;
    }
    
    public String getPrimaryAccountType() {
        return primaryAccountType;
    }
    
    public void setPrimaryAccountType(String primaryAccountType) {
        this.primaryAccountType = primaryAccountType;
    }
    
    public String getJwt() {
        return jwt;
    }
    
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
}
