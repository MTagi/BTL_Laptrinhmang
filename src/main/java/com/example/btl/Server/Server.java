package com.example.btl.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {
    private static final int PORT = 12345;
    private static Connection connection;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server đang lắng nghe tại port " + PORT);

            // Kết nối tới cơ sở dữ liệu
            connectToDatabase();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client đã kết nối: " + clientSocket.getInetAddress());

                // Tạo và khởi động một thread mới cho mỗi client
                new ClientHandler(clientSocket, connection).start();
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
