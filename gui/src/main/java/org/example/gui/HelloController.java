package org.example.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
public class HelloController {
    @FXML
    public TextField name;
    public TextField systemPercentage;
    public TextField gridPortion;
    public ComboBox<String> startTime;
    public ComboBox<String> endTime;
    public TextField resultCommunityUsed;
    public TableColumn<Map.Entry<String, Energy>, Double> CommunityUsedColumn;
    public TableColumn<Map.Entry<String, Energy>, String> timeColumn;
    public TableView<Map.Entry<String, Energy>> detailTableData;
    @FXML
    private Label welcomeText;

   @FXML
    private void onGetCurrentUsage(ActionEvent actionEvent) {

        try {
            String urlString = "http://localhost:8080/energy/current";
            System.out.println(urlString);

            var request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Status Code: " + response.statusCode());
            ObjectMapper mapper = new ObjectMapper();
            EnergyPercentage ep = mapper.readValue(response.body(), EnergyPercentage.class);
            systemPercentage.setText(ep.getCurrentCommunityPool().toString());
            gridPortion.setText(ep.getGridPorton().toString());
        } catch (Exception e) {
            e.printStackTrace();
            welcomeText.setText("ERROR: " + e.getLocalizedMessage());
        }
    }

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

    @FXML
    private void initialize() {
        startTime.setItems(FXCollections.observableArrayList(getHours()));
        endTime.setItems(FXCollections.observableArrayList(getHours()));

         timeColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getKey()));

        // Spalte: Value (Sonnenschein in Minuten)
        CommunityUsedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> cellData.getValue().getValue().getCommunityProduced()));



    }

    @FXML
    private void onShowData(ActionEvent actionEvent) {
        try {
            String urlString = "http://localhost:8080/energy/history?start=" +
                    URLEncoder.encode(startTime.getValue(), StandardCharsets.UTF_8) +
                     "&ende=" + URLEncoder.encode(endTime.getValue(), StandardCharsets.UTF_8);
            System.out.println(urlString);

            var request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Status Code: " + response.statusCode());
            ObjectMapper mapper = new ObjectMapper();
            Energy energy = mapper.readValue(response.body(), Energy.class);
            resultCommunityUsed.setText(energy.getCommunityUsed().toString());

        } catch (Exception e) {
            e.printStackTrace();
            welcomeText.setText("ERROR: " + e.getLocalizedMessage());
        }
    }

    public void onGetDetailData(ActionEvent actionEvent) {
        try {
            String urlString = "http://localhost:8080/energy/detail?start=" +
                    URLEncoder.encode(startTime.getValue(), StandardCharsets.UTF_8) +
                    "&ende=" + URLEncoder.encode(endTime.getValue(), StandardCharsets.UTF_8);
            System.out.println(urlString);

            var request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Status Code: " + response.statusCode());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Energy> detailData = mapper.readValue(response.body(), new TypeReference<LinkedHashMap<String, Energy>>() {
            });

            ObservableList<Map.Entry<String, Energy>> tableData =
                    FXCollections.observableArrayList(detailData.entrySet());
            detailTableData.setItems(tableData);
        } catch (Exception e) {
            e.printStackTrace();
            welcomeText.setText("ERROR: " + e.getLocalizedMessage());
        }
    }
}