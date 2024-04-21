package org.example.max;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.example.max.TypeExtraPostManager.*;

public class ExtraJobEntryController {
    @FXML
    private TextField jobNameField;

    @FXML
    private TextField jobBonusField;

    @FXML
    private TableView<extra_job> jobsTable;

    @FXML
    private TableColumn<extra_job, String> jobNameColumn;

    @FXML
    private TableColumn<extra_job, Integer> jobBonusColumn;

    @FXML
    private TableColumn<extra_job, Calendar> effectiveStartDateColumn;

    @FXML
    private TableColumn<extra_job, Calendar> effectiveEndDateColumn;

    @FXML
    private Button updateButton;

    private ObservableList<extra_job> jobs = FXCollections.observableArrayList();

    private extra_job selectedJob;

    @FXML
    public void initialize() {
        // Initialize table columns
        jobNameColumn.setCellValueFactory(new PropertyValueFactory<>("job_name"));
        jobBonusColumn.setCellValueFactory(new PropertyValueFactory<>("job_bonus"));
        effectiveStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("effectiveStartDate"));
        effectiveEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("effectiveEndDate"));

        // Customize cell value factory for Calendar columns
        effectiveStartDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Calendar item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTime().toString());
                }
            }
        });
        effectiveEndDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Calendar item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTime().toString());
                }
            }
        });

        // Populate the jobs table
        refreshJobsTable();

        // Listen for selection changes and update the form
        jobsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedJob = newValue;
                jobNameField.setText(newValue.getJob_name());
                jobBonusField.setText(String.valueOf(newValue.getJob_bonus()));
                updateButton.setText("Update");
            }
        });
    }

    @FXML
    private void handleAddOrUpdateJob() {
        String jobName = jobNameField.getText();
        String jobBonusText = jobBonusField.getText();
        int jobBonus;
        try {
            jobBonus = Integer.parseInt(jobBonusText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Bonus must be a valid number.");
            return;
        }
        int x=0;
        for (extra_job b :getavailableExtraPosts()) {
            if (b.getJob_name().equals(jobName)) {x=b.getJob_bonus(); break;}
        }
        if (jobName.isEmpty() || jobBonusText.isEmpty()) {
            showAlert("Error", "Job name and bonus must be specified.");
            return;
        }
      if (x==jobBonus){{
          showAlert("notice ", "Job bonus havent change we will ignore the updating process must be specified.");
          return;
      }}



        addOrUpdateTypeExtraPost(jobName, jobBonus);
        refreshJobsTable();
        clearFields();
        showAlert("Success", "Job processed successfully.");
    }

    @FXML
    private void handleDeleteJob() {
        if (selectedJob == null) {
            showAlert("Error", "Please select a job to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to delete this job?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            setTypeExtraPostEndDate(selectedJob.getId());
            refreshJobsTable();
            clearFields();
            showAlert("Success", "Job deleted successfully.");
        }
    }

    private void refreshJobsTable() {
        jobs.clear();
        jobs.addAll(getJobs());
        jobsTable.setItems(jobs);
    }




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        jobNameField.clear();
        jobBonusField.clear();
        selectedJob = null;
        updateButton.setText("Insert");
    }

    private ObservableList<extra_job> getJobs() {
        List<extra_job> jobList = getavailableExtraPosts();
        return FXCollections.observableArrayList(jobList);
    }
}