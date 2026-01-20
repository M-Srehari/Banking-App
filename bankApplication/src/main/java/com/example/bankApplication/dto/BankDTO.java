package com.example.bankApplication.dto;

public class BankDTO {

    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private String otp;
    private String bankName;

    // No-arg constructor
    public BankDTO() {
    }

    // All-arg constructor
    public BankDTO(String name, String email, String password,
                   String confirmPassword, String otp, String bankName) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.otp = otp;
        this.bankName = bankName;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}