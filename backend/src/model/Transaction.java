package model;

import java.time.LocalDateTime;

public class Transaction {

    private String transactionId;
    private String type;
    private double amount;
    private LocalDateTime dateTime;
    private double balanceAfterTransaction;

    public Transaction(
            String transactionId,
            String type,
            double amount,
            double balanceAfterTransaction) {

        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.dateTime = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }
}