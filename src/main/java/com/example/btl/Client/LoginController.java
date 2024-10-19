package com.example.btl.Client;

import com.example.btl.Admin.DashboardUIHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import com.example.btl.User;
import com.example.btl.Server.ServerConnection;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;
    private ServerConnection serverConnection;

    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    // Xử lý sự kiện khi người dùng nhấn nút "Login"
    @FXML
    public void handleLoginButtonAction() {
        try {
            ServerConnection serverConnection = new ServerConnection();
            serverConnection.connect("localhost", 12345);
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
            showAlert("Lỗi", "Không thể kết nối tới server.");
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
            gameController.setUser(user);
            gameController.setServerConnection(serverConnection);
            gameController.setup();

            // Lấy stage hiện tại từ nút đăng nhập
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Giao diện Game");

//            // Thiết lập sự kiện đóng cửa sổ để cập nhật trạng thái offline
//            stage.setOnCloseRequest(event -> {
//                try (Socket socket = new Socket("localhost", 12345);
//                     PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {
//
//                    // Gửi request logout để cập nhật trạng thái offline
//                    output.println("setOffline");
//                    output.println(user.getUsername());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });

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

            stage.setOnCloseRequest(event -> {
                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

                    // Gửi request logout để cập nhật trạng thái offline
                    output.println("setOffline");
                    output.println(user.getUsername());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

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
