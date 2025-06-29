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

/**
 * Controller um die Daten anzuzeigen.
 * Es werden je nach gewähltem Button die Daten mittels RestService ermittelt.
 *
 */
public class EnergyGuiController {
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

    XYChart.Series<String, Number> total = new XYChart.Series<>();
    XYChart.Series<String, Number> community = new XYChart.Series<>();
    XYChart.Series<String, Number> grid = new XYChart.Series<>();
    @FXML
    private Label welcomeText;

    /**
     * onGetCurrentUsage
     * ermitteln der aktuellen Werte
     * Es wird nur der WEbserver aufgerufen, die Daten danach angezeigt.
     *
     * @param actionEvent
     */
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

    /**
     * Aufbereiten einer Liste von Strings mit den möglichen Zeiträumen
     *
     *
     * @return Liste von Strings mit den möglichen Stunden
     */
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

    /**
     * initialize
     * Initialisierungen beim Start durchführen.
     * Die Auswahlfelder werden mit den möglichen Zeiträumen belegt.
     * Die Tabellenspalten werden vorbereitet, damit die richtigen Daten angezeigt werden.
     *
     */
    @FXML
    private void initialize() {
        ObservableList<LocalDateTime> dataList = FXCollections.observableArrayList(getHours());
        startTime.setItems(dataList);
        endTime.setItems(dataList);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        timeColumn.setCellValueFactory(cellData ->   new SimpleStringProperty(
                        cellData.getValue().getTimeHour().format(formatter)
                )
        );

        communityProducedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getCommunityProduced()));
        // Spalte
        communityUsedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getCommunityUsed()));

        // Spalte
        gridUsedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getGridUsed()));

    }

    /**
     * onShowData
     * Anzeigen der Daten zusammengerechnet für den ausgewählten Zeitraum
     *
     * Die Start- und die Endzeit muss ausgewählt sein. Wenn nicht, wird ein Fehler ausgegeben
     *
     * Danach wird der WebServer aufgerufen um die Summe zu ermitteln.
     * Das Ergebnis wird in den Feldern angezeigt.
     *
     *
     * @param actionEvent
     */
    @FXML
    private void onShowData(ActionEvent actionEvent) {

        welcomeText.setText("");
        if (startTime.getValue() == null  || endTime.getValue() == null) {
            welcomeText.setText("Start- und oder Endzeit nicht gesetzt");
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String response = getResponse("energy/history?start=" +
                    URLEncoder.encode(startTime.getValue().format(formatter), StandardCharsets.UTF_8) +
                    "&ende=" + URLEncoder.encode(endTime.getValue().format(formatter), StandardCharsets.UTF_8));
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            // Ausgabe zur Kontrolle
            Energy energy = mapper.readValue(response, Energy.class);
            resultCommunityProduced.setText(energy.getCommunityProduced().toString());
            resultCommunityUsed.setText(energy.getCommunityUsed().toString());
            resultGridUsed.setText(energy.getGridUsed().toString());

        } catch (Exception e) {
            e.printStackTrace();
            welcomeText.setText("ERROR: " + e.getLocalizedMessage());
        }
    }

    /**
     * Ermitteln und aufbereiten der Detaildaten für die anzeige
     * Zuerst wird überprüft, ob die Start- und Endzeit ausgewählt wurden.
     * ist dem nicht der Fall, wird ein Fehler ausgegeben.
     *
     * Sind die Werte gesetzt, wird das Rest-Service für die Detaildaten aufgerufen.
     *
     * Danach werden die Tabelle und das Diagramm aufgebaut.
     *
     * @param actionEvent
     */
    @FXML
    private void onGetDetailData(ActionEvent actionEvent) {
        welcomeText.setText("");
        if (startTime.getValue() == null  || endTime.getValue() == null) {

            welcomeText.setText("Start- und oder Endzeit nicht gesetzt");
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
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

    /**
     * getResponse
     * geordnet den Web-Server aufrufen.
     *
     *
     * @param url - pfad des RestServices
     * @return JSON-Objekt welches vom Server gelesen wurde
     * @throws IOException
     * @throws InterruptedException
     */
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