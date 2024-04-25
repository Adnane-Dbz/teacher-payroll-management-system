package org.example.max;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;

public class HelloApplication extends Application {
    private static Stage primaryStage;
    static double xOffset = 0;
    static double yOffset = 0;
    static StackPane root;

    public static Stage getPrimaryStage() {

        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        HelloApplication.primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
         root = fxmlLoader.load();
        Scene scene = new Scene(root);

        // Remove window decorations and set transparent style
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        // Load application icon (optional)
        InputStream iconStream = HelloApplication.class.getResourceAsStream("/org/example/max/image/01.png");
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        } else {
            System.err.println("Icon file not found or could not be loaded.");
        }

        // Set custom close button
        CustomCloseButton closeButton = new CustomCloseButton(stage);
        root.getChildren().add(closeButton);

        // Position the close button in the top-right corner
        StackPane.setMargin(closeButton, new Insets(10)); // Adjust margins as needed
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);

        // Make the stage movable by dragging
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }




    public static void main(String[] args) {
        launch();
    }
}
