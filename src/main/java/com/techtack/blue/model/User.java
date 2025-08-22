package com.techtack.blue.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String address;
    private String email;
    private String password;
    private String phone_number;
    private String date_of_birth;
    private String identification_card;
    private String gender;
    private String nationality;
    private double accountBalance;
    private boolean req_user;
    private boolean verified;
    private LocalDateTime verificationStartTime;
    private LocalDateTime verificationEndTime;
    private String token;
    private double buyingPower = 0.0;

    public User() {
    }

    public User(Long id, String username, String address, String email, String password, String phone_number, String date_of_birth, String identification_card, String gender, String nationality, double accountBalance, boolean req_user, boolean verified, LocalDateTime verificationStartTime, LocalDateTime verificationEndTime, String token, double buyingPower) {
        this.id = id;
        this.username = username;
        this.address = address;
        this.email = email;
        this.password = password;
        this.phone_number = phone_number;
        this.date_of_birth = date_of_birth;
        this.identification_card = identification_card;
        this.gender = gender;
        this.nationality = nationality;
        this.accountBalance = accountBalance;
        this.req_user = req_user;
        this.verified = verified;
        this.verificationStartTime = verificationStartTime;
        this.verificationEndTime = verificationEndTime;
        this.token = token;
        this.buyingPower = buyingPower;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getIdentification_card() {
        return identification_card;
    }

    public void setIdentification_card(String identification_card) {
        this.identification_card = identification_card;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public boolean isReq_user() {
        return req_user;
    }

    public void setReq_user(boolean req_user) {
        this.req_user = req_user;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public LocalDateTime getVerificationStartTime() {
        return verificationStartTime;
    }

    public void setVerificationStartTime(LocalDateTime verificationStartTime) {
        this.verificationStartTime = verificationStartTime;
    }

    public LocalDateTime getVerificationEndTime() {
        return verificationEndTime;
    }

    public void setVerificationEndTime(LocalDateTime verificationEndTime) {
        this.verificationEndTime = verificationEndTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getBuyingPower() {
        return buyingPower;
    }

    public void setBuyingPower(double buyingPower) {
        this.buyingPower = buyingPower;
    }
}
