package com.bankly.bankly.controller;


import com.bankly.bankly.dto.BillPaymentRequest;
import com.bankly.bankly.dto.DepositRequest;
import com.bankly.bankly.dto.TransferRequest;
import com.bankly.bankly.dto.WithdrawRequest;
import com.bankly.bankly.model.Account;
import com.bankly.bankly.model.Transaction;
import com.bankly.bankly.repository.AccountRepository;
import com.bankly.bankly.repository.TransactionRepository;
import com.bankly.bankly.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Operations related to bank accounts")
public class TransactionController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;


    @PostMapping("/deposit")
    @Operation(summary = "Deposits amount on chosen account account")
    public ResponseEntity<String> deposit(@RequestBody DepositRequest request) {
        Account account = accountService.findByAccountNumber(request.getAccountNumber()).orElse(null);

        if (account == null) {
            return ResponseEntity.badRequest().body("Account not found");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Invalid deposit amount");
        }

        accountService.deposit(account, request.getAmount());
        return ResponseEntity.ok("Deposit successful. New balance: " + account.getBalance());
    }


    @PostMapping("/withdraw")
    @Operation(summary = "Removes amount from source account")
    public ResponseEntity<String> withdraw(@RequestBody WithdrawRequest request) {
        Account account = accountService.findByAccountNumber(request.getAccountNumber()).orElse(null);

        if (account == null) {
            return ResponseEntity.badRequest().body("Account not found");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Invalid withdrawal amount");
        }

        if (!accountService.hasSufficientBalance(account, request.getAmount())) {
            return ResponseEntity.badRequest().body("Insufficient funds");
        }

        accountService.withdraw(account, request.getAmount());
        return ResponseEntity.ok("Withdrawal successful. New balance: " + account.getBalance());
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer amount between two accounts", description = "Withdraws amount from source account and deposits it in destination account")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Invalid transfer amount");
        }

        Account from = accountService.findByAccountNumber(request.getFromAccount()).orElse(null);
        Account to = accountService.findByAccountNumber(request.getToAccount()).orElse(null);

        if (from == null || to == null) {
            return ResponseEntity.badRequest().body("One or both accounts not found");
        }

        if (!accountService.hasSufficientBalance(from, request.getAmount())) {
            return ResponseEntity.badRequest().body("Insufficient funds in source account");
        }

        accountService.withdraw(from, request.getAmount());
        accountService.deposit(to, request.getAmount());

        transactionRepository.save(new Transaction("TRANSFER_OUT", request.getAmount(), from));
        transactionRepository.save(new Transaction("TRANSFER_IN", request.getAmount(), to));

        return ResponseEntity.ok("Transfer successful. New balances:\nFrom: " + from.getBalance() + "\nTo: " + to.getBalance());
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Retrieves the history of the account's transactions")
    public ResponseEntity<?> getTransactions(@PathVariable String accountNumber) {
        Account account = accountService.findByAccountNumber(accountNumber).orElse(null);

        if (account == null) {
            return ResponseEntity.badRequest().body("Account not found");
        }

        return ResponseEntity.ok(transactionRepository.findByAccountOrderByTimestampDescIdDesc(account));
    }

    @PostMapping("/billpayment")
    @Operation(summary = "Bill Payment for another institution", description = "Withdraws the amount from the user's account and logs the institution name and id it was sent to")
    public ResponseEntity<String> billPayment(@RequestBody BillPaymentRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Invalid payment amount");
        }

        Account account = accountService.findByAccountNumber(request.getAccountNumber()).orElse(null);

        if (account == null) {
            return ResponseEntity.badRequest().body("Account not found");
        }

        if (!accountService.hasSufficientBalance(account, request.getAmount())) {
            return ResponseEntity.badRequest().body("Insufficient funds");
        }

        accountService.billPaymentWithdraw(account, request.getAmount());

        transactionRepository.save(new Transaction(
                "BILL_PAYMENT",
                request.getAmount(),
                account,
                request.getInstitutionName(),
                request.getInstitutionId()
        ));

        return ResponseEntity.ok("Payment successful. New balance: " + account.getBalance());
    }



}
