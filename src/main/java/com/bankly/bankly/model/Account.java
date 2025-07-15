package com.bankly.bankly.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "acccounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerName;

    @Column(unique = true)
    private String accountNumber;

    private BigDecimal balance;

    public Account(){}

    public Account(String ownerName, String accountNumber, BigDecimal balance) {
        this.ownerName = ownerName;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public long getId(){
        return id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
