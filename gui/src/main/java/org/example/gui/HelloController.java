package org.example.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller um die Daten anzuzeigen.
 * Es werden je nach gewähltem Button die Daten mittels RestService ermittelt.
 *
 */
public class HelloController {
    @FXML
    public TextField name;
    public TextField systemPercentage;
    public TextField gridPortion;
    public ComboBox<String> startTime;
    public ComboBox<String> endTime;
    public TextField resultCommunityUsed;
    public TableColumn<Map.Entry<String, Energy>, Double> communityUsedColumn;
    public TableColumn<Map.Entry<String, Energy>, String> timeColumn;
    public TableView<Map.Entry<String, Energy>> detailTableData;
    public LineChart<String, Number> diagrammTableData;
    public TableColumn<Map.Entry<String, Energy>, Double> communityProducedColumn;
    public TableColumn<Map.Entry<String, Energy>, Double> gridUsedColumn;
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
    private List<String> getHours() {
        List<String> timestamps = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (int day = 0; day < 7; day++) {
            LocalDate date = startDate.plusDays(day);
            for (int hour = 0; hour < 24; hour++) {
                LocalDateTime dateTime = date.atTime(hour, 0);
                timestamps.add(dateTime.format(formatter));
            }
        }

        // Ausgabe zur Kontrolle
        return timestamps;

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
        startTime.setItems(FXCollections.observableArrayList(getHours()));
        endTime.setItems(FXCollections.observableArrayList(getHours()));

        timeColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getKey()));

        communityProducedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getValue().getCommunityProduced()));
        // Spalte
        communityUsedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getValue().getCommunityUsed()));

        // Spalte
        gridUsedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getValue().getGridUsed()));

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
        if (startTime.getValue() == null || startTime.getValue().isEmpty() || endTime.getValue() == null|| endTime.getValue().isEmpty()) {
            welcomeText.setText("Start- und oder Endzeit nicht gesetzt");
            return;
        }

        try {
            String response = getResponse("energy/history?start=" +
                    URLEncoder.encode(startTime.getValue(), StandardCharsets.UTF_8) +
                    "&ende=" + URLEncoder.encode(endTime.getValue(), StandardCharsets.UTF_8));
            ObjectMapper mapper = new ObjectMapper();
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
        if (startTime.getValue() == null || startTime.getValue().isEmpty() || endTime.getValue() == null|| endTime.getValue().isEmpty()) {

            welcomeText.setText("Start- und oder Endzeit nicht gesetzt");
            return;
        }

        try {
            String response = getResponse("energy/detail?start=" +
                    URLEncoder.encode(startTime.getValue(), StandardCharsets.UTF_8) +
                    "&ende=" + URLEncoder.encode(endTime.getValue(), StandardCharsets.UTF_8));

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Energy> detailData = mapper.readValue(response, new TypeReference<LinkedHashMap<String, Energy>>() {
            });

            ObservableList<Map.Entry<String, Energy>> tableData =
                    FXCollections.observableArrayList(detailData.entrySet());
            detailTableData.setItems(tableData);

            diagrammTableData.getData().removeAll(total, community, grid);

            total = new XYChart.Series<>();
            community = new XYChart.Series<>();
            grid = new XYChart.Series<>();

            detailData.forEach((key, value) -> {
                total.getData().add(new XYChart.Data<>(key, value.getCommunityProduced()));
                community.getData().add(new XYChart.Data<>(key, value.getCommunityUsed()));
                grid.getData().add(new XYChart.Data<>(key, value.getGridUsed()));

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

        System.out.println("Status-Code " + response.statusCode());
        return response.body();
    }
}