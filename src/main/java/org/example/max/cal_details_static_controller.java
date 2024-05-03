package org.example.max;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.ArrayList;

import static org.example.max.TypeExtraPostManager.add_or_update_details_static;
import static org.example.max.TypeExtraPostManager.get_details_static;
import static org.example.max.work_space.showAlert;

public class cal_details_static_controller {

    @FXML
    private TextField MODIFY_point_indiciaire;

    @FXML
    private TextField MODIFY_prime_zone;
    @FXML
    private TextField MODIFY_persantage_for_EXP;
    @FXML
    private Button MODIFY_update_button;




    @FXML
    public void initialize() {
        addNumberFilter(MODIFY_point_indiciaire);
        addNumberFilter(MODIFY_prime_zone);
        addNumberFilter(MODIFY_persantage_for_EXP);
        setDetails();
    }

    @FXML
    public void setDetails() {
        ArrayList<int[]> detailsList = get_details_static(null);

        if (detailsList.size() > 0) {
            int[] details = detailsList.get(0);
            MODIFY_prime_zone.setText(String.valueOf(details[0]));
            MODIFY_point_indiciaire.setText(String.valueOf(details[1]));
            MODIFY_persantage_for_EXP.setText(String.valueOf(details[2]));
        } else {
            showAlert("No static details found.");
        }
    }

    @FXML
    public void updateDetails() {
        if (checkDetails()) return;

        try {
            int zone = Integer.parseInt(MODIFY_prime_zone.getText());
            int index = Integer.parseInt(MODIFY_point_indiciaire.getText());
            int persantage_for_EXP=Integer.parseInt(MODIFY_persantage_for_EXP.getText());
            add_or_update_details_static(zone, index,persantage_for_EXP);
            showAlert("Details updated successfully.");
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numeric values.");
        }
    }

    private boolean checkDetails() {
        if (MODIFY_prime_zone.getText().isEmpty() || MODIFY_point_indiciaire.getText().isEmpty()||MODIFY_persantage_for_EXP.getText().isEmpty()) {
            showAlert("Please fill in all fields.");
            return true;
        }
        return false;
    }

    private void addNumberFilter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(oldValue);
            }
        });
    }




}
