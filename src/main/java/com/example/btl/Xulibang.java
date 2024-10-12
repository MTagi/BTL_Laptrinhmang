package com.example.btl;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Xulibang extends Application {
    private TableView<Player> tableView;
    private ObservableList<Player> playerList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Tạo bảng và các cột
        tableView = new TableView<>();
        TableColumn<Player, Boolean> selectCol = new TableColumn<>("Select");
        selectCol.setCellValueFactory(param -> param.getValue().selectedProperty());
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));

        TableColumn<Player, String> nameCol = new TableColumn<>("Tên");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Player, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));

        TableColumn<Player, String> actionCol = new TableColumn<>("Chấp nhận");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button acceptButton = new Button("Chấp nhận");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(acceptButton);
                }
            }
        });

        // Thêm cột vào bảng
        tableView.getColumns().addAll(selectCol, nameCol, levelCol, actionCol);

        // Tạo dữ liệu mẫu
        playerList = FXCollections.observableArrayList(
                new Player("Player 1", 10),
                new Player("Player 2", 3),
                new Player("Player 3", 8),
                new Player("Player 4", 1),
                new Player("Player 5", 5)
        );

        tableView.setItems(playerList);

        // Thêm Pagination
        Pagination pagination = new Pagination();
        pagination.setPageCount(2);
        pagination.setPageFactory(this::createPage);

        // Số hàng hiển thị
        ComboBox<Integer> rowsPerPage = new ComboBox<>();
        rowsPerPage.setItems(FXCollections.observableArrayList(5, 10, 15));
        rowsPerPage.setValue(5);

        // Layout chính
        VBox vbox = new VBox(tableView, pagination, rowsPerPage);
        Scene scene = new Scene(vbox, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Player Table");
        primaryStage.show();
    }

    private VBox createPage(int pageIndex) {
        int rowsPerPage = 5;  // Số hàng mỗi trang
        int start = pageIndex * rowsPerPage;
        int end = Math.min(start + rowsPerPage, playerList.size());
        tableView.setItems(FXCollections.observableArrayList(playerList.subList(start, end)));
        return new VBox(tableView);
    }

    public static class Player {
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty level;
        private final SimpleBooleanProperty selected;

        public Player(String name, int level) {
            this.name = new SimpleStringProperty(name);
            this.level = new SimpleIntegerProperty(level);
            this.selected = new SimpleBooleanProperty(false);
        }

        public String getName() {
            return name.get();
        }

        public int getLevel() {
            return level.get();
        }

        public SimpleBooleanProperty selectedProperty() {
            return selected;
        }
    }
}
