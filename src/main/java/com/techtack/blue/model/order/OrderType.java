package com.techtack.blue.model.order;

public enum OrderType {
    NORMAL("Normal", "Standard market or limit order"),
    STOP("Stop", "Stop order that becomes market order when triggered"),
    STOP_LIMIT("Stop Limit", "Stop order that becomes limit order when triggered"),
    TRAILING_STOP("Trailing Stop", "Stop order that trails the market price"),
    TRAILING_STOP_LIMIT("Trailing Stop Limit", "Trailing stop that becomes limit order when triggered"),
    OCO("One-Cancels-Other", "Two orders where execution of one cancels the other"),
    STOP_LOSS_TAKE_PROFIT("Stop Loss/Take Profit", "Combined stop loss and take profit order"),
    GTD("Good Till Date", "Order valid until specified date");
    
    private final String displayName;
    private final String description;
    
    OrderType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    public boolean requiresTriggerPrice() {
        return this == STOP || this == STOP_LIMIT;
    }
    
    public boolean requiresTrailingAmount() {
        return this == TRAILING_STOP || this == TRAILING_STOP_LIMIT;
    }
    
    public boolean requiresProfitLossLevels() {
        return this == OCO || this == STOP_LOSS_TAKE_PROFIT;
    }
    
    public boolean requiresExpiryDate() {
        return this == GTD;
    }
}