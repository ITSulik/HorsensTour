package library.resep1.View;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.Model.Entities.Trip;
import library.resep1.ViewModel.BusViewModel;
import library.resep1.ViewModel.ChauffeurViewModel;
import library.resep1.ViewModel.TripViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class DashboardController {

    // ---------------------------------------------------------------
    // Sidebar
    // ---------------------------------------------------------------

    @FXML private ToggleButton planningNavButton;
    @FXML private ToggleButton busesNavButton;
    @FXML private ToggleButton chauffeursNavButton;

    @FXML private VBox planningSection;
    @FXML private VBox busesSection;
    @FXML private VBox chauffeursSection;


    // ---------------------------------------------------------------
    // Planning
    // ---------------------------------------------------------------

    @FXML private TableView<Trip> tripTable;
    @FXML private TableColumn<Trip, String> tripIdColumn;
    @FXML private TableColumn<Trip, String> destinationColumn;
    @FXML private TableColumn<Trip, String> startColumn;
    @FXML private TableColumn<Trip, String> endColumn;
    @FXML private TableColumn<Trip, String> busColumn;
    @FXML private TableColumn<Trip, String> chauffeurColumn;
    @FXML private TableColumn<Trip, String> tripStatusColumn;
    @FXML private TableColumn<Trip, Trip> tripActionsColumn;


    // ---------------------------------------------------------------
    // Buses
    // ---------------------------------------------------------------

    @FXML private TableView<Bus> busTable;
    @FXML private TableColumn<Bus, String> busNumberColumn;
    @FXML private TableColumn<Bus, String> busTypeColumn;
    @FXML private TableColumn<Bus, Number> busCapacityColumn;
    @FXML private TableColumn<Bus, String> busPurposeColumn;
    @FXML private TableColumn<Bus, String> busStatusColumn;
    @FXML private TableColumn<Bus, Bus> busActionsColumn;


    // ---------------------------------------------------------------
    // Chauffeurs
    // ---------------------------------------------------------------

    @FXML private TableView<Chauffeur> chauffeurTable;
    @FXML private TableColumn<Chauffeur, String> chauffeurNameColumn;
    @FXML private TableColumn<Chauffeur, Number> chauffeurExperienceColumn;
    @FXML private TableColumn<Chauffeur, String> chauffeurPreferencesColumn;
    @FXML private TableColumn<Chauffeur, String> chauffeurStatusColumn;
    @FXML private TableColumn<Chauffeur, Chauffeur> chauffeurActionsColumn;


    // ---------------------------------------------------------------
    // ViewModels
    // ---------------------------------------------------------------

    private final TripViewModel tripVM = TripViewModel.getInstance();
    private final BusViewModel busVM = BusViewModel.getInstance();
    private final ChauffeurViewModel chauffeurVM = ChauffeurViewModel.getInstance();


    // ---------------------------------------------------------------
    // Table data
    // ---------------------------------------------------------------

    private final ObservableList<Trip> trips = FXCollections.observableArrayList();
    private final ObservableList<Bus> buses = FXCollections.observableArrayList();
    private final ObservableList<Chauffeur> chauffeurs = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");


    // ---------------------------------------------------------------
    // Initialization
    // ---------------------------------------------------------------

    @FXML
    public void initialize() {
        ToggleGroup navGroup = new ToggleGroup();
        planningNavButton.setToggleGroup(navGroup);
        busesNavButton.setToggleGroup(navGroup);
        chauffeursNavButton.setToggleGroup(navGroup);
        planningNavButton.setSelected(true);

        setUpTripTable();
        setUpBusTable();
        setUpChauffeurTable();

        refreshAll();
        showSection(planningSection);
    }


    // ---------------------------------------------------------------
    // Navigation
    // ---------------------------------------------------------------

    @FXML
    private void onShowPlanning() {
        showSection(planningSection);
    }

    @FXML
    private void onShowBuses() {
        showSection(busesSection);
    }

    @FXML
    private void onShowChauffeurs() {
        showSection(chauffeursSection);
    }

    private void showSection(VBox section) {
        planningSection.setVisible(section == planningSection);
        planningSection.setManaged(section == planningSection);

        busesSection.setVisible(section == busesSection);
        busesSection.setManaged(section == busesSection);

        chauffeursSection.setVisible(section == chauffeursSection);
        chauffeursSection.setManaged(section == chauffeursSection);
    }


    // ---------------------------------------------------------------
    // Data refresh
    // ---------------------------------------------------------------

    private void refreshAll() {
        trips.setAll(tripVM.getAllTrips());
        buses.setAll(busVM.getAllBuses());
        chauffeurs.setAll(chauffeurVM.getAllChauffeurs());

        tripTable.refresh();
        busTable.refresh();
        chauffeurTable.refresh();
    }


    // ===============================================================
    // TRIP TABLE
    // ===============================================================

    private void setUpTripTable() {
        tripIdColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTripId()));

        destinationColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDestination()));

        startColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStartTime().format(DATE_TIME_FORMATTER)));

        endColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEndTime().format(DATE_TIME_FORMATTER)));

        busColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().hasBusAssigned()
                                ? data.getValue().getBusNumber()
                                : "—"
                ));

        chauffeurColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().hasChauffeurAssigned()
                                ? data.getValue().getChauffeurName()
                                : "—"
                ));

        tripStatusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(tripStatus(data.getValue())));

        tripStatusColumn.setCellFactory(column ->
                badgeCell(status -> switch (status) {
                    case "Assigned" -> "badge-assigned";
                    case "Completed" -> "badge-completed";
                    default -> "badge-unassigned";
                }));

        tripActionsColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue()));

        tripActionsColumn.setCellFactory(column -> new TableCell<Trip, Trip>() {

            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            private final HBox box = new HBox(6, viewButton, editButton, deleteButton);

            {
                box.setAlignment(Pos.CENTER_LEFT);

                viewButton.getStyleClass().add("btn-link");
                editButton.getStyleClass().add("btn-link");
                deleteButton.getStyleClass().addAll("btn-link", "btn-link-danger");

                viewButton.setOnAction(event -> onViewTrip(getTableRow().getItem()));
                editButton.setOnAction(event -> onEditTrip(getTableRow().getItem()));
                deleteButton.setOnAction(event -> onDeleteTrip(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Trip trip, boolean empty) {
                super.updateItem(trip, empty);
                setGraphic(empty || trip == null ? null : box);
            }
        });

        tripTable.setItems(trips);
        tripTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tripTable.setPlaceholder(new Label("No trips yet. Click \"+ New trip\" to plan one."));
    }

    private String tripStatus(Trip trip) {
        if (!trip.hasBusAssigned() || !trip.hasChauffeurAssigned()) {
            return "Unassigned";
        }

        if (trip.getEndTime().isBefore(LocalDateTime.now())) {
            return "Completed";
        }

        return "Assigned";
    }

    @FXML
    private void onNewTrip() {
        boolean saved = AddTripController.showDialog(windowOf(tripTable));

        if (saved) {
            refreshAll();
        }
    }

    @FXML
    private void onCheckAvailability() {
        CheckAvailabilityController.showDialog(windowOf(tripTable));
    }

    private void onViewTrip(Trip trip) {
        if (trip == null) {
            return;
        }

        ViewTripController.showDialog(windowOf(tripTable), trip);
    }

    private void onEditTrip(Trip trip) {
        if (trip == null) {
            return;
        }

        boolean saved = EditTripController.showDialog(windowOf(tripTable), trip);

        if (saved) {
            refreshAll();
        }
    }

    private void onDeleteTrip(Trip trip) {
        if (trip == null) {
            return;
        }

        boolean confirmed = ConfirmDeleteController.showDialog(
                windowOf(tripTable),
                "Delete trip",
                trip.getTripId() + " (" + trip.getDestination() + ")",
                "Delete trip"
        );

        if (confirmed) {
            tripVM.deleteTrip(trip.getTripId());
            refreshAll();
        }
    }


    // ===============================================================
    // BUS TABLE
    // ===============================================================

    private void setUpBusTable() {
        busNumberColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBusNumber()));

        busTypeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(Formatting.prettifyBusType(data.getValue().getType())));

        busCapacityColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCapacity()));

        busPurposeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPurpose()));

        busStatusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(busStatus(data.getValue())));

        busStatusColumn.setCellFactory(column ->
                badgeCell(status -> "On trip".equals(status) ? "badge-on-trip" : "badge-available"));

        busActionsColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue()));

        busActionsColumn.setCellFactory(column -> new TableCell<Bus, Bus>() {

            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            private final HBox box = new HBox(6, editButton, deleteButton);

            {
                box.setAlignment(Pos.CENTER_LEFT);

                editButton.getStyleClass().add("btn-link");
                deleteButton.getStyleClass().addAll("btn-link", "btn-link-danger");

                editButton.setOnAction(event -> onEditBus(getTableRow().getItem()));
                deleteButton.setOnAction(event -> onDeleteBus(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Bus bus, boolean empty) {
                super.updateItem(bus, empty);
                setGraphic(empty || bus == null ? null : box);
            }
        });

        busTable.setItems(buses);
        busTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        busTable.setPlaceholder(new Label("No buses yet. Click \"+ New bus\" to add one."));
    }

    private String busStatus(Bus bus) {
        for (Trip trip : tripVM.getAllTrips()) {
            if (trip.hasBusAssigned()
                    && trip.getBusNumber().equalsIgnoreCase(bus.getBusNumber())
                    && trip.getEndTime().isAfter(LocalDateTime.now())) {
                return "On trip";
            }
        }

        return "Available";
    }

    @FXML
    private void onNewBus() {
        boolean saved = AddBusController.showDialog(windowOf(busTable));

        if (saved) {
            refreshAll();
        }
    }

    private void onEditBus(Bus bus) {
        if (bus == null) {
            return;
        }

        boolean saved = EditBusController.showDialog(windowOf(busTable), bus);

        if (saved) {
            refreshAll();
        }
    }

    private void onDeleteBus(Bus bus) {
        if (bus == null) {
            return;
        }

        int upcoming = countUpcomingTripsForBus(bus);

        if (upcoming > 0) {
            CannotDeleteController.showDialog(windowOf(busTable), "bus", bus.getBusNumber(), upcoming);
            return;
        }

        boolean confirmed = ConfirmDeleteController.showDialog(
                windowOf(busTable),
                "Delete bus",
                bus.getBusNumber(),
                "Delete bus"
        );

        if (confirmed) {
            busVM.deleteBus(bus.getBusID());
            refreshAll();
        }
    }

    private int countUpcomingTripsForBus(Bus bus) {
        int count = 0;

        for (Trip trip : tripVM.getAllTrips()) {
            if (trip.hasBusAssigned()
                    && trip.getBusNumber().equalsIgnoreCase(bus.getBusNumber())
                    && trip.getEndTime().isAfter(LocalDateTime.now())) {
                count++;
            }
        }

        return count;
    }


    // ===============================================================
    // CHAUFFEUR TABLE
    // ===============================================================

    private void setUpChauffeurTable() {
        chauffeurNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        chauffeurExperienceColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getExperience()));

        chauffeurPreferencesColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPreferences() == null || data.getValue().getPreferences().isBlank()
                                ? "—"
                                : data.getValue().getPreferences()
                ));

        chauffeurStatusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(chauffeurStatus(data.getValue())));

        chauffeurStatusColumn.setCellFactory(column ->
                badgeCell(status -> "On trip".equals(status) ? "badge-on-trip" : "badge-available"));

        chauffeurActionsColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue()));

        chauffeurActionsColumn.setCellFactory(column -> new TableCell<Chauffeur, Chauffeur>() {

            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            private final HBox box = new HBox(6, editButton, deleteButton);

            {
                box.setAlignment(Pos.CENTER_LEFT);

                editButton.getStyleClass().add("btn-link");
                deleteButton.getStyleClass().addAll("btn-link", "btn-link-danger");

                editButton.setOnAction(event -> onEditChauffeur(getTableRow().getItem()));
                deleteButton.setOnAction(event -> onDeleteChauffeur(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Chauffeur chauffeur, boolean empty) {
                super.updateItem(chauffeur, empty);
                setGraphic(empty || chauffeur == null ? null : box);
            }
        });

        chauffeurTable.setItems(chauffeurs);
        chauffeurTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        chauffeurTable.setPlaceholder(new Label("No chauffeurs yet. Click \"+ New chauffeur\" to add one."));
    }

    private String chauffeurStatus(Chauffeur chauffeur) {
        for (Trip trip : tripVM.getAllTrips()) {
            if (trip.hasChauffeurAssigned()
                    && trip.getChauffeurName().equalsIgnoreCase(chauffeur.getName())
                    && trip.getEndTime().isAfter(LocalDateTime.now())) {
                return "On trip";
            }
        }

        return "Available";
    }

    @FXML
    private void onNewChauffeur() {
        boolean saved = AddChauffeurController.showDialog(windowOf(chauffeurTable));

        if (saved) {
            refreshAll();
        }
    }

    private void onEditChauffeur(Chauffeur chauffeur) {
        if (chauffeur == null) {
            return;
        }

        boolean saved = EditChauffeurController.showDialog(windowOf(chauffeurTable), chauffeur);

        if (saved) {
            refreshAll();
        }
    }

    private void onDeleteChauffeur(Chauffeur chauffeur) {
        if (chauffeur == null) {
            return;
        }

        int upcoming = countUpcomingTripsForChauffeur(chauffeur);

        if (upcoming > 0) {
            CannotDeleteController.showDialog(windowOf(chauffeurTable), "chauffeur", chauffeur.getName(), upcoming);
            return;
        }

        boolean confirmed = ConfirmDeleteController.showDialog(
                windowOf(chauffeurTable),
                "Delete chauffeur",
                chauffeur.getName(),
                "Delete chauffeur"
        );

        if (confirmed) {
            chauffeurVM.deleteChauffeur(chauffeur.getChauffeurID());
            refreshAll();
        }
    }

    private int countUpcomingTripsForChauffeur(Chauffeur chauffeur) {
        int count = 0;

        for (Trip trip : tripVM.getAllTrips()) {
            if (trip.hasChauffeurAssigned()
                    && trip.getChauffeurName().equalsIgnoreCase(chauffeur.getName())
                    && trip.getEndTime().isAfter(LocalDateTime.now())) {
                count++;
            }
        }

        return count;
    }


    // ===============================================================
    // Helpers
    // ===============================================================

    private <S> TableCell<S, String> badgeCell(Function<String, String> styleClassResolver) {
        return new TableCell<>() {

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                Label badge = new Label(status);
                badge.getStyleClass().addAll("badge", styleClassResolver.apply(status));

                setGraphic(badge);
            }
        };
    }

    private Window windowOf(Node node) {
        return node.getScene().getWindow();
    }
}