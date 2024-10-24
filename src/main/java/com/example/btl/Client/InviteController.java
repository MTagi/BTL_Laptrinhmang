package com.example.btl.Client;

import com.example.btl.Server.ServerConnection;
import com.example.btl.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class InviteController {
    private Stage myStage;
    // lấy dữ liệu từ giao diện Login.
    private ServerConnection serverConnection;
    private String nameFriend;
    private Integer idRoom;
    private User dataUser;
    public void setServerConnection(ServerConnection serverConnection, Stage stage) {
        this.serverConnection = serverConnection;
        this.myStage=stage;
    }
    public void setDataUser(User user){
        this.dataUser=user;
    }
    public void setInforRoom(String nameFriend, Integer idRoom){
        this.nameFriend=nameFriend;
        this.idRoom=idRoom;
    }
    public void  clickAccept(){
        try {
            serverConnection.sendMessage("acceptInvite");
            serverConnection.sendMessage(this.nameFriend);
            serverConnection.sendMessage(this.dataUser.getUsername());
            serverConnection.sendMessage(String.valueOf(idRoom));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/CustomRoom.fxml"));
            Scene scene = new Scene(loader.load());
            // Lấy GameController và truyền đối tượng User
            CustomRoomController gameController = loader.getController();
            gameController.setServerConnection(this.serverConnection, this.myStage);
            gameController.setUser(this.dataUser);
            gameController.setIdRoom(this.idRoom);
            gameController.setThread();
            gameController.setInviteToCustomRoom(String.valueOf(this.idRoom), dataUser.getUsername(), this.nameFriend);
            // Lấy stage hiện tại từ nút đăng nhập
            Stage stage = (Stage) this.myStage;
            stage.setScene(scene);
            stage.setTitle("Phòng chờ");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}
