package data;

import java.util.ArrayList;
import java.util.List;

import model.Account;

public class InMemoryAccountRepository {

    private List<Account> accounts;

    public InMemoryAccountRepository() {

        accounts = new ArrayList<>();

        // Demo Account 1
        accounts.add(
            new Account(
                10001,
                "Jayesh",
                "1234",
                25000
            )
        );

        // Demo Account 2
        accounts.add(
            new Account(
                10002,
                "Omkar",
                "2345",
                30000
            )
        );

        // Demo Account 3
        accounts.add(
            new Account(
                10003,
                "Ameya",
                "3456",
                15000
            )
        );
    }

    public Account findByAccountNumber(int accountNumber) {

        for (Account account : accounts) {

            if (account.getAccountNumber() == accountNumber) {
                return account;
            }
        }

        return null;
    }

    public List<Account> getAllAccounts() {
        return accounts;
    }
}