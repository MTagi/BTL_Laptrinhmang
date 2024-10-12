package com.example.btl.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.btl.User;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Connection connection;

    public ClientHandler(Socket socket, Connection connection) {
        this.clientSocket = socket;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            String command = input.readLine();

            switch (command) {
                case "login":
                    LoginHandler loginHandler = new LoginHandler(connection);
                    String username = input.readLine();
                    String password = input.readLine();
                    User user = loginHandler.authenticate(username, password);

                    if (user != null) {
                        output.println("success");
                        output.println(user.getUsername());
                        output.println(user.getPassword());
                        output.println(user.getGmail());
                        output.println(user.getWin());
                        output.println(user.getDraw());
                        output.println(user.getLoss());
                        output.println(user.getTotalPoints());
                        output.println(user.getStatus());
                        output.println(user.getRole());
                    } else {
                        output.println("fail");
                    }
                    break;

                // Thêm các lệnh khác ở đây
                case "setOffline":
                    // Cập nhật trạng thái người dùng thành 'offline'
                    String userToSetOffline = input.readLine();
                    LoginHandler handler = new LoginHandler(connection);
                    handler.updateStatus(userToSetOffline, "offline");
                    break;
                default:
                    output.println("Lệnh không hợp lệ.");
                    break;
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

