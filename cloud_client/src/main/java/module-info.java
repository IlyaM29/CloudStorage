module com.example.cloudstorage.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.transport;
    requires lombok;
    requires org.slf4j;
    requires io.netty.codec;
    requires java.desktop;
    requires com.example.cloudstorage.model;


    opens com.example.cloudstorage.client to javafx.fxml;
    exports com.example.cloudstorage.client;
}