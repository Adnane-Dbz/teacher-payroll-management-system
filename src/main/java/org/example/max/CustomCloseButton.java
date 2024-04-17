package org.example.max;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CustomCloseButton extends Button {

    public CustomCloseButton(Stage stage) {
        super("x");
        setStyle(
                "-fx-background-color: red;" +     // Red background color
                        "-fx-text-fill: white;" +          // White text color
                        "-fx-font-size: 14px;"             // Font size
        );

        setOnAction(event -> {
            stage.close();
        });
    }
}

