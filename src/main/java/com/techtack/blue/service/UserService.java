package com.techtack.blue.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techtack.blue.config.JwtProvider;
import com.techtack.blue.exception.UserException;
import com.techtack.blue.model.User;
import com.techtack.blue.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final HttpServletRequest request;
    
    // Single constructor injection - removed duplicate @Autowired fields
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      JwtProvider jwtProvider, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.request = request;
    }
    
    public boolean isPasswordMatching(String password, User user) {
        // Simplified validation - removed excessive Optional wrapping
        if (user == null || password == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    public User findUserProfileByJwt(String jwt) throws UserException {
        User user = (User) request.getAttribute("currentUser");
        if (user != null) {
            return user;
        }

        if (jwt == null) {
            throw new UserException("User not authenticated");
        }
        
        String email = jwtProvider.getEmailFromToken(jwt);
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException("User not found with email: " + email));
    }

    public User findUserById(Long userId) throws UserException {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserException("User not found with id: " + userId));
    }
    
    public User findUserByEmail(String email) throws UserException {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException("User not found with email: " + email));
    }
    
    public User updateUser(User user, Long userId) throws UserException {
        User userToUpdate = findUserById(userId); // Reuse existing method
        
        // Consolidated validation logic
        validateUniqueFields(user, userToUpdate, userId);
        
        // Update fields
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setPhone_number(user.getPhone_number());
        userToUpdate.setAddress(user.getAddress());
        userToUpdate.setIdentification_card(user.getIdentification_card());
        
        return userRepository.save(userToUpdate);
    }
    
    // Extracted validation logic to reduce duplication
    private void validateUniqueFields(User newUser, User existingUser, Long userId) throws UserException {
        // Username validation
        if (!existingUser.getUsername().equals(newUser.getUsername())) {
            if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
                throw new UserException("Username already exists");
            }
        }
        
        // Email validation
        if (!existingUser.getEmail().equals(newUser.getEmail())) {
            Optional<User> existingUserOpt = userRepository.findByEmail(newUser.getEmail());
            if (existingUserOpt.isPresent() && !existingUserOpt.get().getId().equals(userId)) {
                throw new UserException("User with email already exists");
            }
        }
        
        // Phone validation
        if (!existingUser.getPhone_number().equals(newUser.getPhone_number())) {
            Optional<User> existingUserOpt = userRepository.findByPhoneNumber(newUser.getPhone_number());
            if (existingUserOpt.isPresent() && !existingUserOpt.get().getId().equals(userId)) {
                throw new UserException("User with mobile number already exists");
            }
        }
    }

    public boolean deleteUser(Long userId) throws UserException {
        findUserById(userId);
        userRepository.deleteById(userId);
        return true;
    }
}
