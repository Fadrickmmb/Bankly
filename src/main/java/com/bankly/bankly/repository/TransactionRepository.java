package com.bankly.bankly.repository;

import com.bankly.bankly.model.Account;
import com.bankly.bankly.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountOrderByTimestampDescIdDesc(Account account);

}
