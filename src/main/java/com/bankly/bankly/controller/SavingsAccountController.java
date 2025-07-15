package com.bankly.bankly.controller;


import com.bankly.bankly.dto.AccountToSavingsTransferRequest;
import com.bankly.bankly.model.Account;
import com.bankly.bankly.model.SavingsAccount;
import com.bankly.bankly.repository.AccountRepository;
import com.bankly.bankly.repository.SavingsAccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/savings")
@Tag(name = "Savings Accounts", description = "Operations related to savings accounts")
public class SavingsAccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SavingsAccountRepository savingsRepository;

    @Operation(summary = "Creates new Savings Account")
    @PostMapping("/create/{accountId}")
    public ResponseEntity<?> createSavingsAccount(@PathVariable Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isEmpty()){
            return ResponseEntity.badRequest().body("Account not found");
        }

        Account account = accountOpt.get();

        if (savingsRepository.findByMainAccount(account).isPresent()){
            return ResponseEntity.badRequest().body("This account has reached the maximum number of savings accounts.");
        }

        SavingsAccount savings = new SavingsAccount(account);
        savingsRepository.save(savings);
        return ResponseEntity.ok().body(savings);
    }

    @Operation(summary = "Transfer from main account to savings")
    @PostMapping("/deposit")
    public ResponseEntity<?> depositToSavings(@RequestBody AccountToSavingsTransferRequest request) {
        Optional<Account> accountOpt = accountRepository.findById(request.getAccountId());

        if (accountOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Account not found.");
        }

        Account account = accountOpt.get();

        Optional<SavingsAccount> savingsOpt = savingsRepository.findByMainAccount(account);
        if (savingsOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Savings account not found.");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            return ResponseEntity.badRequest().body("Insufficient funds in main account.");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        SavingsAccount savings = savingsOpt.get();
        savings.setBalance(savings.getBalance().add(request.getAmount()));

        accountRepository.save(account);
        savingsRepository.save(savings);

        return ResponseEntity.ok("Transfer successful.");
    }

    @Operation(summary = "Transfer from savings to main account")
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawFromSavings(@RequestBody AccountToSavingsTransferRequest request) {
        Optional<Account> accountOpt = accountRepository.findById(request.getAccountId());

        if (accountOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Account not found.");
        }

        Account account = accountOpt.get();

        Optional<SavingsAccount> savingsOpt = savingsRepository.findByMainAccount(account);
        if (savingsOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Savings account not found.");
        }

        SavingsAccount savings = savingsOpt.get();
        if (savings.getBalance().compareTo(request.getAmount()) < 0) {
            return ResponseEntity.badRequest().body("Insufficient funds in savings account.");
        }

        savings.setBalance(savings.getBalance().subtract(request.getAmount()));
        account.setBalance(account.getBalance().add(request.getAmount()));

        savingsRepository.save(savings);
        accountRepository.save(account);

        return ResponseEntity.ok("Withdrawal successful.");
    }

}
