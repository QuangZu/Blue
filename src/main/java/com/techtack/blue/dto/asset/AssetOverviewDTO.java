package com.techtack.blue.dto.asset;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class AssetOverviewDTO {
    
    // Main account information
    private String accountNumber;
    private String accountName;
    private String accountType;
    private String currency;
    
    // NAV (Net Asset Value) information
    private Double totalNAV;
    private Double stockNAV;
    private Double investmentProductsNAV;
    private Double fundNAV;
    
    // Asset breakdown
    private Double totalAssets;
    private Double cashBalance;
    private Double listedSecurities;
    private Double privateCorporateBonds;
    private Double sBond;
    private Double sBondPro;
    private Double sCash;
    
    // Liabilities and buying power
    private Double liabilities;
    private Double buyingPower;
    private Double withdrawable;
    private Double availableAdvancedCash;
    private Double rightSubscription;
    private Double cashDividend;
    
    // Unmatched orders
    private UnmatchedOrdersDTO unmatchedOrders;
    
    // Multiple accounts
    private List<AccountSummaryDTO> accounts;
    
    @Data
    @Builder
    public static class UnmatchedOrdersDTO {
        private Double buyValue;
        private Double sellValue;
        private Double t0Buy;
        private Double t0Sell;
        private Double t1Buy;
        private Double t1Sell;
    }
    
    @Data
    @Builder
    public static class AccountSummaryDTO {
        private String accountNumber;
        private String accountName;
        private String accountType;
        private String accountTypeDisplay; // "Cash account", "Margin account", "Derivatives account"
        private boolean isPrimary;
        private String logoType; // for UI display (SSI, SSI-gray, SSI-green)
    }
}
