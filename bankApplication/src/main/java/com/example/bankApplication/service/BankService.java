package com.example.bankApplication.service;

import java.util.Optional;

import com.example.bankApplication.dto.BankDTO;
import com.example.bankApplication.dto.TransactionDTO;
import com.example.bankApplication.dto.UserResponseDTO;

public interface BankService {

    String saveUser(BankDTO dto);

    String validateOtp(BankDTO dto);

	void sendOtp(String email, String otp);
	
	UserResponseDTO getUserData(Optional<Long> id, Optional<String> email);

	String login(BankDTO dto);

	UserResponseDTO getUserData(Long id, String email);
	
	String depositMoney(TransactionDTO transaction);

    String sendMoney(TransactionDTO transaction);

	String logins(BankDTO dto);

}