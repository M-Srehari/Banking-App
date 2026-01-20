package com.example.bankApplication.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.bankApplication.Entity.TransactionEntity;

public interface TransactionHistoryService {
    List<TransactionEntity> getTransactions(String accountNumber);
	
	    List<TransactionEntity> getTransactionsBetweenDates(
	            String accountNumber,
	            LocalDateTime fromDate,
	            LocalDateTime toDate
	    );
}
