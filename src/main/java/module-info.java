module com.example.cloudstorage {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.transport;
    requires lombok;
    requires org.slf4j;
    requires io.netty.codec;
    requires java.desktop;


    opens com.example.cloudstorage to javafx.fxml;
    exports com.example.cloudstorage;
    exports com.example.cloudstorage.client;
}