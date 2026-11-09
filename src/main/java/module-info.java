module com.example.splitdish {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;


    opens com.example.splitdish to javafx.fxml;
    exports com.example.splitdish;
}