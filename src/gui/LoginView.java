package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private final StackPane view;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;
    private final Label statusLabel;
    private final Button goToRegisterButton;

    public LoginView() {
        Label title = Styles.heading("Digital Künstler Portal");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);

        form.add(new Label("Username:"), 0, 0);
        usernameField = Styles.applyModernField(new TextField());
        form.add(usernameField, 1, 0);

        form.add(new Label("Password:"), 0, 1);
        passwordField = Styles.applyModernField(new PasswordField());
        form.add(passwordField, 1, 1);

        loginButton = Styles.applyModernButton(new Button("Login"), true);
        goToRegisterButton = Styles.applyModernButton(new Button("Register"), false);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        goToRegisterButton.setMaxWidth(Double.MAX_VALUE);

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: " + Styles.PINK_DARK + ";");

        GridPane.setHgrow(usernameField, Priority.ALWAYS);
        GridPane.setHgrow(passwordField, Priority.ALWAYS);

        VBox card = new VBox(14, title, form, loginButton, goToRegisterButton, statusLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(4));

        view = Styles.centeredCard(card);
    }

    public StackPane getView() {
        return view;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public Button getGoToRegisterButton() {
        return goToRegisterButton;
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return passwordField.getText(); }
}
