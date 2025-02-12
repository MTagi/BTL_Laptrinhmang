module com.example.btl {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop; // nếu bạn sử dụng module này

    opens com.example.btl to javafx.fxml;
    exports com.example.btl;
    exports com.example.btl.Client;
    opens com.example.btl.Client to javafx.fxml;
    exports com.example.btl.Admin;
    opens com.example.btl.Admin to javafx.fxml;
    exports com.example.btl.Admin.controller;
    opens com.example.btl.Admin.controller to javafx.fxml;
    exports com.example.btl.Server;
    opens com.example.btl.Server to javafx.fxml;
}