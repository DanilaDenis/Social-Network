package com.example.testchangescene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import srv.FriendshipService;
import srv.UserService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField password;
    private UserService service1;
    private FriendshipService service2;
    @FXML
    private TextField email;

    public LoginController() {
    }

    public void go_to_sign_up(ActionEvent event) {
        try {
            Stage actualStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("second-file.fxml"));
            Parent root = fxmlLoader.load();
            SignupController signupController = fxmlLoader.getController();
            signupController.setUtilizatorService(service1, service2);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            actualStage.hide();
        } catch (Exception e) {
            System.out.println("smth");
        }
    }

    public void log_in(ActionEvent event, Long retrievedId) {
        try {
            Stage actualStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("third-file.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            UserViewController userController = fxmlLoader.getController();
            userController.setUtilizatorService(service1, service2, retrievedId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            actualStage.hide();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void logInUser(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String email = this.email.getText();
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Users", "postgres", "postgres");
            preparedStatement = connection.prepareStatement("select * From users Where email = ?");
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                System.out.println("User not found");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("User not found");
                alert.show();
            } else {
                while (resultSet.next()) {
                    String retrievePassword = resultSet.getString("password");
                    int retrievedId = resultSet.getInt("id");
                    if (retrievePassword.equals(password.getText())) {
                        this.log_in(event, (long) retrievedId);
                    } else {
                        System.out.println("Passwords did not match");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Provided incorect data");
                        alert.show();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
            if (connection != null) {
                try {
                    connection.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setServices(UserService service1, FriendshipService service2) {
        this.service1 = service1;
        this.service2 = service2;
    }
}
