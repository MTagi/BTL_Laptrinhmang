package com.example.btl.Client;

import com.example.btl.CustomRoom;
import com.example.btl.Match;
import com.example.btl.Server.ServerConnection;
import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.List;

import com.example.btl.User;

public class MainMenuController {
    @FXML
    private Button createRoom;
    @FXML
    private TableColumn<User, Integer> kbdiem;

    @FXML
    private Button kbexit;

    @FXML
    private TableView<User> kbtable;
    @FXML
    private TextField kbten;

    @FXML
    private TableColumn<User, String> kbtaikhoan;
    @FXML
    private Label namePlayer;
    @FXML
    private Label numberwin;
    @FXML
    private Label score;
    @FXML
    private Label rankmy;
    @FXML
    private TableView<User> ranktable;
    @FXML
    private TableColumn<User, Integer> rank1;

    @FXML
    private TableColumn<User, String> rank2;

    @FXML
    private TableColumn<User, Integer> rank3;

    @FXML
    private AnchorPane rankanchorpane;

    @FXML
    private Label sumgame;
    private User dataUser;
    private volatile boolean running = true;
    private Stage myStage;
    private Thread listen;
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
        listen=new Thread(() -> {
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
        });
        listen.start();

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
                String response;
                response = serverConnection.receiveMessage();
                if("null".equals(response)) response=serverConnection.receiveMessage();
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

                    this.running = false;
                    int roomId = Integer.parseInt(serverConnection.receiveMessage());
                    String player1 = serverConnection.receiveMessage(); // Nhận player1
                    String player2 = serverConnection.receiveMessage(); // Nhận player2 (có thể là null)
                    this.running = true;
                    try {
                        this.running = false;
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
//                    Socket socket2 = new Socket("localhost", 12345);
//                    PrintWriter output2 = new PrintWriter(new OutputStreamWriter(socket2.getOutputStream()), true);
//                    output2.println("setWaiting");
//                    output2.println(this.dataUser.getUsername());
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
    private AnchorPane timphonganchor;

    @FXML
    private Button timphongbutton;

    @FXML
    private TextField timphongtext;
    public void clickTimPhong(){
        timphonganchor.setVisible(true);
    }
    public void clickTimPhongButton(){
        try {
            serverConnection.sendMessage("clickTimPhong");
            String idphong = timphongtext.getText();
            serverConnection.sendMessage(idphong);
            serverConnection.sendMessage(dataUser.getUsername());
            this.running = false;
            String respon = serverConnection.receiveMessage();
            if ("haveroom".equals(respon)) {
                String pla1 = serverConnection.receiveMessage();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/CustomRoom.fxml"));
                    Scene scene = new Scene(loader.load());
                    // Lấy GameController và truyền đối tượng User
                    CustomRoomController gameController = loader.getController();
                    gameController.setServerConnection(this.serverConnection, this.myStage);

                    gameController.setUser(this.dataUser);
                    gameController.setIdRoom(Integer.parseInt(idphong));
                    gameController.setThread();
                    gameController.setInviteToCustomRoom(String.valueOf(idphong), dataUser.getUsername(), pla1);
                    // Lấy stage hiện tại từ nút đăng nhập
                    Stage stage = (Stage) this.myStage;
                    stage.setScene(scene);
                    stage.setTitle("Phòng chờ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showAlert("loi","khong tim thay phong phu hop");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
    private List<User> fetchPlayerRanksFromServer() {
        List<User> users = new ArrayList<>();
        try {
            serverConnection.sendMessage("getRank");
            this.running = false;
            // Nhận dữ liệu từ server
            String response;
            int index = 1; // Biến đếm số thứ tự
            while (!(response = serverConnection.receiveMessage()).equals("END")) {
                String[] parts = response.split(":");
                String username = parts[0].trim();
                if(username.equals(dataUser.getUsername())){
                    rankmy.setText(String.valueOf(index));
                }
                int totalPoints = Integer.parseInt(parts[1].trim());
                // Tạo đối tượng User với thông tin đầy đủ
                User user = new User(username, "", "", 0, 0, 0, totalPoints, "", 0);
                user.setStt(index++); // Gán STT cho user
                users.add(user); // Thêm người dùng vào danh sách
            }
            this.running = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
    @FXML
    private AnchorPane yeucaukbanchor;

    @FXML
    private TableColumn<User, String> yeucautk;
    @FXML
    private TableView<User> yeucautktable;

    public void clickChapNhan(){
        User selectedPlayer = yeucautktable.getSelectionModel().getSelectedItem();
        if (selectedPlayer != null) {
            serverConnection.sendMessage("chapnhanyeucau");
            serverConnection.sendMessage(dataUser.getUsername());
            serverConnection.sendMessage(selectedPlayer.getUsername());
            showAlert("Thông báo", "Them ban be thanh cong");
        } else {
            showAlert("Thông báo", "Vui lòng chọn 1 người chơi");
        }

    }
    public void clickYCKB(){
        try {
            List<User> users = new ArrayList<>();
            yeucaukbanchor.setVisible(true);
            serverConnection.sendMessage("yeucauketban");
            serverConnection.sendMessage(dataUser.getUsername());
            this.running = false;
            String s=serverConnection.receiveMessage();
            this.running=true;
            String []arr=s.split(";");
            for(String res: arr){
                User user = new User(res, "", "", 0, 0, 0, 0, "", 0);
                users.add(user);
            }
            yeucautk.setCellValueFactory(new PropertyValueFactory<>("username"));
            // Gọi phương thức nhận dữ liệu
            yeucautktable.getItems().addAll(users);


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void yeuCauExit(){
        yeucaukbanchor.setVisible(false);
    }
    private List<User> fetchPlayerFromServer() {
        List<User> users = new ArrayList<>();
        try {
            serverConnection.sendMessage("userwithfind");
            serverConnection.sendMessage(kbten.getText());
            this.running = false;
            // Nhận dữ liệu từ server
            String response;
            while (!(response = serverConnection.receiveMessage()).equals("END")) {
                System.out.println(response);
                String[] parts = response.split(":");
                String username = parts[0].trim();
                if(username.equals(dataUser.getUsername())) continue;
                int totalPoints = Integer.parseInt(parts[1].trim());
                // Tạo đối tượng User với thông tin đầy đủ
                User user = new User(username, "", "", 0, 0, 0, totalPoints, "", 0);
                users.add(user); // Thêm người dùng vào danh sách
            }
            this.running = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    @FXML
    private AnchorPane banbeanchor;
    public void clickTim(){
        kbtaikhoan.setCellValueFactory(new PropertyValueFactory<>("username"));
        kbdiem.setCellValueFactory(new PropertyValueFactory<>("totalPoints"));
        List<User> users = fetchPlayerFromServer(); // Gọi phương thức nhận dữ liệu
        kbtable.getItems().addAll(users);
    }
    public void timBan(){
        banbeanchor.setVisible(true);

    }
    public void exitKetBan(){
        banbeanchor.setVisible(false);
    }
    public void clickKetBan(){
        User selectedPlayer = kbtable.getSelectionModel().getSelectedItem();
        if (selectedPlayer != null) {
            serverConnection.sendMessage("ketban");
            serverConnection.sendMessage(dataUser.getUsername());
            serverConnection.sendMessage(selectedPlayer.getUsername());
            showAlert("Thông báo", "Gừi lời mời thành công");
        } else {
            showAlert("Thông báo", "Vui lòng chọn 1 người chơi");
        }
    }
    public void getRank(){
        rankanchorpane.setVisible(true);
        rank1.setCellValueFactory(new PropertyValueFactory<>("stt")); // Đảm bảo User class có phương thức getStt()
        rank2.setCellValueFactory(new PropertyValueFactory<>("username"));
        rank3.setCellValueFactory(new PropertyValueFactory<>("totalPoints"));
        List<User> users = fetchPlayerRanksFromServer(); // Gọi phương thức nhận dữ liệu
        ranktable.getItems().addAll(users);
    }
    public void clickExitRank(){
        rankanchorpane.setVisible(false);
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
