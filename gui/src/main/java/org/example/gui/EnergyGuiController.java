package org.example.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// controller-klasse für die javafx-gui (frontend-logik)
// wird automatisch über die fxml-datei „energy-gui-view.fxml“ geladen und mit der oberfläche verknüpft
// ruft spring-rest-endpunkte auf (z.B. /energy/current) → lädt daten vom server, zeigt sie in textfeldern, tabellen und diagrammen an
// verwendet javafx-elemente wie textfield, combobox, tableview, linechart
// nutzt objectmapper (jackson) zum umwandeln von json in java-objekte
public class EnergyGuiController {
    // --- GUI-Elemente, automatisch vom FXML-Loader injiziert ---
    // @FXML kennzeichnet JavaFX-Elemente, die mit Komponenten aus energy-gui-view.fxml verbunden werden
    @FXML
    public TextField name;
    public TextField systemPercentage;
    public TextField gridPortion;
    public ComboBox<LocalDateTime> startTime;
    public ComboBox<LocalDateTime> endTime;
    public TextField resultCommunityUsed;
    public TableColumn<Energy,  Double> communityUsedColumn;
    public TableColumn<Energy, String> timeColumn;
    public TableView< Energy> detailTableData;
    public LineChart<String, Number> diagrammTableData;
    public TableColumn<Energy, Double> communityProducedColumn;
    public TableColumn<Energy, Double> gridUsedColumn;
    public TextField resultGridUsed;
    public TextField resultCommunityProduced;

    // --- Datenreihen für das Liniendiagramm (Diagramm-Tab) ---
    // XYChart.Series steht für eine Serie von (x,y)-Werten im Liniendiagramm
    XYChart.Series<String, Number> total = new XYChart.Series<>();
    XYChart.Series<String, Number> community = new XYChart.Series<>();
    XYChart.Series<String, Number> grid = new XYChart.Series<>();
    @FXML
    private Label welcomeText;

    // @FXML → Methode wird automatisch mit Button im FXML („onAction“) verknüpft
    // ruft über HTTP den Spring-Rest-Endpunkt /energy/current auf
    // setzt die empfangenen Werte in JavaFX-Textfelder (systemPercentage & gridPortion)
    @FXML
    private void onGetCurrentUsage(ActionEvent actionEvent) {
        try {
            String response = getResponse("energy/current");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            EnergyPercentage ep = mapper.readValue(response, EnergyPercentage.class);
            systemPercentage.setText(ep.getCurrentCommunityPool().toString());
            gridPortion.setText(ep.getGridPorton().toString());
        } catch (Exception e) {
            e.printStackTrace();
            welcomeText.setText("ERROR: " + e.getLocalizedMessage());
        }
    }

    // ruft /energy/getValidData auf → liste aller verfügbaren stundenzeitpunkte vom spring-backend
    // → verwendet für Dropdown-Auswahl in JavaFX (startTime, endTime)
    private List<LocalDateTime> getHours() {
        try {
            String response = getResponse("energy/getValidData");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            // Ausgabe zur Kontrolle
            return mapper.readValue(response, new TypeReference<List<LocalDateTime>>() {
            });
        } catch (ConnectException ce){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fehler");
            alert.setHeaderText(null); // kein Header
            alert.setContentText("Leider ist der Server nicht erreichbar. Die Applikation wird beendet");
            alert.showAndWait();
            System.exit(1);
    }
        catch (Exception e) {
            e.printStackTrace();
            welcomeText.setText("ERROR: " + e.getLocalizedMessage());
        }
        return null;
    }

    // wird automatisch beim Start der GUI aufgerufen (wenn FXML geladen wird)
    // → füllt Dropdowns (start/endTime) mit Stunden vom Backend (via REST)
    // → konfiguriert Tabellenansicht (Mapping der Energy-Objekte auf die Columns)
    @FXML
    private void initialize() {
        ObservableList<LocalDateTime> dataList = FXCollections.observableArrayList(getHours());
        startTime.setItems(dataList);
        endTime.setItems(dataList);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        // stellt dar, wie die „Zeit“-Spalte der TableView gefüllt wird (Column-Binding)
        // → getTimeHour() liefert ein LocalDateTime, das per formatter in lesbares Datum/Uhrzeit-Format umgewandelt wird
        // → SimpleStringProperty macht daraus eine „JavaFX Property“, damit sie korrekt angezeigt & bei Änderungen aktualisiert wird
        timeColumn.setCellValueFactory(cellData ->   new SimpleStringProperty(
                        cellData.getValue().getTimeHour().format(formatter)
                )
        );
        // bindet die Spalten an die entsprechende Property des Energy-Objekts
        communityProducedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getCommunityProduced()));
        communityUsedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getCommunityUsed()));
        gridUsedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getGridUsed()));
    }

    // zeigt die aufsummierten Energiedaten für den ausgewählten Zeitraum an
    // @FXML → Methode ist mit „show data“-Button im FXML über onAction verknüpft
    @FXML
    private void onShowData(ActionEvent actionEvent) {
        welcomeText.setText("");
        if (startTime.getValue() == null  || endTime.getValue() == null) {
            welcomeText.setText("Start- und oder Endzeit nicht gesetzt");
            return;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            // → sendet GET-Anfrage an Spring-Rest-Endpunkt /energy/history?start=...&ende=...
            String response = getResponse("energy/history?start=" +
                    URLEncoder.encode(startTime.getValue().format(formatter), StandardCharsets.UTF_8) +
                    "&ende=" + URLEncoder.encode(endTime.getValue().format(formatter), StandardCharsets.UTF_8));
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            // → Response enthält Gesamtwerte für erzeugt, verbraucht, Netzbezug (als JSON → Energy-Objekt)
            Energy energy = mapper.readValue(response, Energy.class);
            // → setzt die Textfelder (resultCommunityProduced, resultCommunityUsed, resultGridUsed) in der GUI
            resultCommunityProduced.setText(energy.getCommunityProduced().toString());
            resultCommunityUsed.setText(energy.getCommunityUsed().toString());
            resultGridUsed.setText(energy.getGridUsed().toString());
        } catch (Exception e) {
            e.printStackTrace();
            welcomeText.setText("ERROR: " + e.getLocalizedMessage());
        }
    }

    // lädt detaildaten pro stunde aus dem backend (→ /energy/detail?start=...&ende=...)
    // @FXML → methode ist mit dem „detaildaten“-button im FXML via onAction verknüpft
    // daten werden als liste von energy-objekten geladen (eine stunde = ein eintrag)
    // → anzeige in javafx-tableview + update des diagramms mit stundenwerten
    @FXML
    private void onGetDetailData(ActionEvent actionEvent) {
        welcomeText.setText("");
        if (startTime.getValue() == null  || endTime.getValue() == null) {
            welcomeText.setText("Start- und oder Endzeit nicht gesetzt");
            return;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            // → http-get an spring-rest-endpunkt /energy/detail mit zeitraum
            String response = getResponse("energy/detail?start=" +
                    URLEncoder.encode(startTime.getValue().format(formatter), StandardCharsets.UTF_8) +
                    "&ende=" + URLEncoder.encode(endTime.getValue().format(formatter), StandardCharsets.UTF_8));
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            // Ausgabe zur Kontrolle
            List<Energy> detailData = mapper.readValue(response, new TypeReference<List<Energy>>() {
            });

            ObservableList<Energy> tableData =
                    FXCollections.observableArrayList(detailData);
            detailTableData.setItems(tableData);

            diagrammTableData.getData().removeAll(total, community, grid);

            total = new XYChart.Series<>();
            community = new XYChart.Series<>();
            grid = new XYChart.Series<>();
            DateTimeFormatter tableFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

            detailData.forEach(( value) -> {
                total.getData().add(new XYChart.Data<>(value.getTimeHour().format(tableFormatter), value.getCommunityProduced()));
                community.getData().add(new XYChart.Data<>(value.getTimeHour().format(tableFormatter), value.getCommunityUsed()));
                grid.getData().add(new XYChart.Data<>(value.getTimeHour().format(tableFormatter), value.getGridUsed()));

            });
            diagrammTableData.getData().addAll(total, community, grid);
        } catch (Exception e) {
            e.printStackTrace();
            welcomeText.setText("ERROR: " + e.getLocalizedMessage());
        }
    }

    // hilfsmethode zum aufruf von rest-endpunkten (spring)
    // → verwendet java 11 httpclient (java.net.http) für GET-request auf localhost:8080/...
    // → prüft response-code (200 = ok, 404 = keine daten, sonst: fehler)
    // → gibt json-response-body als string zurück (wird später via jackson weiterverarbeitet)
    protected String getResponse(String url) throws IOException, InterruptedException {
        String urlString = "http://localhost:8080/" + url;
        System.out.println(urlString);

        var request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else if (response.statusCode() == 404) {
            throw new RuntimeException("Keine Daten vorhanden");
        } else {
            throw new RuntimeException("Fehler beim Abrufen: HTTP " + response.statusCode());
        }
    }
}