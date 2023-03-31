package com.example.testchangescene;

import domain.User;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import srv.FriendshipService;
import srv.UserService;
import utils.events.UserEntityChangeEvent;
import utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Requests implements Observer<UserEntityChangeEvent> {
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    ObservableList<User> model = FXCollections.observableArrayList();
    private UserService service1;
    private FriendshipService service2;
    private Long id;
    @FXML
    private TableView<User> tableView;

    public void goBack(ActionEvent event) {
        try {
            Stage actualStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("third-file.fxml"));
            Parent root = fxmlLoader.load();
            UserViewController userController = fxmlLoader.getController();
            userController.setUtilizatorService(service1, service2, id);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            actualStage.hide();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<User> messages = service2.pending(id);
        List<User> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    public void setService(UserService service, FriendshipService service2, Long id) {
        this.service1 = service;
        this.service2 = service2;
        this.id = id;
        service.addObserver(this);
        initModel();
    }


    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        initModel();
    }

    public void decline() {
        User user = tableView.getSelectionModel().getSelectedItem();
        if (user != null) {
            System.out.println(user.getId());
            service2.deleteFriend(this.id, user.getId());
            MessageAlert.showMessage(Alert.AlertType.INFORMATION, "Info", "Request declined");
            initModel();
        } else {
            MessageAlert.showMessage(Alert.AlertType.INFORMATION, "Info", "PLease select an user from the table");
        }
    }

    public void accept() {
        User user = tableView.getSelectionModel().getSelectedItem();
        if (user != null) {
            System.out.println(user.getId());
            service2.updateFriend(this.id, user.getId(), "accepted");
            MessageAlert.showMessage(Alert.AlertType.INFORMATION, "Info", "Request accepted");
            initModel();
        } else {
            MessageAlert.showMessage(Alert.AlertType.INFORMATION, "Info", "PLease select an user from the table");

        }
    }
}
