package com.bankly.bankly.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private BigDecimal amount;

    private BigDecimal snapshotBalance;

    private LocalDateTime timestamp = LocalDateTime.now();

    private Long institutionId;
    private String institutionName;


    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Transaction() {}

    public Transaction(String type, BigDecimal amount, Account account ) {
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.snapshotBalance = account.getBalance();
    }

    //Bill Payment
    public Transaction(String type, BigDecimal amount, Account account, String institutionName, Long institutionId) {
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.snapshotBalance = account.getBalance();
        this.institutionName = institutionName;
        this.institutionId = institutionId;
    }


    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getSnapshotBalance() {
        return snapshotBalance;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }
}
