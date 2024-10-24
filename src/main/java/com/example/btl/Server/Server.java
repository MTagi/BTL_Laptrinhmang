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
    private static final int ADMIN_PORT = 54321;
    private static Connection connection;
    private static Map<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            //connectToDatabase();
            new Thread(Server::startClientConnection).start(); // Chạy kết nối client trong thread riêng
            new Thread(Server::startAdminConnection).start();   // Chạy kết nối admin trong thread riêng
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startClientConnection() {
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

    private static void startAdminConnection() {
        try (ServerSocket serverSocket = new ServerSocket(ADMIN_PORT)) {
            System.out.println("Admin server is running on port " + ADMIN_PORT);
            while (true) {
                connectToDatabase();
                Socket adminSocket = serverSocket.accept();
                AdminHandler adminHandler = new AdminHandler(adminSocket, connection);
                adminHandler.start(); // Khởi động một thread cho mỗi admin
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