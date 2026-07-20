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
import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Collections.ChauffeurList;
import library.resep1.Model.Collections.TripList;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.Model.Entities.Trip;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * "Assign resources" popup scoped to one trip's time window. Availability
 * is computed locally here (rather than via TripList#getAvailableBuses /
 * #getAvailableChauffeurs) so the trip currently being created/edited can
 * be excluded from its own conflict check - TripList's helper methods
 * don't take an "exclude this trip" parameter.
 *
 * Picks made here are returned via {@link Result}, not applied directly:
 * the caller (TripFormController for a pending trip, or the dashboard for
 * an already-saved one) decides when to persist them.
 */
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

    private Stage dialogStage;
    private final Result result = new Result();

    public void init(BusList busList, ChauffeurList chauffeurList, TripList tripList,
                      String tripLabel, String destination, LocalDateTime start, LocalDateTime end,
                      String excludeTripId, Stage dialogStage) {
        this.dialogStage = dialogStage;

        String header = "Assign resources: " + tripLabel;
        if (destination != null && !destination.isBlank()) {
            header += " " + destination;
        }
        titleLabel.setText(header);
        timeRangeLabel.setText("Using the trip's start and end time: "
                + DateTimeUtil.format(start) + " \u2013 " + DateTimeUtil.format(end));

        List<Bus> availableBuses = availableBuses(busList, tripList, start, end, excludeTripId);
        List<Chauffeur> availableChauffeurs = availableChauffeurs(chauffeurList, tripList, start, end, excludeTripId);

        if (availableBuses.isEmpty() && availableChauffeurs.isEmpty()) {
            emptyStateBox.setVisible(true);
            emptyStateBox.setManaged(true);
            emptyTimeSlotLabel.setText(
                    "Time slot: " + DateTimeUtil.format(start) + " \u2013 " + DateTimeUtil.format(end));
            resultsBox.setVisible(false);
            resultsBox.setManaged(false);
            return;
        }

        emptyStateBox.setVisible(false);
        emptyStateBox.setManaged(false);
        resultsBox.setVisible(true);
        resultsBox.setManaged(true);

        busesListBox.getChildren().clear();
        if (availableBuses.isEmpty()) {
            busesListBox.getChildren().add(helperLabel("No buses available for this time slot."));
        } else {
            for (Bus bus : availableBuses) {
                busesListBox.getChildren().add(buildBusRow(bus));
            }
        }

        chauffeursListBox.getChildren().clear();
        if (availableChauffeurs.isEmpty()) {
            chauffeursListBox.getChildren().add(helperLabel("No chauffeurs available for this time slot."));
        } else {
            for (Chauffeur chauffeur : availableChauffeurs) {
                chauffeursListBox.getChildren().add(buildChauffeurRow(chauffeur));
            }
        }
    }

    private List<Bus> availableBuses(BusList busList, TripList tripList, LocalDateTime start, LocalDateTime end,
                                      String excludeTripId) {
        List<Bus> available = new ArrayList<>();
        for (Bus bus : busList.getAllBuses()) {
            if (!isBusBusy(tripList, bus, start, end, excludeTripId)) {
                available.add(bus);
            }
        }
        return available;
    }

    private boolean isBusBusy(TripList tripList, Bus bus, LocalDateTime start, LocalDateTime end, String excludeTripId) {
        for (Trip trip : tripList.getAllTrips()) {
            if (excludeTripId != null && trip.getTripId().equalsIgnoreCase(excludeTripId)) {
                continue;
            }
            if (trip.hasBusAssigned() && trip.getBusNumber().equalsIgnoreCase(bus.getBusNumber())
                    && trip.overlaps(start, end)) {
                return true;
            }
        }
        return false;
    }

    private List<Chauffeur> availableChauffeurs(ChauffeurList chauffeurList, TripList tripList, LocalDateTime start,
                                                 LocalDateTime end, String excludeTripId) {
        List<Chauffeur> available = new ArrayList<>();
        for (Chauffeur chauffeur : chauffeurList.getAllChauffeurs()) {
            if (!isChauffeurBusy(tripList, chauffeur, start, end, excludeTripId)) {
                available.add(chauffeur);
            }
        }
        return available;
    }

    private boolean isChauffeurBusy(TripList tripList, Chauffeur chauffeur, LocalDateTime start, LocalDateTime end,
                                     String excludeTripId) {
        for (Trip trip : tripList.getAllTrips()) {
            if (excludeTripId != null && trip.getTripId().equalsIgnoreCase(excludeTripId)) {
                continue;
            }
            if (trip.hasChauffeurAssigned() && trip.getChauffeurName().equalsIgnoreCase(chauffeur.getName())
                    && trip.overlaps(start, end)) {
                return true;
            }
        }
        return false;
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
        Label details = new Label(Formatting.prettifyBusType(bus.getType()) + " \u00b7 " + bus.getCapacity()
                + " seats \u00b7 " + bus.getPurpose());
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
        String preferences = (chauffeur.getPreferences() == null || chauffeur.getPreferences().isBlank())
                ? "No preferences noted" : chauffeur.getPreferences();
        Label details = new Label(chauffeur.getExperience() + " yrs experience \u00b7 " + preferences);
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

    /**
     * Opens the dialog and blocks until it is closed. {@code excludeTripId}
     * should be the id of the trip being edited (or {@code null} for a new,
     * not-yet-saved trip) so its own current assignment doesn't count as a
     * conflict against itself.
     */
    public static Result showDialog(Window owner, BusList busList, ChauffeurList chauffeurList, TripList tripList,
                                     String tripLabel, String destination, LocalDateTime start, LocalDateTime end,
                                     String excludeTripId) {
        try {
            DialogUtil.Loaded<AssignResourcesController> loaded =
                    DialogUtil.load("/library/resep1/assignResourcesView.fxml", "Assign resources", owner);
            loaded.controller.init(busList, chauffeurList, tripList, tripLabel, destination, start, end,
                    excludeTripId, loaded.stage);
            loaded.stage.showAndWait();
            return loaded.controller.result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to open assign resources dialog", e);
        }
    }
}
