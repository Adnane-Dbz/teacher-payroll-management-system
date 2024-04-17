package org.example.max;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class DatabaseConfigApp extends Application {
    private static final String CONFIG_FILE = "db_config.properties";

    private TextField userField;
    private PasswordField passwordField;
    private TextField portField;

    @Override
    public void start(Stage primaryStage) {
        // Load saved configuration
        Properties config = loadConfig();

        // Create UI components
        Label userLabel = new Label("User:");
        userField = new TextField(config.getProperty("user", ""));

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setText(config.getProperty("password", ""));

        Label portLabel = new Label("Port:");
        portField = new TextField(config.getProperty("port", "3306"));

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveConfig());

        // Layout
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.add(userLabel, 0, 0);
        gridPane.add(userField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(portLabel, 0, 2);
        gridPane.add(portField, 1, 2);
        gridPane.add(saveButton, 1, 3);

        // Show the stage
        Scene scene = new Scene(gridPane, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Database Configuration");
        primaryStage.show();
    }

    private void saveConfig() {
        Properties config = new Properties();
        config.setProperty("user", userField.getText());
        config.setProperty("password", passwordField.getText());
        config.setProperty("port", portField.getText());

        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            config.store(output, null);
            System.out.println("Configuration saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties loadConfig() {
        Properties config = new Properties();

        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            config.load(input);
        } catch (IOException e) {
            System.out.println("No configuration file found. Using default values.");
        }

        return config;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
