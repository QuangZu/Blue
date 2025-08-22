package com.techtack.blue.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private String message;
    private Long id;
    
    public AuthResponse() {
    }
    
    public AuthResponse(String jwt, String message, Long id) {
        this.jwt = jwt;
        this.message = message;
        this.id = id;
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
