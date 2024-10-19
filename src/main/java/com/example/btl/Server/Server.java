package com.example.btl.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 12345;
    private static Connection connection;
    private static Map<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                connectToDatabase();
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, connection, onlineUsers);
                clientHandler.start(); // Khởi động một thread cho mỗi client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Kết nối tới database
    private static void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/ltm";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Kết nối database thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
