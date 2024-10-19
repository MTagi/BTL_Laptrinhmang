package com.example.btl.Client;

import com.example.btl.CustomRoom;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import com.example.btl.User;
public class CustomRoomController {
    @FXML
    private Button addFriend;

    @FXML
    private AnchorPane friend;

    @FXML
    private Label hello;

    @FXML
    private Label namePlayer1;

    @FXML
    private Label namePlayer2;

    @FXML
    private Button ready;
    private User dataUser;
    // lấy dữ liệu từ giao diện Login.
    public void setUser(User data) {
        this.dataUser = data; // Lưu dữ liệu nhận được
    }
    private CustomRoom dataCustomRoom;
    // lấy dữ liệu từ giao diện Login.
    public void setCustomRoom(CustomRoom data) {
        this.dataCustomRoom = data; // Lưu dữ liệu nhận được
    }
    public void setup(){
        hello.setText("Chào mừng bạn đến với phòng " + this.dataCustomRoom.getIdRoom());
    }


}
