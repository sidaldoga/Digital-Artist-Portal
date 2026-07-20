package controller;

import gui.DashboardView;
import gui.LoginView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import main.Main;
import network.Client;
import network.Response;
import network.Status;
import network.dto.UserDTO;

public class LoginController {
    private Client client;
    private LoginView view;

    public LoginController(Client client, LoginView view) {
        this.client = client;
        this.view = view;

        this.view.getLoginButton().setOnAction(e -> handleLogin());
    }

    public void handleLogin() {

        String username = view.getUsername();
        String password = view.getPassword();

        Task<Response> loginTask = new Task<>() {
            @Override
            protected Response call() throws Exception {
                return client.login(username, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            Response response = loginTask.getValue();

            if (response != null && response.getStatus() == Status.OK
                    && response.getPayload() instanceof UserDTO user) {
                view.setStatus("Login successful!");
                openDashboard(username, user);
            } else {
                String msg = (response != null && response.getMessage() != null)
                        ? response.getMessage()
                        : "Login failed.";
                view.setStatus("Error: " + msg);
            }
        });

        loginTask.setOnFailed(e ->
                view.setStatus("Connection error: "
                        + (loginTask.getException() != null ? loginTask.getException().getMessage() : "Unknown")));

        new Thread(loginTask).start();
    }

    private void openDashboard(String username, UserDTO user) {
        DashboardController controller = new DashboardController(client, user);
        DashboardView dashboard = new DashboardView(username, controller.getView());


        dashboard.getStage().setOnCloseRequest(ev -> controller.shutdown());
        dashboard.getLogoutButton().setOnAction(e -> handleLogout(controller, dashboard));
        dashboard.show();

        Stage loginStage = (Stage) view.getLoginButton().getScene().getWindow();
        loginStage.close();
    }
    private void handleLogout(DashboardController controller, DashboardView dashboard) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                controller.shutdown();
                client.close();
                return null;
            }
        };

        Runnable backToLogin = () -> {
            try {
                Stage loginStage = new Stage();
                Main.showLoginScreen(loginStage);
                loginStage.show();
            } catch (Exception ex) {
                System.err.println("Could not reopen login: " + ex.getMessage());
            }
            dashboard.getStage().close();
        };

        task.setOnSucceeded(e -> Platform.runLater(backToLogin));
        task.setOnFailed(e -> Platform.runLater(backToLogin));
        new Thread(task).start();
    }
}
