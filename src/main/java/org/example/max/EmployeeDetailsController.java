package org.example.max;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class EmployeeDetailsController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField birthdayField;

    @FXML
    private TextField employeeNumberField;

    @FXML
    private TextField addressField;

    @FXML
    private CheckBox marriedCheckBox;

    @FXML
    private VBox familyVBox;

    @FXML
    private TextField numberOfKidsField;

    @FXML
    private VBox kidsVBox;

    @FXML
    private VBox wifeVBox;

    @FXML
    private void initialize() {
        // Initialize the form
    }

    @FXML
    private void handleMarriedCheckBox() {
        if (marriedCheckBox.isSelected()) {
            familyVBox.setVisible(true);
            addKidsFields();
            addWifeFields();
        } else {
            familyVBox.setVisible(false);
            kidsVBox.getChildren().clear();
            wifeVBox.getChildren().clear();
        }
    }

    private void addKidsFields() {
        int numberOfKids = Integer.parseInt(numberOfKidsField.getText());
        kidsVBox.getChildren().clear();
        for (int i = 1; i <= numberOfKids; i++) {
            Label kidLabel = new Label("Kid " + i + " Details");
            Label nameLabel = new Label("Name");
            TextField nameField = new TextField();
            Label lastNameLabel = new Label("Last Name");
            TextField lastNameField = new TextField();
            Label birthdayLabel = new Label("Birthday");
            TextField birthdayField = new TextField();

            kidsVBox.getChildren().addAll(kidLabel, nameLabel, nameField, lastNameLabel, lastNameField, birthdayLabel, birthdayField);
        }
    }

    private void addWifeFields() {
        Label wifeLabel = new Label("Wife Details");
        Label nameLabel = new Label("Name");
        TextField nameField = new TextField();
        Label lastNameLabel = new Label("Last Name");
        TextField lastNameField = new TextField();
        Label birthdayLabel = new Label("Birthday");
        TextField birthdayField = new TextField();

        wifeVBox.getChildren().addAll(wifeLabel, nameLabel, nameField, lastNameLabel, lastNameField, birthdayLabel, birthdayField);
    }
}
