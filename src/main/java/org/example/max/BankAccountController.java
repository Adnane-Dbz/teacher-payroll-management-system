package org.example.max;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;

import static org.example.max.TypeExtraPostManager.addTypeAccount;
import static org.example.max.TypeExtraPostManager.getTypeComptIds;

public class BankAccountController {

    @FXML
    private ListView<String> accountTypeListView;

    @FXML
    private TextField accountTypeTextField;

    @FXML
    private Button addButton;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        // Populate the ListView with existing account types
        ArrayList<String> accountTypes = getTypeComptIds();
        accountTypeListView.getItems().addAll(accountTypes);
    }

    @FXML
    private void handleAddAccountType() {
        String newAccountType = accountTypeTextField.getText().trim();
        if (newAccountType.isEmpty()) {
            messageLabel.setText("Account type cannot be empty!");
        } else {
          addTypeAccount(newAccountType);
            accountTypeListView.getItems().add(newAccountType);
            accountTypeTextField.clear();
            messageLabel.setText("Account type added successfully!");
        }
    }
}
