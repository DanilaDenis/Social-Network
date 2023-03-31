package com.example.testchangescene;

import domain.FriendshipValidator;
import domain.User;
import domain.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repo.RepoFriendship;
import repo.Repository;
import repo.dbrepo.FriendshipDbRepo;
import repo.dbrepo.UserDbRepo;
import srv.FriendshipService;
import srv.UserService;

import java.io.IOException;

public class HelloApplication extends Application {
    UserService service1;
    FriendshipService service2;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        System.out.println("Reading data from file");
        String username = "postgres";
        String password = "postgres";
        String url = "jdbc:postgresql://localhost:5432/Users";

        Repository<Long, User> utilizatorRepository =
                new UserDbRepo(url, username, password, new UserValidator());
        service1 = new UserService(utilizatorRepository);
        RepoFriendship friendRepository =
                new FriendshipDbRepo(url, username, password, new FriendshipValidator());

        service2 = new FriendshipService(friendRepository);
        initView(primaryStage);
        primaryStage.show();


    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("first-file.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));
        LoginController userController = fxmlLoader.getController();
        userController.setServices(service1, service2);

        primaryStage.show();


    }
}