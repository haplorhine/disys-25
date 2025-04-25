module org.example.gui {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.gui to javafx.fxml;
    exports org.example.gui;
}