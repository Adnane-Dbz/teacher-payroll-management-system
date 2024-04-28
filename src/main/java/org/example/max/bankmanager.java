package org.example.max;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;

public class bankmanager extends Application {

    public bankmanager() {
        // Public constructor
    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bankaccount.fxml"));
        Parent root = fxmlLoader.load(); // Load the root node from FXML

        // Remove window decorations and set transparent style
        stage.initStyle(StageStyle.TRANSPARENT);

        // Load application icon (optional)
        InputStream iconStream = HelloApplication.class.getResourceAsStream("/org/example/max/image/01.png");
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        } else {
            System.err.println("Icon file not found or could not be loaded.");
        }

        // Set custom close button
        CustomCloseButton closeButton = new CustomCloseButton(stage);
        AnchorPane.setTopAnchor(closeButton, 10.0);
        AnchorPane.setRightAnchor(closeButton, 10.0);
        ((AnchorPane) root).getChildren().add(closeButton); // Assuming root is AnchorPane

        // Make the stage movable by dragging
        root.setOnMousePressed(event -> {
            HelloApplication.xOffset = event.getSceneX();
            HelloApplication.yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - HelloApplication.xOffset);
            stage.setY(event.getScreenY() - HelloApplication.yOffset);
        });

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Add Bank Account Type");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



