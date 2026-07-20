package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RegisterView {
    private final StackPane view;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final TextField fnameField;
    private final TextField lnameField;
    private final TextField emailField;
    private final TextField streetField;
    private final TextField cityField;
    private final TextField zipField;
    private final TextField countryField;
    private final ComboBox<String> roleComboBox;
    private final Button registerButton;
    private final Label statusLabel;

    public RegisterView() {
        Label title = Styles.heading("Create your account");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        usernameField = Styles.applyModernField(new TextField());
        passwordField = Styles.applyModernField(new PasswordField());
        fnameField = Styles.applyModernField(new TextField());
        lnameField = Styles.applyModernField(new TextField());
        emailField = Styles.applyModernField(new TextField());
        streetField = Styles.applyModernField(new TextField());
        cityField = Styles.applyModernField(new TextField());
        zipField = Styles.applyModernField(new TextField());
        countryField = Styles.applyModernField(new TextField());

        roleComboBox = Styles.applyModernCombo(new ComboBox<>());
        roleComboBox.getItems().addAll("SUBSCRIBER", "ARTIST");

        int r = 0;
        r = addRow(form, r, "Username:", usernameField);
        r = addRow(form, r, "Password:", passwordField);
        r = addRow(form, r, "First Name:", fnameField);
        r = addRow(form, r, "Last Name:", lnameField);
        r = addRow(form, r, "Email:", emailField);
        r = addRow(form, r, "Street:", streetField);
        r = addRow(form, r, "City:", cityField);
        r = addRow(form, r, "Zip Code:", zipField);
        r = addRow(form, r, "Country:", countryField);
        addRow(form, r, "Role:", roleComboBox);

        registerButton = Styles.applyModernButton(new Button("Register"), true);
        registerButton.setMaxWidth(Double.MAX_VALUE);

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: " + Styles.PINK_DARK + ";");

        VBox card = new VBox(14, title, form, registerButton, statusLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(4));

        view = Styles.centeredCard(card);
    }

    private int addRow(GridPane g, int row, String label, javafx.scene.Node field) {
        g.add(new Label(label), 0, row);
        g.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
        return row + 1;
    }

    public StackPane getView() { return view; }
    public Button getRegisterButton() { return registerButton; }
    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public String getFname() { return fnameField.getText(); }
    public String getLname() { return lnameField.getText(); }
    public String getEmail() { return emailField.getText(); }
    public String getStreet() { return streetField.getText(); }
    public String getCity() { return cityField.getText(); }
    public String getZip() { return zipField.getText(); }
    public String getCountry() { return countryField.getText(); }
    public String getSelectedRole() { return roleComboBox.getValue(); }
    public void setStatus(String text) { statusLabel.setText(text); }
}
