package com.techtack.blue.controller;

import com.techtack.blue.dto.AssetDTO;
import com.techtack.blue.dto.asset.AssetOverviewDTO;
import com.techtack.blue.dto.asset.PortfolioDTO;
import com.techtack.blue.exception.UserException;
import com.techtack.blue.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asset")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @GetMapping("/{userId}/overview")

    public ResponseEntity<AssetOverviewDTO> getAssetOverview(@PathVariable Long userId, @RequestParam String accountNumber) throws UserException {
        AssetOverviewDTO overview = assetService.getAssetOverview(userId, accountNumber);
        return new ResponseEntity<>(overview, HttpStatus.OK);
    }

    @GetMapping("/{userId}/portfolio")
    public ResponseEntity<PortfolioDTO> getPortfolio(@PathVariable Long userId, @RequestParam String accountNumber) throws UserException {
        PortfolioDTO portfolio = assetService.getPortfolio(userId, accountNumber);
        return new ResponseEntity<>(portfolio, HttpStatus.OK);
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<AssetDTO.TransactionResponse> depositFunds(@PathVariable Long userId, @RequestParam String accountNumber, @Valid @RequestBody AssetDTO.DepositRequest request) throws UserException {
        AssetDTO.TransactionResponse response = assetService.processDeposit(userId, accountNumber, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{userId}/deposit/qr")
    public ResponseEntity<AssetDTO.TransactionResponse> generateDepositQRCode(@PathVariable Long userId, @RequestParam String accountNumber, @RequestParam Double amount) throws UserException {
        AssetDTO.TransactionResponse response = assetService.generateDepositQRCode(userId, accountNumber, amount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{userId}/bank-accounts")
    public ResponseEntity<AssetDTO.BankAccount> addBankAccount(@PathVariable Long userId, @Valid @RequestBody AssetDTO.BankAccount bankAccount) throws UserException {
        AssetDTO.BankAccount savedAccount = assetService.addBankAccount(userId, bankAccount);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/bank-accounts")
    public ResponseEntity<List<AssetDTO.BankAccount>> getUserBankAccounts(@PathVariable Long userId) {
        List<AssetDTO.BankAccount> accounts = assetService.getUserBankAccounts(userId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/{userId}/deposit-history")
    public ResponseEntity<List<AssetDTO.TransactionResponse>> getDepositHistory(@PathVariable Long userId) {
        List<AssetDTO.TransactionResponse> history = assetService.getUserDepositHistory(userId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}