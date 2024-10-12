module com.example.btl {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics; // nếu bạn sử dụng module này

    opens com.example.btl to javafx.fxml;
    exports com.example.btl;
    exports com.example.btl.Client;
    opens com.example.btl.Client to javafx.fxml;
}
