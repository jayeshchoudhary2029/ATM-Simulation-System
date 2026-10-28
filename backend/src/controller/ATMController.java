package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import data.InMemoryAccountRepository;
import model.Account;
import service.ATMService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ATMController implements HttpHandler {

    private ATMService atmService;
    private InMemoryAccountRepository repository;

    public ATMController(
            ATMService atmService,
            InMemoryAccountRepository repository) {

        this.atmService = atmService;
        this.repository = repository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        exchange.getResponseHeaders()
        .set("Access-Control-Allow-Origin", "*");

        exchange.getResponseHeaders()
                .set("Access-Control-Allow-Methods",
                        "GET, POST, OPTIONS");

        exchange.getResponseHeaders()
                .set("Access-Control-Allow-Headers",
                        "Content-Type");


        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);

            exchange.close();

            return;
        }

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/api/login")) {

            handleLogin(exchange);

        } else if (path.equals("/api/balance")) {

            handleBalance(exchange);

        } else if (path.equals("/api/withdraw")) {

            handleWithdraw(exchange);

        } else if (path.equals("/api/deposit")) {

            handleDeposit(exchange);

        } else if (path.equals("/api/transfer")) {

            handleTransfer(exchange);

        } else if (path.equals("/api/statement")) {

            handleStatement(exchange);

        } else if (path.equals("/api/change-pin")) {

            handleChangePin(exchange);

        } else {
            sendResponse(
                    exchange,
                    404,
                    "{\"success\":false,\"message\":\"Endpoint not found\"}"
            );
        }
    }

    private void handleLogin(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"success\":false,\"message\":\"POST method required\"}"
            );

            return;
        }

        String requestBody =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        System.out.println(
                "Login request received: " + requestBody
        );

        int accountNumber =
                extractAccountNumber(requestBody);

        String pin =
                extractPin(requestBody);

        Account account =
                atmService.login(accountNumber, pin);

        if (account != null) {

            String response =
                    "{"
                    + "\"success\":true,"
                    + "\"message\":\"Login successful\","
                    + "\"name\":\""
                    + account.getName()
                    + "\""
                    + "}";

            sendResponse(exchange, 200, response);

        } else {

            String response =
                    "{"
                    + "\"success\":false,"
                    + "\"message\":\"Invalid account number or PIN\""
                    + "}";

            sendResponse(exchange, 401, response);
        }
    }

    private void handleBalance(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"success\":false,\"message\":\"GET method required\"}"
            );

            return;
        }

        String query =
                exchange.getRequestURI()
                        .getQuery();

        if (query == null) {
            sendResponse(
                    exchange,
                    400,
                    "{\"success\":false,\"message\":\"Account number required\"}"
            );
            return;
        }

        int accountNumber =
                extractAccountNumberFromQuery(query);

        Account account =
                repository.findByAccountNumber(
                        accountNumber
                );

        if (account == null) {
            sendResponse(
                    exchange,
                    404,
                    "{\"success\":false,\"message\":\"Account not found\"}"
            );
            return;
        }

        double balance =
                atmService.checkBalance(account);

        String response =
                "{"
                + "\"success\":true,"
                + "\"accountNumber\":"
                + account.getAccountNumber()
                + ","
                + "\"balance\":"
                + balance
                + ","
                + "\"name\":\""
                + account.getName()
                + "\""
                + "}";

        sendResponse(
                exchange,
                200,
                response
        );
    }  

    private void handleWithdraw(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"success\":false,\"message\":\"POST method required\"}"
            );

            return;
        }

        String requestBody =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        System.out.println(
                "Withdraw request received: " + requestBody
        );

        int accountNumber =
                extractAccountNumber(requestBody);

        double amount =
                extractAmount(requestBody);

        Account account =
                repository.findByAccountNumber(
                        accountNumber
                );

        if (account == null) {

            sendResponse(
                    exchange,
                    404,
                    "{\"success\":false,\"message\":\"Account not found\"}"
            );

            return;
        }

        boolean success =
                atmService.withdraw(
                        account,
                        amount
                );

        if (!success) {

            sendResponse(
                    exchange,
                    400,
                    "{\"success\":false,\"message\":\"Insufficient balance or invalid amount\"}"
            );

            return;
        }

        String response =
                "{"
                + "\"success\":true,"
                + "\"message\":\"Withdrawal successful\","
                + "\"amount\":"
                + amount
                + ","
                + "\"balance\":"
                + account.getBalance()
                + "}"
                ;

        sendResponse(
                exchange,
                200,
                response
        );
    }
    
    private void handleDeposit(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"success\":false,\"message\":\"POST method required\"}"
            );

            return;
        }

        String requestBody =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        System.out.println(
                "Deposit request received: " + requestBody
        );

        int accountNumber =
                extractAccountNumber(requestBody);

        double amount =
                extractAmount(requestBody);

        Account account =
                repository.findByAccountNumber(
                        accountNumber
                );

        if (account == null) {

            sendResponse(
                    exchange,
                    404,
                    "{\"success\":false,\"message\":\"Account not found\"}"
            );

            return;
        }

        boolean success =
                atmService.deposit(
                        account,
                        amount
                );

        if (!success) {

            sendResponse(
                    exchange,
                    400,
                    "{\"success\":false,\"message\":\"Invalid deposit amount\"}"
            );

            return;
        }

        String response =
                "{"
                + "\"success\":true,"
                + "\"message\":\"Deposit successful\","
                + "\"amount\":"
                + amount
                + ","
                + "\"balance\":"
                + account.getBalance()
                + "}";

        sendResponse(
                exchange,
                200,
                response
        );
    }

    private void handleTransfer(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"success\":false,\"message\":\"POST method required\"}"
            );

            return;
        }

        String requestBody =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        System.out.println(
                "Transfer request received: " + requestBody
        );

        int senderAccountNumber =
                extractAccountNumber(requestBody);

        int receiverAccountNumber =
                extractReceiverAccountNumber(requestBody);

        double amount =
                extractAmount(requestBody);

        Account sender =
                repository.findByAccountNumber(
                        senderAccountNumber
                );

        if (sender == null) {

            sendResponse(
                    exchange,
                    404,
                    "{\"success\":false,\"message\":\"Sender account not found\"}"
            );

            return;
        }

        boolean success =
                atmService.transfer(
                        sender,
                        receiverAccountNumber,
                        amount
                );

        if (!success) {

            sendResponse(
                    exchange,
                    400,
                    "{\"success\":false,\"message\":\"Transfer failed. Check receiver account, amount, or balance.\"}"
            );

            return;
        }

        String response =
                "{"
                + "\"success\":true,"
                + "\"message\":\"Transfer successful\","
                + "\"amount\":"
                + amount
                + ","
                + "\"balance\":"
                + sender.getBalance()
                + "}";

        sendResponse(
                exchange,
                200,
                response
        );
    }

    private void handleStatement(HttpExchange exchange)
            throws IOException {
                
        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"success\":false,\"message\":\"GET method required\"}"
            );

            return;
        }

        String query =
                exchange.getRequestURI()
                        .getQuery();

        if (query == null) {

            sendResponse(
                    exchange,
                    400,
                    "{\"success\":false,\"message\":\"Account number required\"}"
            );

            return;
        }

        int accountNumber =
                extractAccountNumberFromQuery(query);

        Account account =
                repository.findByAccountNumber(
                        accountNumber
                );

        if (account == null) {

            sendResponse(
                    exchange,
                    404,
                    "{\"success\":false,\"message\":\"Account not found\"}"
            );

            return;
        }

        var transactions =
                atmService.getMiniStatement(account);

        StringBuilder response =
                new StringBuilder();

        response.append("{");
        response.append("\"success\":true,");
        response.append("\"accountNumber\":");
        response.append(account.getAccountNumber());
        response.append(",");
        response.append("\"balance\":");
        response.append(account.getBalance());
        response.append(",");
        response.append("\"transactions\":[");

        for (int i = 0;
            i < transactions.size();
            i++) {

            var transaction =
                    transactions.get(i);

            response.append("{");

            response.append("\"transactionId\":\"");
            response.append(
                    transaction.getTransactionId()
            );
            response.append("\",");

            response.append("\"type\":\"");
            response.append(
                    transaction.getType()
            );
            response.append("\",");

            response.append("\"amount\":");
            response.append(
                    transaction.getAmount()
            );
            response.append(",");

            response.append("\"balanceAfter\":");
            response.append(
                    transaction
                            .getBalanceAfterTransaction()
            );

            response.append("}");

            if (i < transactions.size() - 1) {
                response.append(",");
            }
        }

        response.append("]");
        response.append("}");

        sendResponse(
                exchange,
                200,
                response.toString()
        );
    }

    private void handleChangePin(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"success\":false,\"message\":\"POST method required\"}"
            );

            return;
        }

        String requestBody =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        System.out.println(
                "Change PIN request received: "
                + requestBody
        );

        int accountNumber =
                extractAccountNumber(requestBody);

        String oldPin =
                extractOldPin(requestBody);

        String newPin =
                extractNewPin(requestBody);

        Account account =
                repository.findByAccountNumber(
                        accountNumber
                );

        if (account == null) {

            sendResponse(
                    exchange,
                    404,
                    "{\"success\":false,\"message\":\"Account not found\"}"
            );

            return;
        }

        boolean success =
                atmService.changePin(
                        account,
                        oldPin,
                        newPin
                );

        if (!success) {

            sendResponse(
                    exchange,
                    400,
                    "{\"success\":false,\"message\":\"Invalid old PIN or new PIN\"}"
            );

            return;
        }

        sendResponse(
                exchange,
                200,
                "{\"success\":true,\"message\":\"PIN changed successfully\"}"
        );
    }

    private int extractAccountNumberFromQuery(
            String query) {

        String[] parts =
                query.split("=");

        return Integer.parseInt(parts[1]);
    }

    private double extractAmount(String json) {

        String key = "\"amount\"";

        int keyPosition =
                json.indexOf(key);

        int colonPosition =
                json.indexOf(":", keyPosition);

        int commaPosition =
                json.indexOf(",", colonPosition);

        if (commaPosition == -1) {

            commaPosition =
                    json.indexOf("}", colonPosition);
        }

        String value =
                json.substring(
                        colonPosition + 1,
                        commaPosition
                ).trim();

        return Double.parseDouble(value);
    }

    private int extractReceiverAccountNumber(
            String json) {

        String key = "\"receiverAccountNumber\"";

        int keyPosition =
                json.indexOf(key);

        int colonPosition =
                json.indexOf(":", keyPosition);

        int commaPosition =
                json.indexOf(",", colonPosition);

        if (commaPosition == -1) {

            commaPosition =
                    json.indexOf("}", colonPosition);
        }

        String value =
                json.substring(
                        colonPosition + 1,
                        commaPosition
                ).trim();

        return Integer.parseInt(value);
    }

    private String extractOldPin(String json) {

        String key = "\"oldPin\"";

        int keyPosition =
                json.indexOf(key);

        int colonPosition =
                json.indexOf(":", keyPosition);

        int firstQuote =
                json.indexOf("\"", colonPosition + 1);

        int secondQuote =
                json.indexOf("\"", firstQuote + 1);

        return json.substring(
                firstQuote + 1,
                secondQuote
        );
    }

    private String extractNewPin(String json) {

        String key = "\"newPin\"";

        int keyPosition =
                json.indexOf(key);

        int colonPosition =
                json.indexOf(":", keyPosition);

        int firstQuote =
                json.indexOf("\"", colonPosition + 1);

        int secondQuote =
                json.indexOf("\"", firstQuote + 1);

        return json.substring(
                firstQuote + 1,
                secondQuote
        );
    }

    private int extractAccountNumber(String json) {

        String key = "\"accountNumber\"";

        int keyPosition = json.indexOf(key);

        int colonPosition =
                json.indexOf(":", keyPosition);

        int commaPosition =
                json.indexOf(",", colonPosition);

        if (commaPosition == -1) {
            commaPosition = json.indexOf("}", colonPosition);
        }

        String value =
                json.substring(
                        colonPosition + 1,
                        commaPosition
                ).trim();

        return Integer.parseInt(value);
    }

    private String extractPin(String json) {

        String key = "\"pin\"";

        int keyPosition = json.indexOf(key);

        int colonPosition =
                json.indexOf(":", keyPosition);

        int firstQuote =
                json.indexOf("\"", colonPosition + 1);

        int secondQuote =
                json.indexOf("\"", firstQuote + 1);

        return json.substring(
                firstQuote + 1,
                secondQuote
        );
    }

    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response) throws IOException {

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set("Content-Type", "application/json");

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        OutputStream outputStream =
                exchange.getResponseBody();

        outputStream.write(responseBytes);

        outputStream.close();
    }
}