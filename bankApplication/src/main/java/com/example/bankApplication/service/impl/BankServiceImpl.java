package com.example.bankApplication.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.bankApplication.Entity.BankEntity;
import com.example.bankApplication.Entity.TransactionEntity;
import com.example.bankApplication.dto.BankDTO;
import com.example.bankApplication.dto.TransactionDTO;
import com.example.bankApplication.dto.UserResponseDTO;
import com.example.bankApplication.repository.BankRepository;
import com.example.bankApplication.repository.TransactionRepository;
import com.example.bankApplication.service.BankService;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TransactionRepository transactionRepository;

    private static class Otp {
        private static final SecureRandom random = new SecureRandom();

        public static String generateOtp() {
            return IntStream.range(0, 6)
                    .map(i -> random.nextInt(10))
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining());
        }
    }

    @Override
    public String login(BankDTO dto) {
        try {
            Optional<String> error = Stream.of(
                    validate(dto.getEmail(), "Email"),
                    validate(dto.getPassword(), "Password")
            ).filter(Optional::isPresent)
             .map(Optional::get)
             .findFirst();

            if (error.isPresent()) return error.get();

            BankEntity user = bankRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getPassword().equals(dto.getPassword())) {
                return "Invalid credentials";
            }

            String otp = Otp.generateOtp();
            user.setOtp(otp);
            user.setOtpResendCount(0);   
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
            bankRepository.save(user);

            // Send OTP email
            sendOtp(user.getEmail(), otp);

            return "OTP sent to registered email";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during login: " + e.getMessage();
        }
    }
    
    public String resendOtp(String email) {

        BankEntity user = bankRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtpResendCount() >= 1) {
            return "Resend OTP limit exceeded";
        }

        String otp = Otp.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        user.setOtpResendCount(user.getOtpResendCount() + 1);

        bankRepository.save(user);
        sendOtp(user.getEmail(), otp);

        return "OTP resent successfully";
    }

    @Override
    public String validateOtp(BankDTO dto) {
        if (dto.getEmail() == null || dto.getOtp() == null) {
            return "Email and OTP are required";
        }

        BankEntity user = bankRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return "OTP expired, please request a new one";
        }

        if (!dto.getOtp().trim().equals(user.getOtp())) {
            return "Invalid OTP";
        }

        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setOtpResendCount(0);
        bankRepository.save(user);

        return "Login successful";
    }

    public void sendOtp(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP for Login");
        message.setText("Your OTP is: " + otp + "\nIt will expire in 5 minutes.");
        mailSender.send(message);
    }

    @Override
    public String saveUser(BankDTO dto) {
        try {
            Optional<String> error = Stream.of(
                    validate(dto.getName(), "Name"),
                    validate(dto.getEmail(), "Email"),
                    validate(dto.getPassword(), "Password"),
                    validate(dto.getBankName(), "Bank Name"),
                    validatePasswords(dto.getPassword(), dto.getConfirmPassword()),
                    validateEmailFormat(dto.getEmail())
            ).filter(Optional::isPresent)
             .map(Optional::get)
             .findFirst();

            if (error.isPresent()) return error.get();

            if (bankRepository.findByEmail(dto.getEmail()).isPresent()) {
                return "User already exists";
            }

            BankBranch bank = BankBranch.fromBankName(dto.getBankName());

            BankEntity user = new BankEntity();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());
            user.setBankName(dto.getBankName());
            user.setBranchCode(bank.getBranchCode());

            BankEntity savedUser = bankRepository.save(user);

            String accountNumber = bank.getBankCode() + "/" +
                                   bank.getBranchCode() + "/" +
                                   String.format("%04d", savedUser.getId());
            savedUser.setAccountNumber(accountNumber);
            bankRepository.save(savedUser);

            return "Account created successfully. Account No: " + accountNumber;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error saving user: " + e.getMessage();
        }
    }

    @Override
    public String depositMoney(TransactionDTO transaction) {
        try {
            BankEntity user = bankRepository.findByAccountNumber(transaction.getAccountNumber())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if (!user.getPassword().equals(transaction.getPassword())) return "Wrong password";
            if (transaction.getAmount() <= 0) return "Invalid deposit amount";

            double newBalance = user.getBalance() + transaction.getAmount();
            user.setBalance(newBalance);
            bankRepository.save(user);

            transactionRepository.save(new TransactionEntity(
                    null,
                    user.getAccountNumber(),
                    user.getAccountNumber(),
                    transaction.getAmount(),
                    "CREDIT",
                    LocalDateTime.now(),
                    newBalance
            ));

            return "Deposit successful. New balance: " + newBalance;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during deposit: " + e.getMessage();
        }
    }

    @Override
    public String sendMoney(TransactionDTO transaction) {
        try {
            BankEntity sender = bankRepository.findByAccountNumber(transaction.getFromAccount())
                    .orElseThrow(() -> new RuntimeException("Sender account not found"));

            if (!sender.getPassword().equals(transaction.getPassword())) return "Wrong password";
            if (transaction.getAmount() <= 0) return "Invalid amount";
            if (sender.getBalance() < transaction.getAmount()) return "Insufficient balance";

            BankEntity recipient = bankRepository.findByAccountNumber(transaction.getToAccount())
                    .orElseThrow(() -> new RuntimeException("Recipient account not found"));

            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());

            bankRepository.save(sender);
            bankRepository.save(recipient);

            transactionRepository.save(new TransactionEntity(
                    null,
                    sender.getAccountNumber(),
                    recipient.getAccountNumber(),
                    transaction.getAmount(),
                    "DEBIT",
                    LocalDateTime.now(),
                    sender.getBalance()
            ));

            transactionRepository.save(new TransactionEntity(
                    null,
                    sender.getAccountNumber(),
                    recipient.getAccountNumber(),
                    transaction.getAmount(),
                    "CREDIT",
                    LocalDateTime.now(),
                    recipient.getBalance()
            ));

            return "Transaction successful";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public UserResponseDTO getUserData(Optional<Long> id, Optional<String> email) {
        try {
            BankEntity user = id.map(bankRepository::findById)
                    .orElse(email.map(bankRepository::findByEmail)
                    .orElseThrow(() -> new RuntimeException("Either id or email must be provided")))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return new UserResponseDTO(
                    user.getName(),
                    user.getEmail(),
                    user.getAccountNumber(),
                    user.getBankName(),
                    user.getBranchCode(),
                    user.getBalance()
            );

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching user: " + e.getMessage());
        }
    }

    private Optional<String> validate(String value, String field) {
        return (value == null || value.trim().isEmpty()) ? Optional.of(field + " should not be empty") : Optional.empty();
    }

    private Optional<String> validateEmailFormat(String email) {
        if (email != null && !email.contains("@")) return Optional.of("Invalid Email");
        return Optional.empty();
    }

    private Optional<String> validatePasswords(String password, String confirmPassword) {
        if (password != null && !password.equals(confirmPassword)) return Optional.of("Passwords do not match");
        return Optional.empty();
    }

    private String generateAccountNumber(String bankName) {
        BankBranch bank = BankBranch.fromBankName(bankName);
        long count = bankRepository.countByBankName(bankName);
        long sequence = count + 1;
        return bank.getBankCode() + "/" + bank.getBranchCode() + "/" + String.format("%04d", sequence);
    }

    public enum BankBranch {
        KOTAK("KTK", "0010"),
        INDIAN("IND", "0011");

        private final String bankCode;
        private final String branchCode;

        BankBranch(String bankCode, String branchCode) {
            this.bankCode = bankCode;
            this.branchCode = branchCode;
        }

        public String getBankCode() { return bankCode; }
        public String getBranchCode() { return branchCode; }

        public static BankBranch fromBankName(String name) {
            return BankBranch.valueOf(name.toUpperCase());
        }
    }
}
