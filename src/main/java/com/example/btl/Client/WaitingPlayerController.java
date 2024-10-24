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
    private Stage myStage;
    private Integer idRoom;
    public void setUser(User data) {
        this.dataUser = data; // Lưu dữ liệu nhận được
    }
    public void setServerConnection(ServerConnection serverConnection, Stage myStage) {
        this.serverConnection = serverConnection;
        this.myStage=myStage;
        running = true;
        startListeningForInvite();
    }
    public void serIdroom(Integer idRoom){
        this.idRoom=idRoom;
    }
    public static LocalDateTime parseStringToLocalDateTime(String dateTimeString) {
        // Định dạng chuỗi
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

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
                    System.out.println("waiting");
                    if (serverMessage != null && "playgamenow".equals(serverMessage)) {
                        running=false;
                        String idRoomnow=serverConnection.receiveMessage();
                        String idMatch=serverConnection.receiveMessage();
                        String player1=serverConnection.receiveMessage();
                        String player2=serverConnection.receiveMessage();
                        LocalDateTime timeBegin=parseStringToLocalDateTime(serverConnection.receiveMessage());
                        Platform.runLater(() -> {
                            try {
                                Match newMatch=new Match(Integer.parseInt(idMatch), player1, player2, timeBegin);
                                this.running=false;
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/Game.fxml"));
                                Scene scene = new Scene(loader.load());
                                GameController gController = loader.getController();
                                gController.setServerConnection(this.serverConnection, this.myStage);
                                gController.setUser(dataUser);
                                gController.setMatch(newMatch);
                                gController.setInforRoom(Integer.parseInt(idRoomnow));
                                gController.startCountdown();
                                // Lấy stage hiện tại từ nút "exit"
                                Stage stage = this.myStage;
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
    public void clickExit() {
        try {
            // Tải giao diện GameScreen.fxml
            this.running=false;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/MainMenu.fxml"));
            Scene scene = new Scene(loader.load());

            // Lấy GameController và truyền đối tượng User
            MainMenuController gameController = loader.getController();
            gameController.setUser(dataUser);
            gameController.setup();

            // Lấy stage hiện tại từ nút đăng nhập
            Stage stage = (Stage) exit.getScene().getWindow();
            gameController.setServerConnection(serverConnection, stage);
            stage.setScene(scene);
            stage.setTitle("Giao diện Game");

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
