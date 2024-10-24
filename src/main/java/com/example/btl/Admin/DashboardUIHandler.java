package com.example.btl.Admin;

import com.example.btl.Admin.controller.DashboardController;
import com.example.btl.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardUIHandler implements Initializable {

    private DashboardController dashboardController;

    private User admin;

    @FXML
    private Label lblAdminName;

    @FXML
    private TextField searchInfoKey;

    @FXML
    private Button btnInfoPage;

    @FXML
    private ImageView btnLogout;

    @FXML
    private Button btnRankingPage;

    @FXML
    private Button btnSearchInfo;

    @FXML
    private Button btnSearchRank;

    @FXML
    private TableColumn<User, Integer> colDrawRT;

    @FXML
    private TableColumn<User, Integer> colLoseRT;

    @FXML
    private TableColumn<User, Integer> colNumMatchRT;

    @FXML
    private TableColumn<User, String> colPlayerRT;

    @FXML
    private TableColumn<User, Integer> colRankingRT;

    @FXML
    private TableColumn<User, Integer> colScoreRT;

    @FXML
    private TableColumn<User, Integer> colWinRT;

    @FXML
    private TableColumn<User, String> colWinRateRT;

    @FXML
    private Pagination paginationRT;

    // Bang thong tin nguoi dung
    @FXML
    private TableView<User> paginationTableInfo;

    @FXML
    private TableColumn<User, String> colNameInfo;

    @FXML
    private TableColumn<User, String> colStatusInfo;

    @FXML
    private Pagination paginationInfo;

    @FXML
    private Label cardDraw;

    @FXML
    private Label cardLose;

    @FXML
    private Label cardName;

    @FXML
    private Label cardRank;

    @FXML
    private Label cardScore;

    @FXML
    private Label cardStatus;

    @FXML
    private Label cardTotalGame;

    @FXML
    private Label cardWin;

    @FXML
    private Label cardWinRate;

    @FXML
    private AnchorPane playerCardInfo;

    @FXML
    private TableView<User> rankTable;

    @FXML
    private AnchorPane rankingPage;

    @FXML
    private AnchorPane searchInfoPage;

    @FXML
    private TextField searchMaxValue;

    @FXML
    private TextField searchMinValue;

    @FXML
    private Label lblNumPlayer;

    @FXML
    private Label lblNumOnlinePlayer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Thiet lap ket noi toi server
        dashboardController = new DashboardController();
        try {
            dashboardController.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể kết nối tới server");
            alert.setContentText("Vui lòng thử lại sau");
            alert.showAndWait();
            System.exit(0);
        }

        //Xu ly su kien chuyen trang
        btnInfoPage.setOnAction(e -> {
            searchInfoPage.setVisible(true);
            rankingPage.setVisible(false);
        });

        btnRankingPage.setOnAction(e -> {
            searchInfoPage.setVisible(false);
            rankingPage.setVisible(true);
        });

        // Thiet lap cac cot cua bang Info
        colNameInfo.setCellValueFactory(new PropertyValueFactory<>("username"));
        colStatusInfo.setCellValueFactory(new PropertyValueFactory<>("status"));

        // THiet lap cac cot cua bang Ranking
        colRankingRT.setCellValueFactory(cellData -> {
            // Lấy thứ tự của người chơi trong danh sách
            int currentPage = paginationRT.getCurrentPageIndex();
            int rank = (currentPage * 10) + rankTable.getItems().indexOf(cellData.getValue()) + 1;
            return new SimpleIntegerProperty(rank).asObject();
        });
        colPlayerRT.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNumMatchRT.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTotalGames()).asObject());
        colWinRT.setCellValueFactory(new PropertyValueFactory<>("win"));
        colDrawRT.setCellValueFactory(new PropertyValueFactory<>("draw"));
        colLoseRT.setCellValueFactory(new PropertyValueFactory<>("loss"));
        colScoreRT.setCellValueFactory(new PropertyValueFactory<>("totalPoints"));
        colWinRateRT.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getWinRate()));

        // Hien thi thong so thong ke
        lblNumPlayer.setText(dashboardController.getNumOfPlayers() == -1 ? "N/A" : String.valueOf(dashboardController.getNumOfPlayers()));
        lblNumOnlinePlayer.setText(dashboardController.getNumOfOnlinePlayers() == -1 ? "N/A" : String.valueOf(dashboardController.getNumOfOnlinePlayers()));

        // Khởi tạo Timeline để cập nhật thông tin định kỳ
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            lblNumPlayer.setText(dashboardController.getNumOfPlayers() == -1 ? "N/A" : String.valueOf(dashboardController.getNumOfPlayers()));
            lblNumOnlinePlayer.setText(dashboardController.getNumOfOnlinePlayers() == -1 ? "N/A" : String.valueOf(dashboardController.getNumOfOnlinePlayers()));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void setAdmin(User user) {
        this.admin = user;
        // Cap nhat lai giao dien
        lblAdminName.setText(admin.getUsername());
    }

    public void setOnClose(Stage stage) {
        // Đặt sự kiện đóng cửa sổ sẽ thực hiện logout
        stage.setOnCloseRequest(event -> {
            // Hiển thị hộp thoại xác nhận
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận");
            alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất và thoát?");
            if (alert.showAndWait().get() == ButtonType.OK) {
                if(dashboardController.logout(admin.getUsername())) {
                    dashboardController.closeConnection();
                    System.exit(0);
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Lỗi");
                    errorAlert.setHeaderText("Đã xảy ra lỗi khi đăng xuất");
                    errorAlert.setContentText("Vui lòng thử lại sau");
                    errorAlert.showAndWait();
                    event.consume();  // Hủy việc đóng cửa sổ nếu có lỗi
                }
            } else {
                event.consume();  // Hủy việc đóng cửa sổ nếu người dùng chọn "Cancel"
            }
        });
    }

    // Các phương thức xử lý sự kiện
    @FXML
    void clickOnSearchInfo(MouseEvent event) {
        String keySearch = searchInfoKey.getText();
        List<User> users = dashboardController.searchUserByName(keySearch);

        // Lọc ra chỉ các người chơi (role = 0)
        List<User> players = users.stream()
                .filter(user -> user.getRole() == 0)
                .toList();

        int count = players.size();
        int pageCount = (count / 10) + 1;
        paginationInfo.setPageCount(pageCount);
        paginationInfo.setPageFactory((Integer pageIndex) -> {
            int fromIndex = pageIndex * 10;
            int toIndex = Math.min(fromIndex + 10, count);
            paginationTableInfo.setItems(FXCollections.observableArrayList(players.subList(fromIndex, toIndex)));
            return paginationTableInfo;
        });
    }

    @FXML
    void onSelectedPlayer(MouseEvent event) {
        if (event.getClickCount() == 1 && event.getButton().equals(MouseButton.PRIMARY)) {
            User selectedPlayer = paginationTableInfo.getSelectionModel().getSelectedItem();

            if (selectedPlayer != null) {
                // Cập nhật thông tin của người chơi đã chọn vào card info
                cardName.setText(selectedPlayer.getUsername());
                cardStatus.setText(selectedPlayer.getStatus());
                cardWin.setText(String.valueOf(selectedPlayer.getWin()));
                cardLose.setText(String.valueOf(selectedPlayer.getLoss()));
                cardDraw.setText(String.valueOf(selectedPlayer.getDraw()));
                int totalGame = selectedPlayer.getWin() + selectedPlayer.getLoss() + selectedPlayer.getDraw();
                float totalPoints = selectedPlayer.getWin() + 0.5f*selectedPlayer.getDraw();
                float winRate = totalGame == 0 ? 0 : (selectedPlayer.getWin() * 100.0f / totalGame);
                cardTotalGame.setText(String.valueOf(totalGame));
                cardScore.setText(String.valueOf(totalPoints));
                cardWinRate.setText(String.format("%.2f", winRate) + "%");

                int rank = dashboardController.getPlayerRank(selectedPlayer.getUsername());
                cardRank.setText(rank == -1 ? "N/A" : "#"+rank);
                playerCardInfo.setVisible(true);
            }
        }
    }

    @FXML
    void onLogoutClicked(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if(dashboardController.logout(admin.getUsername())) {
                dashboardController.closeConnection();
                System.exit(0);
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Lỗi");
                errorAlert.setHeaderText("Đã xảy ra lỗi khi đăng xuất");
                errorAlert.setContentText("Vui lòng thử lại sau");
            }
        }
    }

    @FXML
    void onSearchRankBtnClicked(MouseEvent event) {
        String keySearch = searchInfoKey.getText();
        if(searchMinValue.getText().isEmpty() || searchMaxValue.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Vui lòng nhập giá trị tìm kiếm");
            alert.showAndWait();
            return;
        }
        if(!searchMinValue.getText().matches("\\d+") || !searchMaxValue.getText().matches("\\d+")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Vui lòng nhập giá trị số nguyên");
            alert.showAndWait();
            return;
        }
        List<User> users = dashboardController.getPlayersInRank(Integer.parseInt(searchMinValue.getText()), Integer.parseInt(searchMaxValue.getText()));

        int count = users.size();
        int pageCount  = (count/10) + 1;
        paginationRT.setPageCount(pageCount);
        paginationRT.setPageFactory((Integer pageIndex) -> {
            int fromIndex = pageIndex * 10;
            int toIndex = Math.min(fromIndex + 10, count);
            rankTable.setItems(FXCollections.observableArrayList(users.subList(fromIndex, toIndex)));
            return rankTable;
        });
    }

    @FXML
    void onCheckHistoryClicked(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText("Chức năng này đang được phát triển");
        alert.showAndWait();
    }

    @FXML
    void onDelClicked(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa người chơi này?");

        if (alert.showAndWait().get() == ButtonType.OK) {

            if(!cardName.equals("Unknow")) {
                if(dashboardController.deleteUser(cardName.getText())) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thành công");
                    successAlert.setHeaderText("Xóa người chơi thành công");
                    successAlert.showAndWait();
                    playerCardInfo.setVisible(false);
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Lỗi");
                    errorAlert.setHeaderText("Đã xảy ra lỗi khi xóa người chơi");
                    errorAlert.setContentText("Vui lòng thử lại sau");
                    errorAlert.showAndWait();
                }
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Lỗi");
                errorAlert.setHeaderText("Vui lòng chọn người chơi cần xóa");
                errorAlert.showAndWait();
            }
        }
    }
}