package com.example.bankApplication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bankApplication.Entity.BankEntity;

public interface BankRepository extends JpaRepository<BankEntity, Long> {
    
    Optional<BankEntity> findByEmail(String email);
    
    Optional<BankEntity> findByEmailIgnoreCase(String email);
    
    @Query("SELECT COUNT(b) FROM BankEntity b WHERE b.bankName = :bankName")
    long countByBankName(@Param("bankName") String bankName);

    Optional<BankEntity> findByAccountNumber(String accountNumber); 
    
}
