package com.example.testchangescene;

import domain.User;
import domain.UserValidator;
import domain.ValidationException;
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

public class SignupController {
    private UserService service1;
    private FriendshipService service2;
    @FXML
    private TextField firstname;
    @FXML
    private TextField lastname;
    @FXML
    private TextField password;
    @FXML
    private TextField email;

    public void go_to_login(ActionEvent event) {
        try {
            Stage actualStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("first-file.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            loginController.setServices(service1, service2);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            actualStage.hide();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void signUpUser(ActionEvent event) {
        Connection connection = null;
        PreparedStatement psInser = null;
        PreparedStatement psCheck = null;
        ResultSet resultSet = null;
        String firstName = this.firstname.getText();
        String lastName = this.lastname.getText();
        String password = this.password.getText();
        String email = this.email.getText();
        Alert alert;
        try {
            User user = new User(firstName, lastName, password, email);
            UserValidator validator = new UserValidator();
            validator.validate(user);
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Users", "postgres", "postgres");
            psCheck = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
            psCheck.setString(1, email);
            resultSet = psCheck.executeQuery();
            if (resultSet.isBeforeFirst()) {
                System.out.println("User already exits");
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Email unavailable");
                alert.show();
            } else {

                psInser = connection.prepareStatement("insert into users (first_name, last_name, password,email) values (?, ?,?,?)");
                psInser.setString(1, firstName);
                psInser.setString(2, lastName);
                psInser.setString(3, password);
                psInser.setString(4, email);
                psInser.executeUpdate();
                this.go_to_login(event);
            }

        } catch (ValidationException validationException) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(validationException.getMessage());
            alert.show();
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
            if (psCheck != null) {
                try {
                    psCheck.close();

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
            if (psInser != null) {
                try {
                    psInser.close();

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

    public void setUtilizatorService(UserService service1, FriendshipService service2) {
        this.service1 = service1;
        this.service2 = service2;
    }
}
