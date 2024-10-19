package com.example.btl.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.example.btl.Match;
import com.example.btl.Server.ServerConnection;
import com.example.btl.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class WaitingPlayerController {
    @FXML
    private Button exit;
    private User dataUser;
    private ServerConnection serverConnection;
    private volatile boolean running = true;
    public void setUser(User data) {
        this.dataUser = data; // Lưu dữ liệu nhận được
    }
    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        startListeningForInvite();
    }
    private LocalDateTime parseStringToLocalDateTime(String dateTimeString) {
        // Định dạng chuỗi
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            // Chuyển đổi chuỗi thành LocalDateTime
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            // Xử lý lỗi nếu định dạng không khớp
            System.err.println("Error parsing date/time: " + e.getMessage());
            return null; // Hoặc xử lý theo cách khác
        }
    }

    private void startListeningForInvite() {
        new Thread(() -> {
            try {
                while (running) {
                    String serverMessage = serverConnection.receiveMessage();
                    System.out.println(serverMessage);
                    if (serverMessage != null && "playgamenow".equals(serverMessage)) {
                        running=false;
                        String idMatch=serverConnection.receiveMessage();
                        System.out.println(idMatch);
                        String player1=serverConnection.receiveMessage();
                        System.out.println(player1);
                        String player2=serverConnection.receiveMessage();
                        System.out.println(player2);
                        LocalDateTime timeBegin=parseStringToLocalDateTime(serverConnection.receiveMessage());
                        Platform.runLater(() -> {
                            try {
                                Match newMatch=new Match(Integer.parseInt(idMatch), player1, player2, timeBegin);
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/Game.fxml"));
                                Scene scene = new Scene(loader.load());
                                GameController gController = loader.getController();
                                gController.setServerConnection(this.serverConnection);
                                gController.setUser(dataUser);
                                gController.setMatch(newMatch);
                                gController.startCountdown();
                                // Lấy stage hiện tại từ nút "exit"
                                Stage stage = (Stage) exit.getScene().getWindow();
                                stage.setScene(scene);
                                stage.setTitle("Giao diện Game");
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            } catch (IOException ex) {
                System.out.println("Error receiving message from server: " + ex.getMessage());
            }
        }).start();
    }

}
