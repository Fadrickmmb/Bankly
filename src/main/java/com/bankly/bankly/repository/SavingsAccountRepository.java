package com.bankly.bankly.repository;

import com.bankly.bankly.model.Account;
import com.bankly.bankly.model.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount,Long> {
    Optional<SavingsAccount> findByMainAccount(Account account);
}
