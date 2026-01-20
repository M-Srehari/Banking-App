package com.example.bankApplication.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bankApplication.Entity.TransactionEntity;

public interface TransactionRepository
        extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByFromAccountOrToAccount(
            String fromAccount,
            String toAccount
    );
        @Query("""
            SELECT t FROM TransactionEntity t
            WHERE t.fromAccount = :accountNumber
            AND t.transactionDate BETWEEN :fromDate AND :toDate
        """)
        List<TransactionEntity> findTransactionsBetweenDates(
                @Param("accountNumber") String accountNumber,
                @Param("fromDate") LocalDateTime fromDate,
                @Param("toDate") LocalDateTime toDate
        );
   }
