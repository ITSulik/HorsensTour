package library.resep1.View;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Collections.ChauffeurList;
import library.resep1.Model.Collections.TripList;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Entities.Chauffeur;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CheckAvailabilityController {

    @FXML private TextField startField;
    @FXML private TextField endField;
    @FXML private Label errorLabel;
    @FXML private VBox emptyStateBox;
    @FXML private Label emptyTimeSlotLabel;
    @FXML private VBox resultsBox;
    @FXML private VBox busesListBox;
    @FXML private VBox chauffeursListBox;

    private BusList busList;
    private ChauffeurList chauffeurList;
    private TripList tripList;
    private Stage dialogStage;

    public void init(BusList busList, ChauffeurList chauffeurList, TripList tripList, Stage dialogStage) {
        this.busList = busList;
        this.chauffeurList = chauffeurList;
        this.tripList = tripList;
        this.dialogStage = dialogStage;
        resultsBox.setVisible(false);
        resultsBox.setManaged(false);
        emptyStateBox.setVisible(false);
        emptyStateBox.setManaged(false);
    }

    @FXML
    private void onCheck() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Optional<LocalDateTime> start = DateTimeUtil.tryParse(startField.getText());
        Optional<LocalDateTime> end = DateTimeUtil.tryParse(endField.getText());

        if (start.isEmpty() || end.isEmpty() || !end.get().isAfter(start.get())) {
            errorLabel.setText("Enter a valid start and end date/time (end after start), e.g. 15 Jul 2026, 08:00.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            resultsBox.setVisible(false);
            resultsBox.setManaged(false);
            emptyStateBox.setVisible(false);
            emptyStateBox.setManaged(false);
            return;
        }

        List<Bus> availableBuses = tripList.getAvailableBuses(start.get(), end.get(), busList.getAllBuses());
        List<Chauffeur> availableChauffeurs =
                tripList.getAvailableChauffeurs(start.get(), end.get(), chauffeurList.getAllChauffeurs());

        if (availableBuses.isEmpty() && availableChauffeurs.isEmpty()) {
            resultsBox.setVisible(false);
            resultsBox.setManaged(false);
            emptyStateBox.setVisible(true);
            emptyStateBox.setManaged(true);
            emptyTimeSlotLabel.setText(
                    "Time slot: " + DateTimeUtil.format(start.get()) + " \u2013 " + DateTimeUtil.format(end.get()));
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
                busesListBox.getChildren().add(infoRow(bus.getBusNumber(),
                        Formatting.prettifyBusType(bus.getType()) + " \u00b7 " + bus.getCapacity()
                                + " seats \u00b7 " + bus.getPurpose()));
            }
        }

        chauffeursListBox.getChildren().clear();
        if (availableChauffeurs.isEmpty()) {
            chauffeursListBox.getChildren().add(helperLabel("No chauffeurs available for this time slot."));
        } else {
            for (Chauffeur chauffeur : availableChauffeurs) {
                String preferences = (chauffeur.getPreferences() == null || chauffeur.getPreferences().isBlank())
                        ? "No preferences noted" : chauffeur.getPreferences();
                chauffeursListBox.getChildren().add(
                        infoRow(chauffeur.getName(), chauffeur.getExperience() + " yrs experience \u00b7 " + preferences));
            }
        }
    }

    private Label helperLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("helper-text");
        return label;
    }

    private HBox infoRow(String name, String details) {
        VBox info = new VBox(2);
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("resource-name");
        Label detailsLabel = new Label(details);
        detailsLabel.getStyleClass().add("resource-details");
        info.getChildren().addAll(nameLabel, detailsLabel);
        HBox row = new HBox(info);
        row.getStyleClass().add("resource-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    @FXML
    private void onClose() {
        dialogStage.close();
    }

    public static void showDialog(Window owner, BusList busList, ChauffeurList chauffeurList, TripList tripList) {
        try {
            DialogUtil.Loaded<CheckAvailabilityController> loaded =
                    DialogUtil.load("/library/resep1/checkAvailabilityView.fxml", "Check availability", owner);
            loaded.controller.init(busList, chauffeurList, tripList, loaded.stage);
            loaded.stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open check availability dialog", e);
        }
    }
}
