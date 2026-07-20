package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.ArtStyle;
import model.DigProductType;
import model.MediumType;
import model.ProductType;
import network.Client;
import network.Response;
import network.Status;
import network.dto.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DashboardController {

    private final Client client;
    private final UserDTO currentUser;

    private final ExecutorService io = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "server-io");
        t.setDaemon(true);
        return t;
    });

    private final StackPane root = new StackPane();
    private final BorderPane content = new BorderPane();
    private final ProgressIndicator spinner = new ProgressIndicator();

    private final Set<Integer> followedArtistIds = new HashSet<>();

    private Runnable refreshDashboard = () -> {};

    private static final String CREAM           = gui.Styles.CREAM;
    private static final String INK             = gui.Styles.INK;
    private static final String CARD_STYLE       = gui.Styles.CARD_STYLE;
    private static final String CELL_WRAP_STYLE  = gui.Styles.CELL_WRAP_STYLE;
    private static final String LIST_STYLE       = gui.Styles.LIST_STYLE;
    private static final String BUY_STYLE        = gui.Styles.BUY_STYLE;
    private static final String BUY_HOVER_STYLE  = gui.Styles.BUY_HOVER_STYLE;
    private static final String SAGE_BTN_STYLE   = gui.Styles.SAGE_BTN_STYLE;
    private static final String NAME_STYLE       = gui.Styles.NAME_STYLE;
    private static final String SECONDARY_STYLE  = gui.Styles.SECONDARY_STYLE;
    private static final String IMAGE_STYLE      = gui.Styles.IMAGE_STYLE;

    public DashboardController(Client client, UserDTO user) {
        this.client = client;
        this.currentUser = user;
        spinner.setMaxSize(64, 64);
        spinner.setVisible(false);
        root.setStyle("-fx-background-color: " + CREAM + ";");
        content.setStyle("-fx-background-color: " + CREAM + "; -fx-padding: 12;");
        root.getChildren().addAll(content, spinner);
        setupByRole();
    }

    public Pane getView() {
        return root;
    }

    public void shutdown() {
        io.shutdownNow();
    }

    private record ProductRow(int id, int artistId, String artistName, String name, double price,
                              boolean physical, int stock, String detail, String imageUrl) {

        static ProductRow of(PhysicalProductDTO p) {
            return new ProductRow(p.getId(), p.getArtistId(), p.getArtistUsername(), p.getName(),
                    p.getBasePrice(), true, p.getStock(), "Stock: " + p.getStock(), null);
        }

        static ProductRow of(DigitalProductDTO d) {
            return new ProductRow(d.getId(), d.getArtistId(), d.getArtistUsername(), d.getName(),
                    d.getBasePrice(), false, -1, "Format: " + d.getFileExtension(), d.getDownloadUrl());
        }

        String typeLabel() { return physical ? "Physical" : "Digital"; }
        String productType() { return physical ? "PHYSICAL" : "DIGITAL"; }
        boolean soldOut() { return physical && stock <= 0; }
    }

    private void setupByRole() {

        if (currentUser instanceof ArtistDTO artist) {
            setupArtistDashboard(artist);
        } else if (currentUser instanceof SubscriberDTO subscriber) {
            setupSubscriberDashboard(subscriber);
        } else {
            content.setCenter(new Label("Unknown role: cannot build dashboard."));
        }
    }

    private void setupArtistDashboard(ArtistDTO artist) {
        ListView<ProductRow> products = new ListView<>();
        products.setStyle(LIST_STYLE);
        products.setCellFactory(lv -> new ProductCell(null));
        products.setPlaceholder(new Label("You haven't added any art products yet."));

        ListView<SubscriptionDTO> followers = new ListView<>();
        configureListView(followers, SubscriptionDTO::getSubscriberUsername);
        followers.setPlaceholder(new Label("You don't have any subscribers yet"));

        Button addProduct = new Button("New Product");
        addProduct.setStyle(SAGE_BTN_STYLE);
        addProduct.setOnAction(e -> openAddProductDialog(artist, refreshDashboard));

        VBox left = new VBox(8, heading("My Products"), addProduct, products);
        VBox right = new VBox(8, heading("Subscribers"), followers);
        VBox.setVgrow(products, Priority.ALWAYS);
        VBox.setVgrow(followers, Priority.ALWAYS);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        HBox dashboard = new HBox(12, left, right);

        TableView<SalesLogDTO> salesTable = buildSalesTable();

        TabPane tabs = new TabPane(
                tab("Dashboard", dashboard),
                tab("Sales report", salesTable));
        tabs.setStyle("-fx-background-color: " + CREAM + ";");
        content.setCenter(tabs);

        refreshDashboard = () -> refreshArtist(artist, products, followers, salesTable);
        refreshDashboard.run();
    }

    private TableView<SalesLogDTO> buildSalesTable() {
        TableView<SalesLogDTO> table = new TableView<>();
        table.setPlaceholder(new Label("No sales yet"));
        table.setStyle("-fx-background-color: " + gui.Styles.CARD + "; -fx-background-radius: 10;"
                + "-fx-border-radius: 10; -fx-border-color: #ECECEC;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 8, 0, 0, 2);");

        TableColumn<SalesLogDTO, String> buyerCol = new TableColumn<>("Buyer");
        buyerCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getBuyerName()));
        TableColumn<SalesLogDTO, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getProductName()));
        TableColumn<SalesLogDTO, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getProductType()));
        TableColumn<SalesLogDTO, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getPurchaseDate()));

        table.getColumns().add(buyerCol);
        table.getColumns().add(productCol);
        table.getColumns().add(typeCol);
        table.getColumns().add(dateCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private void setupSubscriberDashboard(SubscriberDTO subscriber) {
        ListView<SubscriptionDTO> subs = new ListView<>();
        subs.setStyle(LIST_STYLE);
        subs.setCellFactory(lv -> new SubscriptionCell(s -> unsubscribe(subscriber, s, refreshDashboard)));
        subs.setPlaceholder(new Label("You haven't subscribed to any artists yet."));

        ListView<ProductRow> subscribedArt = new ListView<>();
        subscribedArt.setStyle(LIST_STYLE);
        subscribedArt.setCellFactory(lv -> new ProductCell(row -> purchase(subscriber, row, refreshDashboard)));
        subscribedArt.setPlaceholder(new Label("There are no art products by this artist yet."));

        ListView<ArtistDTO> discover = new ListView<>();
        discover.setStyle(LIST_STYLE);

        Tab artTab = tab("Art Products", subscribedArt);
        TabPane tabs = new TabPane(tab("My Subscriptions", subs), artTab, tab("Discover", discover));
        tabs.setStyle("-fx-background-color: " + CREAM + ";");

        discover.setCellFactory(lv -> new DiscoverCell(
                a -> followedArtistIds.contains(a.getId()),
                a -> subscribe(subscriber, a, refreshDashboard),
                a -> viewArtistProducts(a, subscribedArt, tabs, artTab)
        ));

        content.setCenter(tabs);
        refreshDashboard = () -> refreshSubscriber(subscriber, subs, subscribedArt, discover);
        refreshDashboard.run();
    }


    private void refreshArtist(ArtistDTO artist,
                               ListView<ProductRow> products,
                               ListView<SubscriptionDTO> followers,
                               TableView<SalesLogDTO> salesTable) {

        runAsync(() -> new ArtistData(
                        allProductRows().stream().filter(p -> p.artistId() == artist.getId()).toList(),
                        allSubs().stream().filter(s -> s.getArtistId() == artist.getId()).toList(),
                        salesLogs(artist.getId())),
                data -> Platform.runLater(() -> {
                    products.setItems(FXCollections.observableArrayList(data.products()));
                    followers.setItems(FXCollections.observableArrayList(data.followers()));
                    salesTable.setItems(FXCollections.observableArrayList(data.sales()));
                }));
    }

    private void refreshSubscriber(SubscriberDTO me,
                                   ListView<SubscriptionDTO> subs,
                                   ListView<ProductRow> subscribedArt,
                                   ListView<ArtistDTO> discover) {
        runAsync(() -> {
            List<SubscriptionDTO> mySubs = allSubs().stream()
                    .filter(s -> s.getSubscriberId() == me.getId()).toList();
            Set<Integer> followedIds = mySubs.stream()
                    .map(SubscriptionDTO::getArtistId).collect(Collectors.toSet());
            List<ProductRow> art = allProductRows().stream()
                    .filter(p -> followedIds.contains(p.artistId())).toList();
            List<ArtistDTO> allArtists = allArtists();
            return new SubscriberData(mySubs, followedIds, art, allArtists);
        }, data -> Platform.runLater(() -> {
            followedArtistIds.clear();
            followedArtistIds.addAll(data.followedIds());
            subs.setItems(FXCollections.observableArrayList(data.subscriptions()));
            subscribedArt.setItems(FXCollections.observableArrayList(data.art()));
            if (discover != null) {
                discover.setItems(FXCollections.observableArrayList(data.allArtists()));
                discover.refresh();
            }
        }));
    }

    private void viewArtistProducts(ArtistDTO artist, ListView<ProductRow> target,
                                    TabPane tabs, Tab artTab) {
        runAsync(() -> allProductRows().stream()
                        .filter(p -> p.artistId() == artist.getId()).toList(),
                rows -> Platform.runLater(() -> {
                    target.setItems(FXCollections.observableArrayList(rows));
                    tabs.getSelectionModel().select(artTab);
                }));
    }

    private record ArtistData(List<ProductRow> products, List<SubscriptionDTO> followers,
                              List<SalesLogDTO> sales) {}
    private record SubscriberData(List<SubscriptionDTO> subscriptions, Set<Integer> followedIds,
                                  List<ProductRow> art, List<ArtistDTO> allArtists) {}


    @SuppressWarnings("unchecked")
    private <T> List<T> payloadList(Response r) {
        if (r != null && r.getStatus() == Status.OK && r.getPayload() instanceof List<?> list) {
            return (List<T>) list;
        }
        return List.of();
    }

    private List<ProductRow> allProductRows() {
        try {
            List<ProductRow> rows = new ArrayList<>();
            for (PhysicalProductDTO p : this.<PhysicalProductDTO>payloadList(client.getAllPhysicalProducts())) {
                rows.add(ProductRow.of(p));
            }
            for (DigitalProductDTO d : this.<DigitalProductDTO>payloadList(client.getAllDigitalProducts())) {
                rows.add(ProductRow.of(d));
            }
            return rows;
        } catch (Exception ex) {
            throw new RuntimeException("Loading products failed: " + ex.getMessage(), ex);
        }
    }

    private List<ArtistDTO> allArtists() {
        try { return payloadList(client.getAllArtists()); }
        catch (Exception ex) { throw new RuntimeException("GET_ALL_ARTISTS failed: " + ex.getMessage(), ex); }
    }

    private List<SubscriptionDTO> allSubs() {
        try { return payloadList(client.getAllSubscriptions()); }
        catch (Exception ex) { throw new RuntimeException("GET_ALL_SUBSCRIPTIONS failed: " + ex.getMessage(), ex); }
    }

    private List<SalesLogDTO> salesLogs(int artistId) {
        try { return payloadList(client.getSalesLogsByArtist(artistId)); }
        catch (Exception ex) { throw new RuntimeException("GET_SALES_LOGS_BY_ARTIST failed: " + ex.getMessage(), ex); }
    }


    private void subscribe(SubscriberDTO me, ArtistDTO artist, Runnable onDone) {
        SubscriptionDTO dto = new SubscriptionDTO(0, me.getId(), me.getUsername(),
                artist.getId(), artist.getUsername(), true);
        runAsync(() -> requireOk(callChecked(() -> client.addSubscription(dto))), ok -> onDone.run());
    }

    private void unsubscribe(SubscriberDTO me, SubscriptionDTO sub, Runnable onDone) {
        runAsync(() -> requireOk(callChecked(
                () -> client.removeSubscription(me.getId(), sub.getArtistId()))), ok -> onDone.run());
    }

    private void purchase(SubscriberDTO me, ProductRow row, Runnable onDone) {
        PurchaseDTO dto = new PurchaseDTO(me.getId(), row.id(), row.productType());
        runAsync(() -> requireOk(callChecked(() -> client.addPurchase(dto))), ok -> onDone.run());
    }

    private void openAddProductDialog(ArtistDTO artist, Runnable onDone) {
        Dialog<Supplier<Response>> dialog = new Dialog<>();
        dialog.setTitle("New Product");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ChoiceBox<String> type = new ChoiceBox<>();
        type.getItems().addAll("PHYSICAL", "DIGITAL");
        type.setValue("PHYSICAL");

        TextField name  = new TextField();
        TextField desc  = new TextField();
        TextField price = new TextField("0.0");
        ComboBox<ArtStyle> style = enumBox(ArtStyle.values(), ArtStyle.REALISM);

        TextField stock  = new TextField("1");
        TextField weight = new TextField("1.0");
        ComboBox<MediumType> medium = enumBox(MediumType.values(), MediumType.OIL_PAINTING);
        ComboBox<ProductType> ptype = enumBox(ProductType.values(), ProductType.CANVAS);
        TextField w = new TextField("20"), h = new TextField("30"), d = new TextField("2");

        TextField url  = new TextField("https://");
        TextField size = new TextField("10.0");
        TextField ext  = new TextField(".png");
        ComboBox<DigProductType> dtype = enumBox(DigProductType.values(), DigProductType.POSTER);

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(6); grid.setPadding(new Insets(10));
        Runnable layout = () -> {
            grid.getChildren().clear();
            int row = 0;
            row = addRow(grid, row, "Type", type);
            row = addRow(grid, row, "Name", name);
            row = addRow(grid, row, "Description", desc);
            row = addRow(grid, row, "Price", price);
            row = addRow(grid, row, "Style", style);
            if ("PHYSICAL".equals(type.getValue())) {
                row = addRow(grid, row, "Stock", stock);
                row = addRow(grid, row, "Weight", weight);
                row = addRow(grid, row, "Medium", medium);
                row = addRow(grid, row, "Product type", ptype);
                addRow(grid, row, "Size W/H/D", new HBox(4, w, h, d));
            } else {
                row = addRow(grid, row, "Download-URL", url);
                row = addRow(grid, row, "Size (MB)", size);
                row = addRow(grid, row, "File extension", ext);
                addRow(grid, row, "Digital type", dtype);
            }
        };
        layout.run();
        type.setOnAction(e -> layout.run());
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            try {
                if ("PHYSICAL".equals(type.getValue())) {
                    PhysicalProductDTO dto = new PhysicalProductDTO(0, artist.getId(),
                            name.getText(), desc.getText(), parseD(price), artist.getUsername(),
                            style.getValue().name(), parseI(stock), parseD(weight),
                            medium.getValue().name(), ptype.getValue().name(),
                            new DimensionsDTO(parseD(w), parseD(h), parseD(d)));
                    return () -> callChecked(() -> client.addPhysicalProduct(dto));
                } else {
                    DigitalProductDTO dto = new DigitalProductDTO(0, artist.getId(),
                            name.getText(), desc.getText(), parseD(price), artist.getUsername(),
                            style.getValue().name(), url.getText(), parseD(size), ext.getText(),
                            dtype.getValue().name());
                    return () -> callChecked(() -> client.addDigitalProduct(dto));
                }
            } catch (NumberFormatException ex) {
                showError("Invalid number: " + ex.getMessage());
                return null;
            }
        });

        dialog.showAndWait().ifPresent(call ->
                runAsync(() -> requireOk(call.get()), ok -> onDone.run()));
    }

    private final class ProductCell extends ListCell<ProductRow> {
        private final ImageView imageView = new ImageView();
        private final Label name = new Label();
        private final Label artist = new Label();
        private final Label type = new Label();
        private final Label detail = new Label();
        private final Label price = new Label();
        private final Button buy = new Button("Buy");
        private final HBox box;
        private final Consumer<ProductRow> onBuy;

        ProductCell(Consumer<ProductRow> onBuy) {
            this.onBuy = onBuy;
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            imageView.setStyle(IMAGE_STYLE);

            box = new HBox(12, imageView, name, artist, type, detail, spacer(), price);
            box.setAlignment(Pos.CENTER_LEFT);
            box.setStyle(CARD_STYLE);

            name.setStyle(NAME_STYLE);
            artist.setStyle(SECONDARY_STYLE);
            type.setStyle("-fx-text-fill: #999999;");
            detail.setStyle(SECONDARY_STYLE);
            price.setStyle("-fx-text-fill: " + INK + "; -fx-font-weight: bold;");

            setStyle(CELL_WRAP_STYLE);

            if (onBuy != null) {
                imageView.setOnMouseClicked(e -> {
                    ProductRow row = getItem();
                    if (row != null && row.imageUrl() != null) {
                        showLargeImage(row.imageUrl(), row.name());
                    }
                });
                buy.setStyle(BUY_STYLE);
                buy.setOnMouseEntered(e -> { if (!buy.isDisabled()) buy.setStyle(BUY_HOVER_STYLE); });
                buy.setOnMouseExited(e -> { if (!buy.isDisabled()) buy.setStyle(BUY_STYLE); });
                box.getChildren().add(buy);
                buy.setOnAction(e -> {
                    ProductRow row = getItem();
                    if (row != null) onBuy.accept(row);
                });
            }
        }

        private void showLargeImage(String s, String name) {
        }

        @Override protected void updateItem(ProductRow item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);

            if (item != null && item.imageUrl() != null) {
                imageView.setImage(new Image(item.imageUrl(), true));
            } else {
                imageView.setImage(null);
            }

            if (empty || item == null) {
                setGraphic(null);
                return;
            }
            name.setText(item.name());
            artist.setText("Artist: " + item.artistName());
            type.setText("[" + item.typeLabel() + "]");
            detail.setText(item.detail());
            price.setText(item.price() + "€");
            if (onBuy != null) {
                buy.setDisable(item.soldOut());
                buy.setText(item.soldOut() ? "Sold out" : "Buy");
            }
            setGraphic(box);
        }
    }


    private final class SubscriptionCell extends ListCell<SubscriptionDTO> {
        private final Label label = new Label();
        private final Button unsubscribe = new Button("Unsubscribe");
        private final HBox box = new HBox(10, label, spacer(), unsubscribe);
        private final Consumer<SubscriptionDTO> onUnsubscribe;

        SubscriptionCell(Consumer<SubscriptionDTO> onUnsubscribe) {
            this.onUnsubscribe = onUnsubscribe;
            box.setAlignment(Pos.CENTER_LEFT);
            box.setStyle(CARD_STYLE);
            label.setStyle(NAME_STYLE);
            setStyle(CELL_WRAP_STYLE);
            unsubscribe.setStyle(BUY_STYLE);
            unsubscribe.setOnMouseEntered(e -> unsubscribe.setStyle(BUY_HOVER_STYLE));
            unsubscribe.setOnMouseExited(e -> unsubscribe.setStyle(BUY_STYLE));
            unsubscribe.setOnAction(e -> {
                SubscriptionDTO s = getItem();
                if (s != null) onUnsubscribe.accept(s);
            });
        }

        @Override protected void updateItem(SubscriptionDTO item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }
            label.setText(item.getArtistUsername());
            setGraphic(box);
        }
    }


    private final class DiscoverCell extends ListCell<ArtistDTO> {
        private final Label label = new Label();
        private final Button action = new Button();
        private final HBox box = new HBox(10, label, spacer(), action);
        private final Predicate<ArtistDTO> isSubscribed;
        private final Consumer<ArtistDTO> onSubscribe;
        private final Consumer<ArtistDTO> onViewProducts;

        DiscoverCell(Predicate<ArtistDTO> isSubscribed,
                     Consumer<ArtistDTO> onSubscribe,
                     Consumer<ArtistDTO> onViewProducts) {
            this.isSubscribed = isSubscribed;
            this.onSubscribe = onSubscribe;
            this.onViewProducts = onViewProducts;
            box.setAlignment(Pos.CENTER_LEFT);
            box.setStyle(CARD_STYLE);
            label.setStyle(NAME_STYLE);
            setStyle(CELL_WRAP_STYLE);
        }

        @Override protected void updateItem(ArtistDTO item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }
            label.setText(item.getUsername());
            if (isSubscribed.test(item)) {
                action.setText("See the artworks");
                action.setStyle(SAGE_BTN_STYLE);
                action.setOnAction(e -> onViewProducts.accept(item));
            } else {
                action.setText("Subscribe");
                action.setStyle(BUY_STYLE);
                action.setOnAction(e -> onSubscribe.accept(item));
            }
            setGraphic(box);
        }
    }

    private <T> void runAsync(Supplier<T> work, Consumer<T> onSuccess) {
        Task<T> task = new Task<>() {
            @Override protected T call() { return work.get(); }
        };
        task.setOnRunning(e -> setLoading(true));
        task.setOnSucceeded(e -> { setLoading(false); onSuccess.accept(task.getValue()); });
        task.setOnFailed(e -> {
            setLoading(false);
            Throwable ex = task.getException();
            showError(ex == null ? "Unknown error" : ex.getMessage());
        });
        io.execute(task);
    }

    private void setLoading(boolean loading) {
        spinner.setVisible(loading);
        content.setDisable(loading);
    }

    private Response callChecked(CheckedCall call) {
        try {
            return call.run();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private boolean requireOk(Response r) {
        if (r == null || r.getStatus() != Status.OK) {
            throw new RuntimeException(r == null ? "No response" : r.getMessage());
        }
        return true;
    }

    @FunctionalInterface
    private interface CheckedCall { Response run() throws Exception; }


    private <T> void configureListView(ListView<T> listView, Function<T, String> displayFunction) {
        listView.setStyle(LIST_STYLE);
        listView.setCellFactory(lv -> new ListCell<>() {
            private final Label label = new Label();
            private final HBox box = new HBox(label);

            {
                box.setAlignment(Pos.CENTER_LEFT);
                box.setStyle(CARD_STYLE);
                label.setStyle(NAME_STYLE);
                setStyle(CELL_WRAP_STYLE);
            }

            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(displayFunction.apply(item));
                    setGraphic(box);
                }
            }
        });
    }

    private <E extends Enum<E>> ComboBox<E> enumBox(E[] values, E selected) {
        ComboBox<E> box = new ComboBox<>();
        box.getItems().addAll(values);
        box.setValue(selected);
        return box;
    }

    private Tab tab(String title, javafx.scene.Node c) {
        Tab t = new Tab(title, c);
        t.setClosable(false);
        return t;
    }

    private Label heading(String text) {
        return gui.Styles.heading(text);
    }

    private int addRow(GridPane g, int row, String labelText, javafx.scene.Node field) {
        g.add(new Label(labelText), 0, row);
        g.add(field, 1, row);
        return row + 1;
    }

    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    private double parseD(TextField f) { return Double.parseDouble(f.getText().trim()); }
    private int    parseI(TextField f) { return Integer.parseInt(f.getText().trim()); }

    private void showError(String msg) {
        gui.Styles.styleAlert(new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK)).showAndWait();
    }

    private void showLargeImage(String imageUrl, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);

        ImageView bigView = new ImageView(new Image(imageUrl, true));
        bigView.setPreserveRatio(true);
        bigView.setFitWidth(600);

        StackPane root = new StackPane(bigView);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();

    }
}
