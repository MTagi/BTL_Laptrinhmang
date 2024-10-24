package com.example.btl.Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import com.example.btl.User;
import com.example.btl.Server.ServerConnection;
import com.example.btl.Admin.DashboardUIHandler;

public class LoginController {

    @FXML
    private TextField gmaildk;

    @FXML
    private AnchorPane loginAnchorpane;

    @FXML
    private TextField matkhaudk;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField taikhoandk;
    @FXML
    private AnchorPane dkianchorpane;

    @FXML
    private TextField usernameField;
    private ServerConnection serverConnection;


    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    // Xử lý sự kiện khi người dùng nhấn nút "Login"
    @FXML
    public void handleLoginButtonAction() {
        try {
            ServerConnection serverConnection = new ServerConnection();
            serverConnection.connect("26.250.117.172", 12345);
            this.serverConnection=serverConnection;
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Lỗi", "Tên đăng nhập và mật khẩu không được để trống.");
                return;
            }       
                // Gửi lệnh login và thông tin đăng nhập
                serverConnection.sendMessage("login");
                serverConnection.sendMessage(username);
                serverConnection.sendMessage(password);

                // Nhận phản hồi từ server
                String response = serverConnection.receiveMessage();

                if ("success".equals(response)) {
                    // Nhận dữ liệu người dùng từ server
                    String usernameReceived = serverConnection.receiveMessage();
                    String passwordReceived =serverConnection.receiveMessage();
                    String gmail = serverConnection.receiveMessage();
                    int win = Integer.parseInt(serverConnection.receiveMessage());
                    int draw = Integer.parseInt(serverConnection.receiveMessage());
                    int loss = Integer.parseInt(serverConnection.receiveMessage());
                    int totalPoints = Integer.parseInt(serverConnection.receiveMessage());
                    String status = serverConnection.receiveMessage();
                    int role = Integer.parseInt(serverConnection.receiveMessage());

                    // Tạo đối tượng User
                    User user = new User(usernameReceived, passwordReceived, gmail, win, draw, loss, totalPoints, status, role);
                    if (role == 0) {
                        switchToGameScreen(user);
                    } else {
                        switchToAdminScreen(user);
                    }
                    // Chuyển sang giao diện game và truyền đối tượng User

                } else {
                    // Nếu đăng nhập thất bại, hiển thị thông báo
                    showAlert("Thất bại", "Tài khoản hoặc mật khẩu không chính xác.");
                }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void switchDangKi(){
        loginAnchorpane.setVisible(false);
        dkianchorpane.setVisible(true);
    }
    public void switchdnhap(){
        loginAnchorpane.setVisible(true);
        dkianchorpane.setVisible(false);
    }
    public void clickDki(){
        try {
            ServerConnection serverConnection = new ServerConnection();
            serverConnection.connect("localhost", 12345);
            this.serverConnection = serverConnection;
            String usernamedk = taikhoandk.getText();
            String passworddk = matkhaudk.getText();
            String gmaildki= gmaildk.getText();

            if (usernamedk.isEmpty() || passworddk.isEmpty() || gmaildki.isEmpty()) {
                showAlert("Lỗi", "Thong tin không được để trống.");
                return;
            }
            serverConnection.sendMessage("dangki");
            serverConnection.sendMessage(usernamedk);
            serverConnection.sendMessage(passworddk);
            serverConnection.sendMessage(gmaildki);
            String respon=serverConnection.receiveMessage();
            if("dkithanhcong".equals(respon)){
                showAlert("thong bao", "dang ki thanh cong.");
                return;
            }
            else{
                showAlert("thong bao", "tai khoan da ton tai.");
                return;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    // Chuyển sang giao diện game
    private void switchToGameScreen(User user) {
        try {
            // Tải giao diện GameScreen.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/MainMenu.fxml"));
            Scene scene = new Scene(loader.load());

            // Lấy GameController và truyền đối tượng User
            MainMenuController gameController = loader.getController();

            // Lấy stage hiện tại từ nút đăng nhập
            Stage stage = (Stage) usernameField.getScene().getWindow();
            gameController.setServerConnection(serverConnection, stage);
            stage.setScene(scene);
            stage.setTitle("Giao diện Game");
            gameController.setUser(user);
            gameController.setThread();
            gameController.setup();
            // Thiết lập sự kiện đóng cửa sổ để cập nhật trạng thái offline
            stage.setOnCloseRequest(event -> {
                    serverConnection.sendMessage("setOffline");
                    serverConnection.sendMessage(user.getUsername());
            });

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải giao diện game.");
        }
    }
    private void switchToAdminScreen(User user) {
        try {
            // Tải giao diện Dashboard.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/Dashboard.fxml"));
            Scene scene = new Scene(loader.load());

            DashboardUIHandler dashboardUIHandler = loader.getController();
            dashboardUIHandler.setAdmin(user);


            // Lấy stage hiện tại từ nút đăng nhập
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Giao diện Admin");
            stage.setResizable(false);
            stage.centerOnScreen();

            dashboardUIHandler.setOnClose(stage);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải giao diện game.");
        }
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
