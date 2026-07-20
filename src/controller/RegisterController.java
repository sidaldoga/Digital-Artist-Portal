package controller;

import gui.LoginView;
import gui.RegisterView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.Client;
import network.Status;
import network.dto.AddressDTO;
import network.dto.ArtistDTO;
import network.dto.SubscriberDTO;
import network.dto.UserDTO;
import javafx.concurrent.Task;
import network.Response;

import java.io.IOException;

public class RegisterController {
    private Client client;
    private RegisterView view;

    public RegisterController(Client client, RegisterView view) {
        this.client = client;
        this.view = view;

        this.view.getRegisterButton().setOnAction(e -> handleRegister());
    }

    public void handleRegister() {
        String username = view.getUsername();
        String password = view.getPassword();
        String fname = view.getFname();
        String lname = view.getLname();
        String email = view.getEmail();
        String street = view.getStreet();
        String city = view.getCity();
        String zip = view.getZip();
        String country = view.getCountry();
        String role = view.getSelectedRole();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            view.setStatus("Error: Please fill in the required fields..");
            return;
        }

        AddressDTO address = new AddressDTO(street, city, zip, country);
        UserDTO userDTO;

        if ("SUBSCRIBER".equals(role)) {
            userDTO = new SubscriberDTO(0, fname, lname, username, email, role, 0, address, password);
        } else {
            userDTO = new ArtistDTO(0, fname, lname, username, email, role, true, password);
        }

        Task<Response> registerTask = new Task<>() {
            @Override
            protected Response call() throws Exception {
                return client.register(userDTO);
            }
        };

        registerTask.setOnSucceeded(e -> {
            Response response = registerTask.getValue();

            if (response != null) {
                System.out.println("DEBUG: Response status: " + response.getStatus());
                System.out.println("DEBUG: Response message: " + response.getMessage());
            } else {
                System.out.println("DEBUG: Response is NULL");
            }

            if (response != null && response.getStatus() == Status.OK) {
                view.setStatus("Register successful!");

                navigateToLogin();
            } else {
                String msg = (response != null && response.getMessage() != null)
                        ? response.getMessage()
                        : "An unknown error occurred during registration..";
                view.setStatus("Error: " + msg);
            }
        });

        registerTask.setOnFailed(e -> {
            view.setStatus("Connection error: " + (registerTask.getException() != null ? registerTask.getException().getMessage() : "Unknown"));
        });

        new Thread(registerTask).start();
    }

    private void navigateToLogin() {
        System.out.println("DEBUG: LoginView class is being initialized..");
        try {
            LoginView loginView = new LoginView();

            new LoginController(this.client, loginView);

            Stage stage = (Stage) view.getRegisterButton().getScene().getWindow();

            stage.setScene(new Scene(loginView.getView()));
            stage.setTitle("Login");
            stage.show();

        } catch (Exception e) {
            System.err.println("ERROR: An error occurred while switching to the login screen!");
            e.printStackTrace();
        }
    }
}