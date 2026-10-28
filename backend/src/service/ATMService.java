package service;

import data.InMemoryAccountRepository;
import model.Account;
import model.Transaction;

import java.util.List;
import java.util.UUID;

public class ATMService {

    private InMemoryAccountRepository repository;

    public ATMService(InMemoryAccountRepository repository) {
        this.repository = repository;
    }

    // LOGIN
    public Account login(int accountNumber, String pin) {

        Account account =
                repository.findByAccountNumber(accountNumber);

        if (account != null && account.getPin().equals(pin)) {
            return account;
        }

        return null;
    }

    // CHECK BALANCE
    public double checkBalance(Account account) {
        return account.getBalance();
    }

    // DEPOSIT
    public boolean deposit(Account account, double amount) {

        if (amount <= 0) {
            return false;
        }

        account.credit(amount);

        Transaction transaction = new Transaction(
                generateTransactionId(),
                "DEPOSIT",
                amount,
                account.getBalance()
        );

        account.addTransaction(transaction);

        return true;
    }

    // WITHDRAW
    public boolean withdraw(Account account, double amount) {

        if (amount <= 0) {
            return false;
        }

        if (!account.debit(amount)) {
            return false;
        }

        Transaction transaction = new Transaction(
                generateTransactionId(),
                "WITHDRAW",
                amount,
                account.getBalance()
        );

        account.addTransaction(transaction);

        return true;
    }

    // TRANSFER
    public boolean transfer(
            Account sender,
            int receiverAccountNumber,
            double amount) {

        if (amount <= 0) {
            return false;
        }

        Account receiver =
                repository.findByAccountNumber(receiverAccountNumber);

        if (receiver == null) {
            return false;
        }

        if (!sender.debit(amount)) {
            return false;
        }

        receiver.credit(amount);

        Transaction senderTransaction = new Transaction(
                generateTransactionId(),
                "TRANSFER SENT",
                amount,
                sender.getBalance()
        );

        Transaction receiverTransaction = new Transaction(
                generateTransactionId(),
                "TRANSFER RECEIVED",
                amount,
                receiver.getBalance()
        );

        sender.addTransaction(senderTransaction);
        receiver.addTransaction(receiverTransaction);

        return true;
    }

    // CHANGE PIN
    public boolean changePin(
            Account account,
            String oldPin,
            String newPin) {

        if (!account.getPin().equals(oldPin)) {
            return false;
        }

        if (newPin == null || newPin.length() != 4) {
            return false;
        }

        account.setPin(newPin);

        return true;
    }

    // MINI STATEMENT
    public List<Transaction> getMiniStatement(Account account) {
        return account.getTransactions();
    }

    // GENERATE TRANSACTION ID
    private String generateTransactionId() {

        return UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}