package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class Styles {

    private Styles() {}

    public static final String CREAM     = "#FAF9F6";
    public static final String CARD      = "#FFFFFF";
    public static final String PINK      = "#F4C2C2";
    public static final String PINK_DARK = "#E0A0A0";
    public static final String SAGE      = "#8FBC8F";
    public static final String INK       = "#333333";

    public static final String CARD_STYLE =
            "-fx-background-color: " + CARD + ";"
            + "-fx-background-radius: 10; -fx-border-radius: 10;"
            + "-fx-border-color: #ECECEC; -fx-border-width: 1; -fx-padding: 12;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 2);";
    public static final String CELL_WRAP_STYLE =
            "-fx-background-color: transparent; -fx-padding: 6 6 6 6;";
    public static final String LIST_STYLE =
            "-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 4;";
    public static final String BUY_STYLE =
            "-fx-background-color: " + PINK + "; -fx-text-fill: " + INK + ";"
            + "-fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;";
    public static final String BUY_HOVER_STYLE =
            "-fx-background-color: " + PINK_DARK + "; -fx-text-fill: " + INK + ";"
            + "-fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;";
    public static final String SAGE_BTN_STYLE =
            "-fx-background-color: " + SAGE + "; -fx-text-fill: white;"
            + "-fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;";
    public static final String NAME_STYLE =
            "-fx-text-fill: " + INK + "; -fx-font-weight: bold; -fx-font-size: 14px;";
    public static final String SECONDARY_STYLE = "-fx-text-fill: " + SAGE + ";";
    public static final String IMAGE_STYLE =
            "-fx-border-color: #DDDDDD; -fx-border-width: 1;"
            + "-fx-border-radius: 6; -fx-background-radius: 6;";
    public static final String HEADING_STYLE =
            "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + INK + ";";
    public static final String FIELD_STYLE =
            "-fx-background-color: " + CARD + "; -fx-background-radius: 8;"
            + "-fx-border-radius: 8; -fx-border-color: #E0E0E0; -fx-border-width: 1;"
            + "-fx-padding: 8; -fx-text-fill: " + INK + ";";
    public static final String FIELD_FOCUS_STYLE =
            "-fx-background-color: " + CARD + "; -fx-background-radius: 8;"
            + "-fx-border-radius: 8; -fx-border-color: " + PINK + "; -fx-border-width: 2;"
            + "-fx-padding: 7; -fx-text-fill: " + INK + ";";
    public static final String HEADER_STYLE =
            "-fx-background-color: " + SAGE + "; -fx-padding: 12 20 12 20;";
    public static final String APP_BG_STYLE = "-fx-background-color: " + CREAM + ";";

    public static Button applyModernButton(Button b, boolean accent) {
        String base = accent ? BUY_STYLE : SAGE_BTN_STYLE;
        String hover = accent ? BUY_HOVER_STYLE : BUY_STYLE;
        b.setStyle(base);
        b.setOnMouseEntered(e -> { if (!b.isDisabled()) b.setStyle(hover); });
        b.setOnMouseExited(e -> { if (!b.isDisabled()) b.setStyle(base); });
        return b;
    }

    public static <T extends TextInputControl> T applyModernField(T field) {
        field.setStyle(FIELD_STYLE);
        field.focusedProperty().addListener((o, was, is) ->
                field.setStyle(is ? FIELD_FOCUS_STYLE : FIELD_STYLE));
        return field;
    }

    public static <T> ComboBox<T> applyModernCombo(ComboBox<T> combo) {
        combo.setStyle(FIELD_STYLE);
        return combo;
    }

    public static ListView<?> applyModernList(ListView<?> list) {
        list.setStyle(LIST_STYLE);
        return list;
    }

    public static Label heading(String text) {
        Label l = new Label(text);
        l.setStyle(HEADING_STYLE);
        return l;
    }

    public static HBox header(String appName, String username) {
        Label title = new Label(appName);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(12, title, spacer);
        if (username != null && !username.isEmpty()) {
            Label user = new Label("👤 " + username);
            user.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
            bar.getChildren().add(user);
        }
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle(HEADER_STYLE);
        return bar;
    }
    public static StackPane centeredCard(Region content) {
        VBox card = new VBox(content);
        card.setStyle(CARD_STYLE + "-fx-padding: 24;");
        card.setMaxWidth(Region.USE_PREF_SIZE);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane wrapper = new StackPane(card);
        StackPane.setAlignment(card, Pos.CENTER);
        wrapper.setStyle(APP_BG_STYLE);
        return wrapper;
    }

    public static Alert styleAlert(Alert alert) {
        alert.getDialogPane().setStyle(APP_BG_STYLE);
        return alert;
    }
}
