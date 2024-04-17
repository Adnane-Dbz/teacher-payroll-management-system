package org.example.max;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.example.max.TypeExtraPostManager.initializeDatabase;
import static org.example.max.work_space.showAlert;

public class DatabaseConfigController {
    private static final String CONFIG_FILE = "db_config.properties";

    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField portField;

    @FXML
    public void initialize() {
        // Load saved configuration
        Properties config = loadConfig();
        userField.setText(config.getProperty("user", ""));
        passwordField.setText(config.getProperty("password", ""));
        portField.setText(config.getProperty("port", "3306"));
    }

    @FXML
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

    static Properties loadConfig() {
        Properties config = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            config.load(input);
        } catch (IOException e) {
            System.out.println("No configuration file found. Using default values.");
        }
        return config;
    }


    @FXML

    static boolean isDatabaseConnectionCorrects() {
        Properties config = loadConfig();
        String user = config.getProperty("user", "");
        String password = config.getProperty("password", "");
        String port = config.getProperty("port", "");
        String url = "jdbc:mysql://127.0.0.1:" + port + "/?user=" + user + "&password=" + password;

        try {
            // Explicitly load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.close();
            showAlert("Connected to MySQL.");
            System.out.println("Connected to MySQL.");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to MySQL database.");
            e.printStackTrace();
        }
        return false;
    }




    static boolean isDatabaseConnectionCorrect() {
         try { Properties config = loadConfig();
        String user = config.getProperty("user", "");
        String password = config.getProperty("password", "");
        String port = config.getProperty("port", "");
        String url = "jdbc:mysql://127.0.0.1:" + port + "/?user=" + user + "&password=" + password;
             Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url);
            connection.close();
            showAlert("connect to my sql.");
            System.out.println(" connect to my sql.");
            return true;
        } catch (Exception e) {


            return false;
        }
    }


    @FXML
     void  testConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {

            Properties config = loadConfig();
            String user = config.getProperty("user", "");
            String password = config.getProperty("password", "");
            String port =config.getProperty("port", "");
            String url = "jdbc:mysql://127.0.0.1:" + port + "/salary?user=" + user + "&password=" + password;
            String url2 = "jdbc:mysql://127.0.0.1:" + port + "/user=" + user + "&password=" + password;
            Connection connection = DriverManager.getConnection(url2,user,password);

            if (connection != null) {
                initializeDatabase();
                connection.close();
                TypeExtraPostManager.URL=url;
                showAlert("connect to the database.");
                System.out.println(" connect to the database.");

            }else {  showAlert("Failed to connect to the database:you didnt enter a correct database password/user/port");

            }
        } catch (SQLException e) {
            showAlert("Failed to connect to the database totaly :there is a issue with mysql");

            System.out.println("Failed to connect to the database.");
        }

    }






}
