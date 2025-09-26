package com.techtack.blue.service;


import com.techtack.blue.dto.AssetDTO;
import com.techtack.blue.dto.asset.AssetOverviewDTO;
import com.techtack.blue.dto.asset.PortfolioDTO;
import com.techtack.blue.exception.UserException;
import com.techtack.blue.model.BankAccount;
import com.techtack.blue.model.DepositTransaction;
import com.techtack.blue.model.Portfolio;
import com.techtack.blue.model.TradingAccount;
import com.techtack.blue.model.User;
import com.techtack.blue.model.TradingAccount.AccountType;
import com.techtack.blue.repository.BankAccountRepository;
import com.techtack.blue.repository.DepositTransactionRepository;
import com.techtack.blue.repository.PortfolioRepository;
import com.techtack.blue.repository.TradingAccountRepository;
import com.techtack.blue.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AssetService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BankAccountRepository bankAccountRepository;
    
    @Autowired
    private DepositTransactionRepository depositTransactionRepository;
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TradingAccountRepository tradingAccountRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    public AssetOverviewDTO getAssetOverview(Long userId, String accountNumber) throws UserException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException("User not found"));
        
        return AssetOverviewDTO.builder()
            .accountNumber(accountNumber)
            .accountName(user.getUsername())
            .totalNAV(user.getAccountBalance())
            .cashBalance(user.getAccountBalance())
            .buyingPower(user.getBuyingPower())
            .build();
    }

    public PortfolioDTO getPortfolio(Long userId, String accountNumber) throws UserException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException("User not found"));
        
        // Implementation would go here
        // This would include fetching holdings, allocations, etc.
        
        return PortfolioDTO.builder()
            .accountNumber(accountNumber)
            .totalValue(0.0) // Placeholder
            .totalCost(0.0)  // Placeholder
            .profitLoss(0.0) // Placeholder
            .build();
    }

    @Transactional
    public void initializeUserAccounts(User user) {
        String accountNameBase = user.getUsername() != null ? user.getUsername() : user.getEmail();

        // Create a CASH account
        TradingAccount cashAccount = new TradingAccount();
        cashAccount.setUser(user);
        cashAccount.setAccountNumber(generateAccountNumber(user, AccountType.CASH));
        cashAccount.setAccountType(AccountType.CASH);
        cashAccount.setAccountName(accountNameBase + " - CASH");
        cashAccount.setPrimary(true);
        cashAccount.setActive(true);
        tradingAccountRepository.save(cashAccount);

        // Create a MARGIN account
        TradingAccount marginAccount = new TradingAccount();
        marginAccount.setUser(user);
        marginAccount.setAccountNumber(generateAccountNumber(user, AccountType.MARGIN));
        marginAccount.setAccountType(AccountType.MARGIN);
        marginAccount.setAccountName(accountNameBase + " - MARGIN");
        marginAccount.setPrimary(false);
        marginAccount.setActive(true);
        tradingAccountRepository.save(marginAccount);

        // Create a portfolio for the user
        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        portfolio.setTotalValue(BigDecimal.ZERO);
        portfolio.setCashBalance(BigDecimal.ZERO);
        portfolioRepository.save(portfolio);
    }
    
    @Transactional
    public AssetDTO.TransactionResponse processDeposit(Long userId, String accountNumber, AssetDTO.DepositRequest request) throws UserException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException("User not found"));
        
        DepositTransaction transaction = new DepositTransaction();
        transaction.setUser(user);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionReference(request.getReference() != null ? 
            request.getReference() : qrCodeService.generateTransactionReference());
        
        if ("BANK_TRANSFER".equals(request.getMethod())) {
            BankAccount bankAccount = bankAccountRepository.findById(request.getBankAccountId())
                .orElseThrow(() -> new UserException("Bank account not found"));
            
            if (!bankAccount.getUser().getId().equals(userId)) {
                throw new UserException("Bank account does not belong to user");
            }
            
            transaction.setBankAccount(bankAccount);
            transaction.setDepositMethod(DepositTransaction.DepositMethod.BANK_TRANSFER);
        } else {
            transaction.setDepositMethod(DepositTransaction.DepositMethod.QR_CODE);
        }
        
        // Process deposit
        transaction.setStatus(DepositTransaction.TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());
        
        // Update user balance
        user.setAccountBalance(user.getAccountBalance() + request.getAmount());
        user.setBuyingPower(user.getBuyingPower() + request.getAmount());
        
        userRepository.save(user);
        transaction = depositTransactionRepository.save(transaction);

        notificationService.sendNotificationToDevice(
            "user_device_token_" + userId,
            "Deposit Successful",
            String.format("$%.2f has been added to your account", request.getAmount()),
            null
        );
        
        // Build response
        return AssetDTO.TransactionResponse.builder()
            .transactionId(transaction.getId())
            .amount(transaction.getAmount())
            .status(transaction.getStatus().toString())
            .method(transaction.getDepositMethod().toString())
            .reference(transaction.getTransactionReference())
            .newBalance(user.getAccountBalance())
            .newBuyingPower(user.getBuyingPower())
            .timestamp(transaction.getCreatedAt())
            .message("Deposit successful")
            .build();
    }

    public AssetDTO.TransactionResponse generateDepositQRCode(Long userId, String accountNumber, Double amount) throws UserException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException("User not found"));
        
        String transactionRef = qrCodeService.generateTransactionReference();
        String qrData = qrCodeService.generateDepositQRData(userId, amount, transactionRef);
        String qrCodeBase64 = qrCodeService.generateQRCode(qrData);
        
        // Create pending transaction
        DepositTransaction transaction = new DepositTransaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setDepositMethod(DepositTransaction.DepositMethod.QR_CODE);
        transaction.setStatus(DepositTransaction.TransactionStatus.PENDING);
        transaction.setTransactionReference(transactionRef);
        transaction.setQrCode(qrCodeBase64);
        depositTransactionRepository.save(transaction);
        
        return AssetDTO.TransactionResponse.builder()
            .transactionId(transaction.getId())
            .amount(transaction.getAmount())
            .status(transaction.getStatus().toString())
            .method(transaction.getDepositMethod().toString())
            .reference(transaction.getTransactionReference())
            .qrCodeBase64(qrCodeBase64)
            .qrData(qrData)
            .message("QR code generated successfully")
            .build();
    }

    @Transactional
    public AssetDTO.BankAccount addBankAccount(Long userId, AssetDTO.BankAccount dto) throws UserException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException("User not found"));
        
        // Check if account already exists
        if (bankAccountRepository.existsByAccountNumberAndBankName(dto.getAccountNumber(), dto.getBankName())) {
            throw new UserException("Bank account already exists");
        }
        
        BankAccount bankAccount = new BankAccount();
        bankAccount.setUser(user);
        bankAccount.setAccountNumber(dto.getAccountNumber());
        bankAccount.setBankName(dto.getBankName());
        bankAccount.setAccountHolderName(dto.getAccountHolderName());
        bankAccount.setPrimary(dto.isPrimary());

        if (dto.isPrimary()) {
            List<BankAccount> userAccounts = bankAccountRepository.findByUser(user);
            for (BankAccount account : userAccounts) {
                account.setPrimary(false);
                bankAccountRepository.save(account);
            }
        }
        
        bankAccount = bankAccountRepository.save(bankAccount);
        
        return AssetDTO.BankAccount.builder()
            .id(bankAccount.getId())
            .accountNumber(bankAccount.getAccountNumber())
            .bankName(bankAccount.getBankName())
            .accountHolderName(bankAccount.getAccountHolderName())
            .isPrimary(bankAccount.isPrimary())
            .isVerified(bankAccount.isVerified())
            .build();
    }

    public List<AssetDTO.BankAccount> getUserBankAccounts(Long userId) {
        List<BankAccount> accounts = bankAccountRepository.findByUserId(userId);
        
        return accounts.stream().map(account -> {
            return AssetDTO.BankAccount.builder()
                .id(account.getId())
                .accountNumber(maskAccountNumber(account.getAccountNumber()))
                .bankName(account.getBankName())
                .accountHolderName(account.getAccountHolderName())
                .isPrimary(account.isPrimary())
                .isVerified(account.isVerified())
                .build();
        }).collect(Collectors.toList());
    }

    public List<AssetDTO.TransactionResponse> getUserDepositHistory(Long userId) {
        List<DepositTransaction> transactions = depositTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return transactions.stream().map(tx -> {
            return AssetDTO.TransactionResponse.builder()
                .transactionId(tx.getId())
                .amount(tx.getAmount())
                .status(tx.getStatus().toString())
                .method(tx.getDepositMethod().toString())
                .reference(tx.getTransactionReference())
                .timestamp(tx.getCreatedAt())
                .build();
        }).collect(Collectors.toList());
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        int len = accountNumber.length();
        return "*".repeat(len - 4) + accountNumber.substring(len - 4);
    }

    private String generateAccountNumber(User user, AccountType accountType) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return String.format("B-%s-%s-%s", user.getId(), accountType.toString().substring(0, 1), uuid.substring(0, 6));
    }
}

