package org.example.max;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.util.List;

import static org.example.max.TypeExtraPostManager.*;

public class FacDepController {

    @FXML
    private ListView<String> facultyListView;

    @FXML
    private TextField facultyTextField;

    @FXML
    private ComboBox<String> facultyComboBox;

    @FXML
    private ListView<String> departmentListView;

    @FXML
    private TextField departmentTextField;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        // Populate ListView and ComboBox with existing faculties
        List<String> faculties =getFacultes();
        facultyListView.getItems().addAll(faculties);
        facultyComboBox.getItems().addAll(faculties);

        facultyComboBox.setOnAction(event -> handleFacultySelection());
    }

    @FXML
    private void handleFacultySelection() {
        String selectedFaculty = facultyComboBox.getValue();
        if (selectedFaculty != null && !selectedFaculty.isEmpty()) {
            List<String> departments = getDepartements();
            departmentListView.getItems().setAll(departments);
        }
    }

    @FXML
    private void handleAddFaculty() {
        String newFaculty = facultyTextField.getText().trim();
        if (newFaculty.isEmpty()) {
            messageLabel.setText("Faculty name cannot be empty!");
        } else {
           setFaculte(newFaculty);
            facultyListView.getItems().add(newFaculty);
            facultyComboBox.getItems().add(newFaculty);
            facultyTextField.clear();
            messageLabel.setText("Faculty added successfully!");
        }
    }

    @FXML
    private void handleAddDepartment() {
        String selectedFaculty = facultyComboBox.getValue();
        String newDepartment = departmentTextField.getText().trim();
        if (selectedFaculty == null || selectedFaculty.isEmpty()) {
            messageLabel.setText("Please select a faculty first!");
        } else if (newDepartment.isEmpty()) {
            messageLabel.setText("Department name cannot be empty!");
        } else {
           setDepartement(selectedFaculty, newDepartment);
            departmentListView.getItems().add(newDepartment);
            departmentTextField.clear();
            messageLabel.setText("Department added successfully!");
        }
    }

    @FXML
    private void handleDeleteDepartment() {
        String selectedDepartment = departmentListView.getSelectionModel().getSelectedItem();
        if (selectedDepartment == null) {
            messageLabel.setText("Please select a department to delete!");
        } else {
            if (canDeleteDepartment(selectedDepartment)) {
                deleteDepartment(selectedDepartment);
                departmentListView.getItems().remove(selectedDepartment);
                messageLabel.setText("Department deleted successfully!");
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Cannot delete department. There are teachers associated with this department.");
                alert.showAndWait();
            }
        }
    }
}
