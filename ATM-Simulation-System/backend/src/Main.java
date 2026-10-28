import com.sun.net.httpserver.HttpServer;

import controller.ATMController;
import data.InMemoryAccountRepository;
import service.ATMService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args)
            throws IOException {

        InMemoryAccountRepository repository =
                new InMemoryAccountRepository();

        ATMService atmService =
                new ATMService(repository);

        ATMController controller =
                new ATMController(
                        atmService,
                        repository
                );

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080),
                        0
                );

        server.createContext(
                "/api/login",
                controller
        );

        server.createContext(
                "/api/balance",
                controller
        );

        server.createContext(
                "/api/withdraw",
                controller
        );

        server.createContext(
                "/api/deposit",
                controller
        );

        server.createContext(
                "/api/transfer",
                controller
        );

        server.createContext(
                "/api/statement",
                controller
        );

        server.createContext(
                "/api/change-pin",
                controller
        );

        server.setExecutor(null);

        server.start();

        System.out.println(
                "================================"
        );
        System.out.println(
                "     ATM SIMULATION SYSTEM"
        );
        System.out.println(
                "================================"
        );
        System.out.println(
                "Server started successfully!"
        );
        System.out.println(
                "http://localhost:8080"
        );
        System.out.println(
                "Login API: /api/login"
        );
        System.out.println(
                "Balance API: /api/balance"
        );
    }
}