package com.example.btl.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {
    private static final int PORT = 12345;
    private static final int ADMIN_PORT = 54321;
    private static Connection connection;

    public static void main(String[] args) {
        try {
            connectToDatabase();
            new Thread(Server::startClientServer).start(); // Chạy server client trong thread riêng
            new Thread(Server::startAdminServer).start();   // Chạy server admin trong thread riêng
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startClientServer() {
        try (ServerSocket clientServerSocket = new ServerSocket(PORT)) {
            System.out.println("Server đang lắng nghe client tại port " + PORT);
            while (true) {
                Socket clientSocket = clientServerSocket.accept();
                System.out.println("Client đã kết nối: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket, connection).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startAdminServer() {
        try (ServerSocket adminServerSocket = new ServerSocket(ADMIN_PORT)) {
            System.out.println("Server đang lắng nghe admin tại port " + ADMIN_PORT);
            while (true) {
                Socket adminSocket = adminServerSocket.accept();
                System.out.println("Admin đã kết nối: " + adminSocket.getInetAddress());
                new AdminHandler(adminSocket, connection).start();
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
            String password = "mysql@123456";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Kết nối database thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
