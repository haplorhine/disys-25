module org.example.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires spring.webflux;
    requires reactor.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;


    opens org.example.gui to javafx.fxml;
    exports org.example.gui;
}