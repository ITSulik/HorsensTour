package library.resep1.View;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.ViewModel.BusViewModel;
import library.resep1.ViewModel.ChauffeurViewModel;
import library.resep1.ViewModel.TripViewModel;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AssignResourcesController {

    public static final class Result {
        public String busNumber;
        public String chauffeurName;
    }

    @FXML private Label titleLabel;
    @FXML private Label timeRangeLabel;
    @FXML private VBox emptyStateBox;
    @FXML private Label emptyTimeSlotLabel;
    @FXML private VBox resultsBox;
    @FXML private VBox busesListBox;
    @FXML private VBox chauffeursListBox;

    private BusViewModel busVM;
    private ChauffeurViewModel chauffeurVM;
    private TripViewModel tripVM;

    private Stage dialogStage;

    private final Result result = new Result();

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public void init(
            BusViewModel busVM,
            ChauffeurViewModel chauffeurVM,
            TripViewModel tripVM,
            String tripLabel,
            String destination,
            LocalDateTime start,
            LocalDateTime end,
            String excludeTripId,
            Stage dialogStage
    ) {
        this.busVM = busVM;
        this.chauffeurVM = chauffeurVM;
        this.tripVM = tripVM;
        this.dialogStage = dialogStage;

        String header = "Assign resources: " + tripLabel;

        if (destination != null && !destination.isBlank()) {
            header += " " + destination;
        }

        titleLabel.setText(header);

        timeRangeLabel.setText(
                "Using the trip's start and end time: "
                        + start.format(DATE_TIME_FORMATTER)
                        + " – "
                        + end.format(DATE_TIME_FORMATTER)
        );

        List<Bus> availableBuses = getAvailableBuses(start, end);
        List<Chauffeur> availableChauffeurs = getAvailableChauffeurs(start, end);

        if (availableBuses.isEmpty() && availableChauffeurs.isEmpty()) {
            showEmptyState(start, end);
            return;
        }

        hideEmptyState();
        showResults();

        populateBuses(availableBuses);
        populateChauffeurs(availableChauffeurs);
    }

    private List<Bus> getAvailableBuses(LocalDateTime start, LocalDateTime end) {
        return tripVM.getAvailableBuses(start, end, busVM.getAllBuses());
    }

    private List<Chauffeur> getAvailableChauffeurs(LocalDateTime start, LocalDateTime end) {
        return tripVM.getAvailableChauffeurs(start, end, chauffeurVM.getAllChauffeurs());
    }

    private void showEmptyState(LocalDateTime start, LocalDateTime end) {
        emptyStateBox.setVisible(true);
        emptyStateBox.setManaged(true);

        emptyTimeSlotLabel.setText(
                "Time slot: "
                        + start.format(DATE_TIME_FORMATTER)
                        + " – "
                        + end.format(DATE_TIME_FORMATTER)
        );

        resultsBox.setVisible(false);
        resultsBox.setManaged(false);
    }

    private void hideEmptyState() {
        emptyStateBox.setVisible(false);
        emptyStateBox.setManaged(false);
    }

    private void showResults() {
        resultsBox.setVisible(true);
        resultsBox.setManaged(true);
    }

    private void populateBuses(List<Bus> buses) {
        busesListBox.getChildren().clear();

        if (buses.isEmpty()) {
            busesListBox.getChildren().add(
                    helperLabel("No buses available for this time slot.")
            );
            return;
        }

        for (Bus bus : buses) {
            busesListBox.getChildren().add(buildBusRow(bus));
        }
    }

    private void populateChauffeurs(List<Chauffeur> chauffeurs) {
        chauffeursListBox.getChildren().clear();

        if (chauffeurs.isEmpty()) {
            chauffeursListBox.getChildren().add(
                    helperLabel("No chauffeurs available for this time slot.")
            );
            return;
        }

        for (Chauffeur chauffeur : chauffeurs) {
            chauffeursListBox.getChildren().add(buildChauffeurRow(chauffeur));
        }
    }

    private Label helperLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("helper-text");
        return label;
    }

    private HBox buildBusRow(Bus bus) {
        VBox info = new VBox(2);
        Label name = new Label(bus.getBusNumber());
        name.getStyleClass().add("resource-name");

        Label details = new Label(
                Formatting.prettifyBusType(bus.getType())
                        + " · "
                        + bus.getCapacity()
                        + " seats · "
                        + bus.getPurpose()
        );
        details.getStyleClass().add("resource-details");

        info.getChildren().addAll(name, details);

        Button assignButton = new Button("Assign");
        assignButton.getStyleClass().add("btn-secondary-small");

        assignButton.setOnAction(e -> {
            result.busNumber = bus.getBusNumber();
            disableAllButtons(busesListBox, assignButton);
        });

        HBox row = new HBox(12, info, spacer(), assignButton);
        row.getStyleClass().add("resource-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox buildChauffeurRow(Chauffeur chauffeur) {
        VBox info = new VBox(2);
        Label name = new Label(chauffeur.getName());
        name.getStyleClass().add("resource-name");

        String preferences =
                chauffeur.getPreferences() == null || chauffeur.getPreferences().isBlank()
                        ? "No preferences noted"
                        : chauffeur.getPreferences();

        Label details = new Label(
                chauffeur.getExperience() + " yrs experience · " + preferences
        );
        details.getStyleClass().add("resource-details");

        info.getChildren().addAll(name, details);

        Button assignButton = new Button("Assign");
        assignButton.getStyleClass().add("btn-secondary-small");

        assignButton.setOnAction(e -> {
            result.chauffeurName = chauffeur.getName();
            disableAllButtons(chauffeursListBox, assignButton);
        });

        HBox row = new HBox(12, info, spacer(), assignButton);
        row.getStyleClass().add("resource-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void disableAllButtons(VBox listBox, Button clicked) {
        for (Node node : listBox.getChildren()) {
            if (node instanceof HBox row) {
                for (Node child : row.getChildren()) {
                    if (child instanceof Button button) {
                        button.setDisable(true);
                        if (button == clicked) {
                            button.setText("Assigned");
                        }
                    }
                }
            }
        }
    }

    private Region spacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }

    public static Result showDialog(
            Window owner,
            BusViewModel busVM,
            ChauffeurViewModel chauffeurVM,
            TripViewModel tripVM,
            String tripLabel,
            String destination,
            LocalDateTime start,
            LocalDateTime end,
            String excludeTripId
    ) {
        try {
            DialogUtil.Loaded<AssignResourcesController> loaded =
                    DialogUtil.load(
                            "/library/resep1/assignResourcesView.fxml",
                            "Assign resources",
                            owner
                    );

            loaded.controller.init(
                    busVM,
                    chauffeurVM,
                    tripVM,
                    tripLabel,
                    destination,
                    start,
                    end,
                    excludeTripId,
                    loaded.stage
            );
            loaded.stage.showAndWait();
            return loaded.controller.result;

        } catch (IOException e) {
            throw new RuntimeException("Failed to open assign resources dialog", e);
        }
    }
}