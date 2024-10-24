package com.example.btl.Client;

import com.example.btl.CustomRoom;
import com.example.btl.Match;
import com.example.btl.Server.ServerConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.example.btl.User;
import javafx.util.Callback;

public class CustomRoomController {
    @FXML
    private Button addFriend;

    @FXML
    private AnchorPane friend;

    @FXML
    private Label hello;
    @FXML
    private AnchorPane friendanchorpane;

    @FXML
    private AnchorPane friendinviteanchorpane;

    @FXML
    private Label namePlayer1;

    @FXML
    private Label namePlayer2;

    @FXML
    private Button ready;
    @FXML
    private AnchorPane inviteAnchorPane;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, Void> inviteColumn;

    @FXML
    private TableView<User> inviteTable;

    // Danh sách người dùng
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ServerConnection serverConnection;
    private User dataUser;
    private Stage myStage;
    private Integer idRoom;
    private volatile boolean running = true;
    private Thread listenTheard;
    // lấy dữ liệu từ giao diện Login.
    public void setUser(User data) {
        this.dataUser = data; // Lưu dữ liệu nhận được
    }
    public void setIdRoom(Integer idRoom){
        this.idRoom=idRoom;
    }
    // lấy dữ liệu từ giao diện Login.
    public void setup(){
        hello.setText("Chào mừng bạn đến với phòng " + this.idRoom);
        namePlayer1.setText(dataUser.getUsername());
    }
    public void setServerConnection(ServerConnection serverConnection, Stage myStage) {
        this.serverConnection = serverConnection;
        this.myStage=myStage;
    }
    public void setThread(){
        this.running =true;
        startListeningForInvite();
    }
    public void startListeningForInvite() {
        this.listenTheard=new Thread(() -> {
            try {
                while (this.running) {
                    String serverMessage = serverConnection.receiveMessage();
                    System.out.println(serverMessage);
                    System.out.println("custom");
                    if ("friendAcceptInvite".equals(serverMessage)) {
                        this.running=false;
                        String nameFriend=serverConnection.receiveMessage();
                        Platform.runLater(() -> {
                            try {
                                friendanchorpane.setVisible(true);
                                friendinviteanchorpane.setVisible(false);
                                ready.setVisible(true);
                                namePlayer2.setText(nameFriend);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        this.running=true;
                    }
                    if("waitingcustomroom".equals(serverMessage)){
                        try {
                            Platform.runLater(() -> {
                                showAlert("waiting", "Vui lòng chờ thêm người chơi khác sẵn sàng");
                            });
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if("playnowcustomroom".equals(serverMessage)){
                        try {
                            this.running=false;
                            String idRoomnow = serverConnection.receiveMessage();
                            String idMatch = serverConnection.receiveMessage();
                            String player1 = serverConnection.receiveMessage();
                            String player2 = serverConnection.receiveMessage();
                            LocalDateTime timeBegin = parseStringToLocalDateTime(serverConnection.receiveMessage());

                            Platform.runLater(() -> {
                                try {
                                    Match newMatch = new Match(Integer.parseInt(idMatch), player1, player2, timeBegin);
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
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        this.running=true;
                    }
                    if("playgamenowcustomroom".equals(serverMessage)){
                        try {
                            this.running=false;

                            String idRoomnow = serverConnection.receiveMessage();
                            String idMatch = serverConnection.receiveMessage();
                            String player1 = serverConnection.receiveMessage();
                            String player2 = serverConnection.receiveMessage();
                            LocalDateTime timeBegin = parseStringToLocalDateTime(serverConnection.receiveMessage());

                            Platform.runLater(() -> {
                                try {
                                    Match newMatch = new Match(Integer.parseInt(idMatch), player1, player2, timeBegin);
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
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        this.running=true;
                    }
                }
            } catch (IOException ex) {
                System.out.println("Error receiving message from server: " + ex.getMessage());
            }
        });
        this.listenTheard.start();

    }
    public void clickInvite() {
        try {
            serverConnection.sendMessage("getlistfriend");
            serverConnection.sendMessage(dataUser.getUsername());
//            this.running = false;
//
//            // Nhận tin nhắn từ server và xử lý dữ liệu ngay khi có phản hồi
//            String s = serverConnection.receiveMessage();
//            this.running = true;
            String s;
            synchronized (this) {
                this.running = false;
                s = serverConnection.receiveMessage();
                this.running = true;
            }
            // Kiểm tra dữ liệu trả về từ server để biết khi nào server đã phản hồi
            if (!s.equals("")) {
                String[] listUser = s.trim().split(";");
                for (String username : listUser) {
                    userList.add(new User(username));
                }
            } else {
                userList.clear();
            }

            inviteAnchorPane.setVisible(true);
            addFriend.setVisible(false);

            // Cấu hình table columns
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

            inviteColumn.setCellFactory(new Callback<>() {
                @Override
                public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                    return new TableCell<>() {
                        private final Button inviteButton = new Button("Gửi lời mời");

                        {
                            inviteButton.setOnAction(event -> {
                                inviteAnchorPane.setVisible(false);
                                addFriend.setVisible(true);

                                User user = getTableView().getItems().get(getIndex());
                                System.out.println("Đã gửi lời mời tới: " + user.getUsername());

                                serverConnection.sendMessage("sendInvite");
                                serverConnection.sendMessage(user.getUsername());
                                serverConnection.sendMessage(dataUser.getUsername());
                                serverConnection.sendMessage(String.valueOf(idRoom));

                                // Kiểm tra trạng thái luồng listenTheard sau khi server đã gửi dữ liệu
                                if (listenTheard.isAlive()) {
                                    System.out.println("aaaaaaaaaaaa");
                                } else {
                                    System.out.println("bbbbbbbb");
                                }
                            });
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(inviteButton);
                            }
                        }
                    };
                }
            });

            // Cài đặt dữ liệu cho TableView
            inviteTable.setItems(userList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sau khi nhận dữ liệu từ server và cập nhật this.running, kiểm tra luồng
        if (!listenTheard.isAlive()) {
            startListeningForInvite(); // Hàm này khởi tạo lại listenTheard
            System.out.println("Khởi tạo lại listenTheard.");
        }
    }

    public void clickExitListFriend(){
        inviteAnchorPane.setVisible(false);
        addFriend.setVisible(true);
    }
    public void setInviteToCustomRoom(String idRoom, String n1,String n2){
        this.idRoom=Integer.parseInt(idRoom);
        hello.setText("Chào mừng bạn đến với phòng " + idRoom);
        namePlayer1.setText(n1);
        namePlayer2.setText(n2);
        friendanchorpane.setVisible(true);
        friendinviteanchorpane.setVisible(false);
        ready.setVisible(true);

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
    public void clickReady(){
        setThread();
        ready.setVisible(false);
        try {
            serverConnection.sendMessage("clickready");
            serverConnection.sendMessage(dataUser.getUsername());
            serverConnection.sendMessage(namePlayer2.getText());
            serverConnection.sendMessage(String.valueOf(this.idRoom));
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    // Phương thức để hiển thị thông báo với tiêu đề và nội dung
    public void showAlert(String title, String message) {
        // Tạo một thông báo kiểu thông tin (INFORMATION)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);              // Thiết lập tiêu đề cho thông báo
        alert.setHeaderText(null);          // Loại bỏ tiêu đề phần đầu của thông báo (có thể giữ nếu cần)
        alert.setContentText(message);      // Thiết lập nội dung của thông báo

        // Hiển thị thông báo và chờ người dùng đóng
        alert.showAndWait();
    }

}
