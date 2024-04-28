package org.example.max;



import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;

public class StageUtil {

    public static void setupStage(Stage stage, Parent root) {
        Scene scene = new Scene(root);

        // Remove window decorations and set transparent style
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);

        // Load application icon (optional)
        InputStream iconStream = HelloApplication.class.getResourceAsStream("/org/example/max/image/01.png");
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        } else {
            System.err.println("Icon file not found or could not be loaded.");
        }

        // Set custom close button
        CustomCloseButton closeButton = new CustomCloseButton(stage);
        ((StackPane) root).getChildren().add(closeButton);

        // Position the close button in the top-right corner
        StackPane.setMargin(closeButton, new Insets(10)); // Adjust margins as needed
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);

        stage.show();
    }
}
