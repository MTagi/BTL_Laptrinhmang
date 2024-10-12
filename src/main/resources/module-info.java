module com.example.btl {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports com.example.btl.Client;
    opens com.example.btl.Client to javafx.fxml; // Để FXML có thể truy cập vào controller
}
