package pl.quiz.server;

import java.io.IOException;
import java.util.Scanner;

public class ServerCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Server server = null;

        String input;
        while (true) {
            input = scanner.nextLine();
            switch (input) {
                case "start":
                    System.out.println("Starting server");
                    server = new Server(8080);
                    server.start();
                    break;
                case "stop":
                    System.out.println("Stopping server");
                    if (server != null && !server.isInterrupted()) {
                        try {
                            server.interrupt();
                            server.stopServer();
                        } catch (IOException e) {
                            System.err.println("Error during server stopping.");
                        }
                    } else {
                        System.err.println("Server is already stopped");
                    }
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    System.err.println("Unknown command");
                    break;

            }
        }
    }
}
