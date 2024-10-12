package com.example.btl.Client;

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

public class MainMenuController {
    private User dataUser;
    // lấy dữ liệu từ giao diện Login.
    public void setUser(User data) {
        this.dataUser = data; // Lưu dữ liệu nhận được
    }

        @FXML
        private TextField usernameField;

        @FXML
        private TextField passwordField;

        // Xử lý sự kiện khi người dùng nhấn nút "Login"
        @FXML
        public void handleLoginButtonAction() {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Lỗi", "Tên đăng nhập và mật khẩu không được để trống.");
                return;
            }

            // Gửi request đăng nhập tới server
            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Gửi lệnh login và thông tin đăng nhập
                output.println("login");
                output.println(username);
                output.println(password);

                // Nhận phản hồi từ server
                String response = input.readLine();

                if ("success".equals(response)) {
                    // Nhận dữ liệu người dùng từ server
                    String usernameReceived = input.readLine();
                    String passwordReceived = input.readLine();
                    String gmail = input.readLine();
                    int win = Integer.parseInt(input.readLine());
                    int draw = Integer.parseInt(input.readLine());
                    int loss = Integer.parseInt(input.readLine());
                    int totalPoints = Integer.parseInt(input.readLine());
                    String status = input.readLine();
                    int role = Integer.parseInt(input.readLine());

                    // Tạo đối tượng User
                    User user = new User(usernameReceived, passwordReceived, gmail, win, draw, loss, totalPoints, status, role);

                    // Chuyển sang giao diện game và truyền đối tượng User
                    switchToGameScreen(user);
                } else {
                    // Nếu đăng nhập thất bại, hiển thị thông báo
                    showAlert("Thất bại", "Tài khoản hoặc mật khẩu không chính xác.");
                }

            } catch (Exception e) {
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

//            // Lấy GameController và truyền đối tượng User
//            GameController gameController = loader.getController();
//            gameController.setUser(user);

                // Lấy stage hiện tại từ nút đăng nhập
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Giao diện Game");

                // Thiết lập sự kiện đóng cửa sổ để cập nhật trạng thái offline
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

