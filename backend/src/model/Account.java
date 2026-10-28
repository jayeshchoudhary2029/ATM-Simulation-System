package model;

import java.util.ArrayList;
import java.util.List;

public class Account {

    private int accountNumber;
    private String name;
    private String pin;
    private double balance;

    private List<Transaction> transactions;

    public Account(int accountNumber, String name, String pin, double balance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.pin = pin;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void credit(double amount) {
        balance += amount;
    }

    public boolean debit(double amount) {

        if (amount <= balance) {
            balance -= amount;
            return true;
        }

        return false;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}