package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
// hauptklasse der gui-anwendung (javafx)
// - wird von javafx gestartet (extends javafx.application.Application)
// - lädt die fxml-datei energy-gui-view.fxml
// - verwendet javafx.fxml.FXMLLoader, javafx.scene.Scene, javafx.stage.Stage → alles aus dem javafx-framework
// - beim start wird ein fenster mit dem titel "Energy Community Monitor" geöffnet
// - verbunden mit controller: org.example.gui.EnergyGuiController (in fxml definiert)
public class EnergyGuiApplication extends Application {
    // wird von javafx beim start automatisch aufgerufen
    // - lädt das fxml-layout
    // - setzt die größe (breite 620px, höhe dynamisch)
    // - setzt den fenstertitel und zeigt das hauptfenster an
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EnergyGuiApplication.class.getResource("energy-gui-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 620, Region.USE_COMPUTED_SIZE);
        stage.setTitle("Energy Community Monitor");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}