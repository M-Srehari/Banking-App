package com.example.bankApplication.dto;

public class UserData {

    private String name;
    private String email;
    private String accountNumber;
    private String bankName;
    private String branchCode;
    private Double balance;

    // No-arg constructor
    public UserData() {
    }

    // All-arg constructor
    public UserData(String name, String email, String accountNumber,
                    String bankName, String branchCode, Double balance) {
        this.name = name;
        this.email = email;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.branchCode = branchCode;
        this.balance = balance;
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

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
