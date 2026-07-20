package main;

import controller.*;
import gui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.Client;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        showLoginScreen(primaryStage);
        primaryStage.show();
    }

    public static void showLoginScreen(Stage stage) throws Exception {
        Client client = new Client("localhost", 8080);

        LoginView loginView = new LoginView();
        new LoginController(client, loginView);
        Scene loginScene = new Scene(loginView.getView(), 440, 420);

        RegisterView registerView = new RegisterView();
        new RegisterController(client, registerView);
        Scene registerScene = new Scene(registerView.getView(), 480, 640);

        loginView.getGoToRegisterButton().setOnAction(e -> {
            stage.setScene(registerScene);
            stage.setTitle("Register");
        });

        stage.setScene(loginScene);
        stage.setTitle("Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}