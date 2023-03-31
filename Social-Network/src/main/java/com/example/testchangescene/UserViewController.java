package com.example.testchangescene;

import domain.RepoException;
import domain.User;
import domain.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import srv.FriendshipService;
import srv.UserService;
import utils.events.UserEntityChangeEvent;
import utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserViewController implements Observer<UserEntityChangeEvent> {
    UserService service;
    FriendshipService service2;
    ObservableList<User> model = FXCollections.observableArrayList();
    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    @FXML
    private TextField emailText;
    private Long id;

    public void go_to_login(ActionEvent event) {
        try {
            Stage actualStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("first-file.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            loginController.setServices(service, service2);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            actualStage.hide();
        } catch (Exception e) {
            System.out.println("smth");
        }
    }

    public void go_to_requests(ActionEvent event) {
        try {
            Stage actualStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("requests.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Requests requests = fxmlLoader.getController();
            requests.setService(service, service2, id);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            actualStage.hide();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void setUtilizatorService(UserService service, FriendshipService service2, Long id) {
        this.service = service;
        this.service2 = service2;
        this.id = id;
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<User> messages = service2.allFriends(id);
        List<User> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        initModel();
    }

    public void handleAddFriend(ActionEvent actionEvent) {
        try {
            String emailText = this.emailText.getText();

            User user = service.findUser(emailText);
            if(user == null){
                throw new ValidationException("User not found");
            }
            service2.addFriend(this.id, user.getId());
            initModel();
            MessageAlert.showMessage(Alert.AlertType.INFORMATION, "Info", "Friend request sent!!");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        } catch (NumberFormatException n) {
            MessageAlert.showMessage(Alert.AlertType.INFORMATION, "Warning", "ID must be a number!");
        } catch (RepoException repoException){
            MessageAlert.showMessage(Alert.AlertType.INFORMATION, "Warning", repoException.getMessage());
        }
    }

    public void handleDeleteFriend(ActionEvent actionEvent) {

        User user = tableView.getSelectionModel().getSelectedItem();
        if (user != null) {
            System.out.println(user.getId());
            service2.deleteFriend(this.id, user.getId());
            MessageAlert.showMessage(Alert.AlertType.INFORMATION, "Info", "Frendship cancelled successfuly!");
            initModel();
        } else {
            MessageAlert.showMessage(Alert.AlertType.INFORMATION, "Info", "PLease select an user from the table");
        }
    }
}
