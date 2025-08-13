package com.techtack.blue.model.order;

public enum OrderStatus {
    PENDING("Pending"),
    PARTIALLY_FILLED("Partially Filled"),
    FILLED("Filled"),
    CANCELLED("Cancelled"),
    REJECTED("Rejected"),
    EXPIRED("Expired"),
    TRIGGERED("Triggered"),
    SUSPENDED("Suspended");
    
    private final String displayName;
    
    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    public boolean isActive() {
        return this == PENDING || this == PARTIALLY_FILLED || this == TRIGGERED;
    }
    
    public boolean isCompleted() {
        return this == FILLED || this == CANCELLED || this == REJECTED || this == EXPIRED;
    }
}