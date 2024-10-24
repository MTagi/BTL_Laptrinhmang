package com.example.btl.Client;

import com.example.btl.CustomRoom;
import com.example.btl.Match;
import com.example.btl.Server.ServerConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.example.btl.User;

public class MainMenuController {
    @FXML
    private Button createRoom;
    @FXML
    private Label namePlayer;
    @FXML
    private Label numberwin;
    @FXML
    private Label score;

    @FXML
    private Label sumgame;
    private User dataUser;
    private volatile boolean running = true;
    private Stage myStage;
    // lấy dữ liệu từ giao diện Login.
    private ServerConnection serverConnection;

    public void setServerConnection(ServerConnection serverConnection, Stage stage) {
        this.serverConnection = serverConnection;
        this.myStage=stage;

    }
    public void setThread(){
        this.running =true;
        startListeningForInvite();
    }
    public void setUser(User data) {
        this.dataUser = data; // Lưu dữ liệu nhận được
    }
    private void startListeningForInvite() {
        new Thread(() -> {
            try {
                while (this.running) {
                    String serverMessage = serverConnection.receiveMessage();
                    System.out.println(serverMessage);
                    System.out.println("mainmenu");
                    if ("ReceiveInvite".equals(serverMessage)) {
                        running=false;
                        String nameFriend=serverConnection.receiveMessage();
                        Integer idRoom= Integer.parseInt(serverConnection.receiveMessage());
                        Platform.runLater(() -> {
                            try {
                                this.running=false;
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/Invite.fxml"));
                                Scene scene = new Scene(loader.load());
                                InviteController tes=loader.getController();
                                tes.setServerConnection(serverConnection, this.myStage);
                                tes.setDataUser(this.dataUser);
                                tes.setInforRoom(nameFriend,idRoom);
                                Stage stage = this.myStage;
                                stage.setScene(scene);
                                stage.setTitle("Giao diện Game");
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        running=true;
                    }
                }
            } catch (IOException ex) {
                System.out.println("Error receiving message from server: " + ex.getMessage());
            }
        }).start();

    }
    public void clickLogOut(){
        try {
            // Tải giao diện GameScreen.fxml
            this.running=false;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/Login.fxml"));
            Scene scene = new Scene(loader.load());

            // Lấy stage hiện tại từ nút đăng nhập
            Stage stage = this.myStage;
            stage.setScene(scene);
            stage.setTitle("Login");
            serverConnection.sendMessage("setOffline");
            serverConnection.sendMessage(this.dataUser.getUsername());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải giao diện.");
        }
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
    @FXML
    private void clickPlayNow() {
        try {
                // Gửi yêu cầu đến server
                serverConnection.sendMessage("checkRandomRoom");
                serverConnection.sendMessage(dataUser.getUsername());
                this.running=false;
                // Nhận phản hồi từ server
                String response = serverConnection.receiveMessage();
                if ("playnow".equals(response)) {
                    String idRoomnow=serverConnection.receiveMessage();
                    String idMatch=serverConnection.receiveMessage();
                    String player1=serverConnection.receiveMessage();
                    String player2=serverConnection.receiveMessage();
                    LocalDateTime timeBegin=parseStringToLocalDateTime(serverConnection.receiveMessage());
                    Match newMatch=new Match(Integer.parseInt(idMatch), player1, player2, timeBegin);
                    this.running=false;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/Game.fxml"));
                    Scene scene = new Scene(loader.load());
                    GameController gController = loader.getController();
                    gController.setServerConnection(this.serverConnection, myStage);
                    gController.setUser(dataUser);
                    gController.setMatch(newMatch);
                    gController.setInforRoom(Integer.parseInt(idRoomnow));
                    gController.startCountdown();
                    // Lấy stage hiện tại từ nút "exit"
                    Stage stage = this.myStage;
                    stage.setScene(scene);
                    stage.setTitle("Giao diện Game");
                    stage.show();
                } else {
                    this.running=false;
                    int roomId = Integer.parseInt(serverConnection.receiveMessage());
                    String player1 = serverConnection.receiveMessage(); // Nhận player1
                    String player2 = serverConnection.receiveMessage(); // Nhận player2 (có thể là null)
                    this.running=true;
                        try {
                            this.running=false;
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/WaitingPlay.fxml"));
                            Scene scene = new Scene(loader.load());
                            WaitingPlayerController wController = loader.getController();
                            wController.setServerConnection(this.serverConnection, this.myStage);
                            wController.setUser(dataUser);
                            wController.serIdroom(roomId);
                            // Lấy stage hiện tại từ nút đăng nhập
                            Stage stage = this.myStage;
                            stage.setScene(scene);
                            stage.setTitle("Giao diện Game");
                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        running=true;
    }
// Chuyển sang giao diện game
    public void clickCreateRoom() {
        this.running =false;
        try {
            try {
                serverConnection.sendMessage("createCustomRoom");
                serverConnection.sendMessage(this.dataUser.getUsername());
                // Nhận phản hồi từ server về thông tin phòng
                String roomCreationResponse = serverConnection.receiveMessage();
                if ("roomCreated".equals(roomCreationResponse)) {
                    // Nhận thông tin phòng chơi từ server
                    int customRoom = Integer.parseInt(serverConnection.receiveMessage());
                    String player1 = serverConnection.receiveMessage(); // player1 có thể là null ban đầu
                    String player2 = serverConnection.receiveMessage(); // player2 có thể là null ban đầu
                    // Tạo đối tượng Room từ thông tin server trả về
                    CustomRoom room = new CustomRoom(customRoom, player1, player2);
                    // Tải giao diện GameScreen.fxml
                    this.running=false;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/CustomRoom.fxml"));
                    Scene scene = new Scene(loader.load());
                    // Lấy GameController và truyền đối tượng User
                    CustomRoomController gameController = loader.getController();
                    gameController.setUser(this.dataUser);
                    gameController.setIdRoom(customRoom);
                    gameController.setServerConnection(this.serverConnection, this.myStage);
                    gameController.setThread();
                    gameController.setup();
                    // Lấy stage hiện tại từ nút đăng nhập
                    Stage stage = this.myStage;
                    stage.setScene(scene);
                    stage.setTitle("Phòng chờ");
                    // Thiết lập sự kiện đóng cửa sổ để cập nhật trạng thái offline
                    stage.setOnCloseRequest(event -> {
                        serverConnection.sendMessage("setOffline");
                        serverConnection.sendMessage(this.dataUser.getUsername());
                    });
                    stage.show();

                    // chuyển người chơi sang chế độ waiting
                    Socket socket2 = new Socket("localhost", 12345);
                    PrintWriter output2 = new PrintWriter(new OutputStreamWriter(socket2.getOutputStream()), true);
                    output2.println("setWaiting");
                    output2.println(this.dataUser.getUsername());
                } else {
                    showAlert("Lỗi", "Không thể tạo phòng.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể tải giao diện game.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        this.running =true;
    }

    @FXML
    public void setup() {
        namePlayer.setText(dataUser.getUsername());
        score.setText(String.valueOf(this.dataUser.getTotalPoints()));
        numberwin.setText(String.valueOf(this.dataUser.getWin()));
        int tong=this.dataUser.getDraw()+this.dataUser.getWin()+this.dataUser.getLoss();
        sumgame.setText(String.valueOf(tong));
    }
        // Hiển thị thông báo cho người dùng
        private void showAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);  // Không có tiêu đề phụ, tạo sự tối giản
            alert.setContentText(message);

            // Thiết lập kích thước của thông báo (tùy chọn)
            alert.getDialogPane().setPrefSize(350, 100);

            // Hiển thị cảnh báo
            alert.showAndWait();
        }
}


//public void clickCreateRoom() {
//        try {
//            // Tải giao diện CustomRoom.fxml
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/CustomRoom.fxml"));
//            Scene scene = new Scene(loader.load());
//
//            // Lấy CustomRoomController và truyền đối tượng User
//            CustomRoomController gameController = loader.getController();
//            gameController.setUser(this.dataUser);
//
//            Task<Void> createRoomTask = new Task<>() {
//                @Override
//                protected Void call() throws Exception {
//                    try (Socket socket = new Socket("localhost", 12345);
//                         PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
//                         BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//
//                        // Gửi yêu cầu tạo phòng chơi
//                        serverConnection.sendMessage("createCustomRoom");
//                        serverConnection.sendMessage(dataUser.getUsername());
//
//                        // Nhận phản hồi từ server về việc tạo phòng
//                        String roomCreationResponse = serverConnection.receiveMessage();
//                        if ("roomCreated".equals(roomCreationResponse)) {
//                            // Nhận thông tin phòng chơi từ server
//                            int customRoom = Integer.parseInt(serverConnection.receiveMessage());
//                            String player1 = serverConnection.receiveMessage();
//                            String player2 = serverConnection.receiveMessage();
//
//                            // Tạo đối tượng CustomRoom từ thông tin server trả về
//                            CustomRoom room = new CustomRoom(customRoom, player1, player2);
//                            gameController.setCustomRoom(room);
//
//                            // **Cập nhật trạng thái "waiting" trong luồng nền**
//                            Task<Void> setWaitingTask = new Task<>() {
//                                @Override
//                                protected Void call() throws Exception {
//                                    try (Socket socket = new Socket("localhost", 12345);
//                                         PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {
//                                        // Gửi yêu cầu cập nhật trạng thái "waiting" tới server
//                                        serverConnection.sendMessage("setWaiting");
//                                        serverConnection.sendMessage(dataUser.getUsername());
//                                        output.flush();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    return null;
//                                }
//                            };
//
//                            // Chạy Task cập nhật trạng thái waiting trên luồng nền
//                            new Thread(setWaitingTask).start();
//                        } else {
//                            Platform.runLater(() -> showAlert("Lỗi", "Không thể tạo phòng."));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Platform.runLater(() -> showAlert("Lỗi", "Không thể giao tiếp với server."));
//                    }
//                    return null;
//                }
//            };
//            // Chạy Task tạo phòng trên luồng nền
//            new Thread(createRoomTask).start();
//
//            // Chuyển người chơi sang giao diện phòng chờ
//            Stage stage = (Stage) namePlayer.getScene().getWindow();
//            stage.setScene(scene);
//            stage.setTitle("Phòng chờ");
//
//            // **Xử lý khi đóng cửa sổ để đặt người chơi offline**
//            stage.setOnCloseRequest(event -> {
//                Task<Void> setOfflineTask = new Task<>() {
//                    @Override
//                    protected Void call() throws Exception {
//                        try (Socket socket = new Socket("localhost", 12345);
//                             PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {
//                            serverConnection.sendMessage("setOffline");
//                            serverConnection.sendMessage(dataUser.getUsername());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//                };
//
//                // Chạy Task cập nhật trạng thái offline trên luồng nền
//                new Thread(setOfflineTask).start();
//            });
//
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert("Lỗi", "Không thể tải giao diện game.");
//        }
//    }