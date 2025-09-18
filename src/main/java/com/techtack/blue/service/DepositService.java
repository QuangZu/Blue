package com.techtack.blue.service;

import com.techtack.blue.dto.*;
import com.techtack.blue.exception.UserException;
import com.techtack.blue.model.*;
import com.techtack.blue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepositService {
    
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
    
    @Transactional
    public DepositResponse processDeposit(Long userId, DepositRequest request) throws UserException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException("User not found"));
        
        DepositTransaction transaction = new DepositTransaction();
        transaction.setUser(user);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionReference(request.getTransactionReference() != null ? 
            request.getTransactionReference() : qrCodeService.generateTransactionReference());
        
        if ("BANK_TRANSFER".equals(request.getDepositMethod())) {
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
        DepositResponse response = new DepositResponse();
        response.setTransactionId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setStatus(transaction.getStatus().toString());
        response.setDepositMethod(transaction.getDepositMethod().toString());
        response.setTransactionReference(transaction.getTransactionReference());
        response.setNewBalance(user.getAccountBalance());
        response.setNewBuyingPower(user.getBuyingPower());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setMessage("Deposit successful");
        
        return response;
    }
    
    public QRCodeResponse generateDepositQRCode(Long userId, Double amount) throws UserException {
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
        
        QRCodeResponse response = new QRCodeResponse();
        response.setQrCodeBase64(qrCodeBase64);
        response.setTransactionReference(transactionRef);
        response.setAmount(amount);
        response.setUserId(userId);
        response.setMessage("QR code generated successfully");
        
        return response;
    }
    
    @Transactional
    public BankAccountDto addBankAccount(Long userId, BankAccountDto dto) throws UserException {
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
        
        // If this is set as primary, unset other primary accounts
        if (dto.isPrimary()) {
            List<BankAccount> userAccounts = bankAccountRepository.findByUser(user);
            for (BankAccount account : userAccounts) {
                account.setPrimary(false);
                bankAccountRepository.save(account);
            }
        }
        
        bankAccount = bankAccountRepository.save(bankAccount);
        
        dto.setId(bankAccount.getId());
        dto.setVerified(bankAccount.isVerified());
        
        return dto;
    }
    
    public List<BankAccountDto> getUserBankAccounts(Long userId) {
        List<BankAccount> accounts = bankAccountRepository.findByUserId(userId);
        
        return accounts.stream().map(account -> {
            BankAccountDto dto = new BankAccountDto();
            dto.setId(account.getId());
            dto.setAccountNumber(maskAccountNumber(account.getAccountNumber()));
            dto.setBankName(account.getBankName());
            dto.setAccountHolderName(account.getAccountHolderName());
            dto.setPrimary(account.isPrimary());
            dto.setVerified(account.isVerified());
            return dto;
        }).collect(Collectors.toList());
    }
    
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        int len = accountNumber.length();
        return "*".repeat(len - 4) + accountNumber.substring(len - 4);
    }
    
    public List<DepositTransaction> getUserDepositHistory(Long userId) {
        return depositTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
