module com.example.cloudstorage {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.transport;
    requires lombok;
    requires io.netty.codec;


    opens com.example.cloudstorage to javafx.fxml;
    exports com.example.cloudstorage;
}