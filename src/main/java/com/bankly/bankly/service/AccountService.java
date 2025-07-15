package com.bankly.bankly.service;

import com.bankly.bankly.model.Account;
import com.bankly.bankly.model.Transaction;
import com.bankly.bankly.repository.AccountRepository;
import com.bankly.bankly.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public boolean hasSufficientBalance(Account account, BigDecimal amount) {
        return account.getBalance().compareTo(amount) >= 0;
    }

    public Account deposit(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        Account updated = accountRepository.save(account);
        transactionRepository.save(new Transaction("DEPOSIT", amount, updated));
        return updated;
    }


    public Account withdraw(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().subtract(amount));
        Account updated = accountRepository.save(account);
        transactionRepository.save(new Transaction("WITHDRAW", amount, updated));
        return updated;
    }

    public Account billPaymentWithdraw(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }



}
