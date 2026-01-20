package com.example.bankApplication.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bankApplication.Entity.TransactionEntity;
import com.example.bankApplication.repository.TransactionRepository;
import com.example.bankApplication.service.TransactionHistoryService;

@Service
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<TransactionEntity> getTransactions(String accountNumber) {
        return transactionRepository
                .findByFromAccountOrToAccount(accountNumber, accountNumber);
    }
    
    @Override
    public List<TransactionEntity> getTransactionsBetweenDates(
            String accountNumber,
            LocalDateTime fromDate,
            LocalDateTime toDate) {

        return transactionRepository.findTransactionsBetweenDates(
                accountNumber,
                fromDate,
                toDate
        );
    }
}
