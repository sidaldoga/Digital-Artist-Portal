package gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class DashboardView {

    private final Stage stage = new Stage();
    private final Button logoutButton = new Button("Logout");

    public DashboardView(String username, Pane content) {
        HBox header = Styles.header("Digital Künstler Portal", username);
        Styles.applyModernButton(logoutButton, true);
        header.getChildren().add(logoutButton);

        BorderPane layout = new BorderPane();
        layout.setStyle(Styles.APP_BG_STYLE);
        layout.setTop(header);
        layout.setCenter(content);
        HBox.setHgrow(content, Priority.ALWAYS);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Dashboard - " + username);
    }

    public void show() {
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public Button getLogoutButton() {
        return logoutButton;
    }
}
