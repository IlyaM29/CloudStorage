module com.example.cloudstorage {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cloudstorage to javafx.fxml;
    exports com.example.cloudstorage;
}