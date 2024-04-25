package org.example.max;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.max.DatabaseConfigController.isDatabaseConnectionCorrect;
import static org.example.max.HelloApplication.*;
import static org.example.max.TypeExtraPostManager.*;


public class LoginController {

    static String current_username = "";
    @FXML private AnchorPane loginPage;
    @FXML private AnchorPane signUpPage;
    @FXML private AnchorPane forgotPasswordPage;

    @FXML private TextField login_username;
    @FXML private PasswordField login_password;
    @FXML private CheckBox login_showpassword;
    @FXML private Button login_login;
    @FXML private Button login_signup;
    @FXML private Button login_forgotpassword;

    @FXML private TextField signup_username;
    @FXML private PasswordField signup_password;
    @FXML private TextField signup_lastname;
    @FXML private ComboBox<String> signup_question;
    @FXML private TextField signup_answer;
    @FXML private Button signup_createAccount;
    @FXML private Button signup_backToLogin;

    @FXML private TextField forgot_username;
    @FXML private ComboBox<String> forgot_question;
    @FXML private TextField forgot_answer;
    @FXML private PasswordField forgot_newPassword;
    @FXML private Button forgot_resetPassword;
    @FXML private Button forgot_backToLogin;

boolean first_time_login=false;


    @FXML
    public void initialize() { if(!first_time_login) {



        login_login.setOnAction(event -> handleLogin());
        login_signup.setOnAction(event -> showSignUpPage());
        login_forgotpassword.setOnAction(event -> showForgotPasswordPage());

        signup_createAccount.setOnAction(event -> handleSignUp());
        signup_backToLogin.setOnAction(event -> showLoginPage());

        forgot_resetPassword.setOnAction(event -> handlePasswordReset());
        forgot_backToLogin.setOnAction(event -> showLoginPage());
    } else {



        if (!isDatabaseConnectionCorrect()){database_check(); }
       else initializeDatabase();

    }
        if (!first_time_login)first_time_login=true;
    }

    private void handleLogin() {

initialize();
        if (!isDatabaseConnectionCorrect())return;
        String username = login_username.getText();
        String password = login_password.getText();

        try (Connection connection =getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                current_username=login_username.getText();
                System.out.println("Login successful");
                seturl();
                loadProjectInside();
            } else {
                System.out.println("Invalid username or password");
                // Show error message
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSignUp() {
        initialize();
        if (!isDatabaseConnectionCorrect())return;
        String username = signup_username.getText();
        String password = signup_password.getText();
        String lastname = signup_lastname.getText();
        int questionNumber = signup_question.getSelectionModel().getSelectedIndex() + 1;
        String answer = signup_answer.getText();

        try (Connection connection = getConnection()) {
            String query = "INSERT INTO users (username, password, lastname, question_number, answer) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, lastname);
            statement.setInt(4, questionNumber);
            statement.setString(5, answer);
            statement.executeUpdate();

            System.out.println("Sign up successful");
            showLoginPage();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handlePasswordReset() {
        initialize(); if (!isDatabaseConnectionCorrect())return;
        String username = forgot_username.getText();
        int questionNumber = forgot_question.getSelectionModel().getSelectedIndex() + 1;
        String answer = forgot_answer.getText();
        String newPassword = forgot_newPassword.getText();

        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND question_number = ? AND answer = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setInt(2, questionNumber);
            statement.setString(3, answer);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                query = "UPDATE users SET password = ? WHERE username = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, newPassword);
                statement.setString(2, username);
                statement.executeUpdate();

                System.out.println("Password reset successful");
                showLoginPage();
            } else {
                System.out.println("Incorrect username or answer");
                // Show error message
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadProjectInside() throws IOException {
        Parent projectInsideRoot = FXMLLoader.load(getClass().getResource("projectinside.fxml"));
        Stage stage = HelloApplication.getPrimaryStage();
        Scene scene = new Scene(projectInsideRoot);

        // Set custom close button
        CustomCloseButton closeButton = new CustomCloseButton(stage);
        root.getChildren().add(closeButton);

        // Position the close button in the top-right corner
        StackPane.setMargin(closeButton, new Insets(10)); // Adjust margins as needed
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);

        // Add event handlers to make the scene movable
        projectInsideRoot.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        projectInsideRoot.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        stage.setScene(scene);
        stage.setTitle("Project Inside");
        stage.show();
    }

    private void showLoginPage() {
        loginPage.setVisible(true);
        signUpPage.setVisible(false);
        forgotPasswordPage.setVisible(false);
    }

    private void showSignUpPage() {
        loginPage.setVisible(false);
        signUpPage.setVisible(true);
        forgotPasswordPage.setVisible(false);
    }

    private void showForgotPasswordPage() {
        loginPage.setVisible(false);
        signUpPage.setVisible(false);
        forgotPasswordPage.setVisible(true);
    }



    public static void database_check() {
        try {
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(HelloApplication.getPrimaryStage());
            database_info database_info = new database_info();
            database_info.start(newStage);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    }


