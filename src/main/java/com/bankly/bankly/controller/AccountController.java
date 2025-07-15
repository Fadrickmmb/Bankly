package com.bankly.bankly.controller;

import com.bankly.bankly.model.Account;
import com.bankly.bankly.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Operations related to bank accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Operation(summary = "Creates new Bank Account")
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account accountRequest){
        accountRequest.setAccountNumber(UUID.randomUUID().toString());
        if (accountRequest.getBalance() == null){
            accountRequest.setBalance(BigDecimal.ZERO);
        }

        Account saved = accountRepository.save(accountRequest);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Lists all bank accounts")
    @GetMapping
    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    @Operation(summary = "Retrieves Bank Account by Id")
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id){
        return accountRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
