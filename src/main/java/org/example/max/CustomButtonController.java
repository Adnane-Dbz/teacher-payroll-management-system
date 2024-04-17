package org.example.max;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CustomButtonController {

    @FXML
    public static void CustomCloseButton(AnchorPane anchorPane) {
        // Create the close button
        Button closeButton = new Button("Close");

        // Apply the CSS style
        closeButton.setStyle(
                "-fx-background-color: red;" +     // Red background color
                        "-fx-text-fill: white;" +          // White text color
                        "-fx-font-size: 14px;"             // Font size
        );

        // Set the button's action to close the stage
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        // Add the button to the AnchorPane
        anchorPane.getChildren().add(closeButton);

        // Position the button in the top-right corner
        AnchorPane.setTopAnchor(closeButton, 10.0);
        AnchorPane.setRightAnchor(closeButton, 10.0);
    }


    @FXML
    public static void CustomCloseButtons(AnchorPane anchorPane) {
        // Create the close button
        Button closeButton = new Button("Close");

        // Apply the CSS style
        closeButton.setStyle(
                "-fx-background-color: red;" +     // Red background color
                        "-fx-text-fill: white;" +          // White text color
                        "-fx-font-size: 14px;"             // Font size
        );

        // Set the button's action to close the stage
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        // Add the button to the AnchorPane
        anchorPane.getChildren().add(closeButton);

        // Position the button in the top-right corner
        AnchorPane.setTopAnchor(closeButton, 10.0);
        AnchorPane.setRightAnchor(closeButton, 10.0);
    }









    @FXML

    public static void CustomMinButton(AnchorPane anchorPane) {
        // Create the close button
        Button closeButton = new Button("minimize");

        // Apply the CSS style
        closeButton.setStyle(
                "-fx-background-color: blue;" +     // Red background color
                        "-fx-text-fill: white;" +          // White text color
                        "-fx-font-size: 14px;"             // Font size
        );

        // Set the button's action to minimize the stage
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.setIconified(true);
        });

        // Add the button to the AnchorPane
        anchorPane.getChildren().add(closeButton);


        // Position the button in the top-right corner
        AnchorPane.setTopAnchor(closeButton, 10.0);
        AnchorPane.setRightAnchor(closeButton, 80.0);
    }
}
