package com.example.bankApplication.dto;

public class TransactionDTO {

    private String accountNumber;
    private Double amount;
    private String fromAccount;
    private String toAccount;
    private String password;

    // No-arg constructor
    public TransactionDTO() {
    }

    // All-arg constructor
    public TransactionDTO(String accountNumber, Double amount,
                          String fromAccount, String toAccount, String password) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.password = password;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
