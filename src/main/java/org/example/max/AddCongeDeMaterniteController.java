package org.example.max;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.example.max.TypeExtraPostManager.getConnection;
import static org.example.max.work_space.idx;

public class AddCongeDeMaterniteController {

    @FXML
    private TextField idEnseignantField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label countdownLabel;

    private Timeline countdownTimeline;

    @FXML
    private void initialize() {
        idEnseignantField.setOnAction(e -> checkExistingConge());
        idEnseignantField.setText(idx);
    }

    @FXML
    private void setEndDate() {
        LocalDate startDate = startDatePicker.getValue();
        if (startDate != null) {
            LocalDate endDate = startDate.plusDays(98);
            endDatePicker.setValue(endDate);
        }
    }

    @FXML
    private void addRecord() {
        String idEnseignant = idEnseignantField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (idEnseignant.isEmpty() || startDate == null || endDate == null) {
            System.out.println("Please fill all fields");
            return;
        }

        if (isOverlappingPeriod(idEnseignant, startDate, endDate)) {
            System.out.println("There is already a record with a period that includes the current time");
            return;
        }

        String query = "INSERT INTO conge_de_maternite (effective_start_date_conge_de_maternite, effective_end_date_conge_de_maternite, Id_Enseignant) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            stmt.setString(3, idEnseignant);

            stmt.executeUpdate();
            System.out.println("Record added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isOverlappingPeriod(String idEnseignant, LocalDate startDate, LocalDate endDate) {
        String query = "SELECT COUNT(*) FROM conge_de_maternite WHERE Id_Enseignant = ? AND (effective_start_date_conge_de_maternite <= ? AND effective_end_date_conge_de_maternite >= ?)";

        try (Connection conn =getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idEnseignant);
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            stmt.setDate(3, java.sql.Date.valueOf(startDate));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void checkExistingConge() {
        String idEnseignant = idEnseignantField.getText();
        if (idEnseignant.isEmpty()) {
            countdownLabel.setText("Please enter Id Enseignant");
            return;
        }

        String query = "SELECT effective_end_date_conge_de_maternite FROM conge_de_maternite WHERE Id_Enseignant = ? AND effective_end_date_conge_de_maternite >= CURDATE() ORDER BY effective_end_date_conge_de_maternite DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idEnseignant);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LocalDate endDate = rs.getDate("effective_end_date_conge_de_maternite").toLocalDate();
                startCountdownTimer(endDate);
            } else {
                countdownLabel.setText("No ongoing CongeDeMaternite");
                if (countdownTimeline != null) {
                    countdownTimeline.stop();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startCountdownTimer(LocalDate endDate) {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MIDNIGHT);
            long secondsLeft = ChronoUnit.SECONDS.between(now, endDateTime);

            if (secondsLeft <= 0) {
                countdownLabel.setText("CongeDeMaternite has ended");
                countdownTimeline.stop();
            } else {
                long days = secondsLeft / (24 * 3600);
                secondsLeft %= 24 * 3600;
                long hours = secondsLeft / 3600;
                secondsLeft %= 3600;
                long minutes = secondsLeft / 60;
                long seconds = secondsLeft % 60;

                countdownLabel.setText(String.format("CongeDeMaternite ends in: %d days %02d:%02d:%02d", days, hours, minutes, seconds));
            }
        }));
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }
}
