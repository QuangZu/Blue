package com.techtack.blue.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techtack.blue.config.JwtProvider;
import com.techtack.blue.exception.UserException;
import com.techtack.blue.model.SecureToken;
import com.techtack.blue.model.User;
import com.techtack.blue.model.TradingAccount;
import com.techtack.blue.repository.UserRepository;
import com.techtack.blue.repository.TradingAccountRepository;
import com.techtack.blue.response.AuthResponse;
import com.techtack.blue.service.CustomUserDetailsServiceImplementation;
import com.techtack.blue.service.EmailService;
import com.techtack.blue.service.SecureTokenService;
import com.techtack.blue.service.UserService;
import com.techtack.blue.service.AssetService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetailsServiceImplementation customUserDetails;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AssetService assetService;
    
    @Autowired
    private TradingAccountRepository tradingAccountRepository;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody User user) throws UserException {
        
        Optional<User> isEmailExist = userRepository.findByEmail(user.getEmail());
        
        if (isEmailExist.isPresent()) {
            throw new UserException("Email is already registered");
        }
        
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setPhone_number(user.getPhone_number());
        newUser.setAccountBalance(0.0);
    
        newUser.setVerified(false);
        newUser.setVerificationStartTime(LocalDateTime.now());
        newUser.setVerificationEndTime(LocalDateTime.now().plusDays(1));
    
        User savedUser = userRepository.save(newUser);
    
        SecureToken secureToken = secureTokenService.createSecureToken(savedUser);
        assetService.initializeUserAccounts(savedUser);
    
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getUsername(), secureToken.getToken());
        
        AuthResponse authResponse = new AuthResponse();
        authResponse.setMessage("Registration successful. Please check your email to verify your account.");
        
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@Valid @RequestParam String token) {
        SecureToken secureToken = secureTokenService.findByToken(token);
        
        if (secureToken == null) {
            return ResponseEntity.badRequest().body("Invalid verification token");
        }
        
        if (!secureTokenService.isTokenValid(secureToken)) {
            return ResponseEntity.badRequest().body("Verification token has expired");
        }
        
        User user = secureToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        secureTokenService.removeToken(secureToken);

        return ResponseEntity.ok("Email verified successfully. You can now login.");
    }
    
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody User user) {
        String email = user.getEmail();
        String password = user.getPassword();
        
        try {
            Authentication authentication = authenticate(email, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String token = jwtProvider.generateToken(authentication);
            
            User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found with email: " + email));
            loggedInUser.setToken(token);
            userRepository.save(loggedInUser);
            
            // Get user's trading accounts
            List<TradingAccount> accounts = tradingAccountRepository.findByUserIdAndIsActiveTrue(loggedInUser.getId());
            
            // Initialize accounts if user has none
            if (accounts.isEmpty()) {
                assetService.initializeUserAccounts(loggedInUser);
                accounts = tradingAccountRepository.findByUserIdAndIsActiveTrue(loggedInUser.getId());
            }
            
            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(token);
            authResponse.setId(loggedInUser.getId());
            authResponse.setEmail(loggedInUser.getEmail());
            authResponse.setUsername(loggedInUser.getUsername());
            authResponse.setAccountBalance(loggedInUser.getAccountBalance());
            authResponse.setBuyingPower(loggedInUser.getBuyingPower());
            authResponse.setVerified(loggedInUser.isVerified());
            authResponse.setPhoneNumber(loggedInUser.getPhone_number());
            
            // Add primary account info
            accounts.stream()
                .filter(TradingAccount::isPrimary)
                .findFirst()
                .ifPresent(account -> {
                    authResponse.setPrimaryAccountNumber(account.getAccountNumber());
                    authResponse.setPrimaryAccountType(account.getAccountType().name());
                });
            
            if (!loggedInUser.isVerified()) {
                authResponse.setMessage("Login Success. Please verify your email to access all features.");
            } else {
                authResponse.setMessage("Login Success");
            }
            
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Wrong account or password");
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<AuthResponse> changePassword(@Valid @RequestBody Map<String, String> request) throws UserException {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        
        if (email == null || newPassword == null) {
            throw new UserException("Email and new password are required");
        }
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException("User not found with email: " + email));
        
        if (!user.isReq_user()) {
            throw new UserException("Please request password reset first");
        }
        
        if (userService.isPasswordMatching(newPassword, user)) {
            throw new UserException("New password must be different from the current password");
        }
        
        SecureToken verificationToken = secureTokenService.createSecureToken(user);
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerified(false);
        userRepository.save(user);
        
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), verificationToken.getToken());
        
        AuthResponse response = new AuthResponse();
        response.setMessage("Password change verification email sent. Please check your email.");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@Valid @RequestBody Map<String, String> request) throws UserException {
        String email = request.get("email");
    
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException("User not found with email: " + email));
        
        user.setReq_user(true);
        userRepository.save(user);
    
        SecureToken resetToken = secureTokenService.createSecureToken(user);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetToken.getToken());
    
        AuthResponse response = new AuthResponse();
        response.setMessage("Password reset email sent successfully");
    
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/verify-password-change")
    public ResponseEntity<?> verifyPasswordChange(@Valid @RequestParam String token) {
        SecureToken secureToken = secureTokenService.findByToken(token);
        
        if (secureToken == null) {
            return ResponseEntity.badRequest().body("Invalid verification token");
        }
        
        if (!secureTokenService.isTokenValid(secureToken)) {
            return ResponseEntity.badRequest().body("Verification token has expired");
        }
        
        User user = secureToken.getUser();
        user.setVerified(true);
        user.setReq_user(false);
        userRepository.save(user);
        
        secureTokenService.removeToken(secureToken);
        
        return ResponseEntity.ok("Password changed successfully. You can now login with your new password.");
    }
    
    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);
        
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username");
        }
        
        // Use UserService for password comparison
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new BadCredentialsException("Invalid username"));
        if (!userService.isPasswordMatching(password, user)) {
            throw new BadCredentialsException("Invalid password");
        }
        
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // Extract email from JWT token
            String email = jwtProvider.getEmailFromToken(token);
            
            // Get user from database
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found"));
            
            // Get user's trading accounts
            List<TradingAccount> accounts = tradingAccountRepository.findByUserIdAndIsActiveTrue(user.getId());
            
            // Initialize accounts if user has none
            if (accounts.isEmpty()) {
                assetService.initializeUserAccounts(user);
                accounts = tradingAccountRepository.findByUserIdAndIsActiveTrue(user.getId());
            }
            
            // Build profile response
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("id", user.getId());
            profileData.put("email", user.getEmail());
            profileData.put("username", user.getUsername());
            profileData.put("phoneNumber", user.getPhone_number());
            profileData.put("address", user.getAddress());
            profileData.put("dateOfBirth", user.getDate_of_birth());
            profileData.put("identificationCard", user.getIdentification_card());
            profileData.put("gender", user.getGender());
            profileData.put("nationality", user.getNationality());
            profileData.put("accountBalance", user.getAccountBalance());
            profileData.put("buyingPower", user.getBuyingPower());
            profileData.put("verified", user.isVerified());
            
            // Add trading accounts info
            Map<String, Object> accountsInfo = new HashMap<>();
            for (TradingAccount account : accounts) {
                Map<String, Object> accountData = new HashMap<>();
                accountData.put("accountNumber", account.getAccountNumber());
                accountData.put("accountType", account.getAccountType().name());
                accountData.put("totalNAV", account.getTotalNAV());
                accountData.put("cashBalance", account.getCashBalance());
                accountData.put("buyingPower", account.getBuyingPower());
                accountData.put("isPrimary", account.isPrimary());
                accountsInfo.put(account.getAccountType().name(), accountData);
            }
            profileData.put("tradingAccounts", accountsInfo);
            
            // Find primary account
            accounts.stream()
                .filter(TradingAccount::isPrimary)
                .findFirst()
                .ifPresent(account -> {
                    profileData.put("primaryAccountNumber", account.getAccountNumber());
                    profileData.put("primaryAccountType", account.getAccountType().name());
                });
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", profileData);
            
            return ResponseEntity.ok(response);
            
        } catch (UserException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to retrieve user profile");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
