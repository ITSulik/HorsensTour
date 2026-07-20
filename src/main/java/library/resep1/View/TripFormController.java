package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Collections.ChauffeurList;
import library.resep1.Model.Collections.TripList;
import library.resep1.Model.Entities.Trip;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class TripFormController {

    public enum Mode { VIEW, NEW, EDIT }

    @FXML private Label titleLabel;
    @FXML private VBox bannerBox;
    @FXML private Label bannerTitle;
    @FXML private Label bannerSubtitle;

    @FXML private TextField destinationField;
    @FXML private Label destinationError;
    @FXML private TextField startField;
    @FXML private Label startError;
    @FXML private TextField endField;
    @FXML private Label endError;
    @FXML private TextArea detailsArea;

    @FXML private Label assignedBusLabel;
    @FXML private Button assignBusButton;
    @FXML private Label busConflictText;

    @FXML private Label assignedChauffeurLabel;
    @FXML private Button assignChauffeurButton;
    @FXML private Label chauffeurConflictText;

    @FXML private HBox viewFooter;
    @FXML private HBox editFooter;
    @FXML private Button saveButton;

    private BusList busList;
    private ChauffeurList chauffeurList;
    private TripList tripList;

    private Mode mode;
    private Trip trip; // null in NEW mode
    private String pendingBusNumber;     // null/blank = unassigned
    private String pendingChauffeurName; // null/blank = unassigned

    private Stage dialogStage;
    private boolean saved;

    public void init(BusList busList, ChauffeurList chauffeurList, TripList tripList,
                      Mode mode, Trip trip, Stage dialogStage) {
        this.busList = busList;
        this.chauffeurList = chauffeurList;
        this.tripList = tripList;
        this.mode = mode;
        this.trip = trip;
        this.dialogStage = dialogStage;
        this.pendingBusNumber = trip == null ? null : trip.getBusNumber();
        this.pendingChauffeurName = trip == null ? null : trip.getChauffeurName();

        clearBanner();
        clearFieldErrors();

        switch (mode) {
            case VIEW -> setupView();
            case NEW -> setupNew();
            case EDIT -> setupEdit();
        }
        refreshAssignedLabels();
    }

    private void setupView() {
        titleLabel.setText("Trip details: " + trip.getTripId());
        destinationField.setText(trip.getDestination());
        startField.setText(DateTimeUtil.format(trip.getStartTime()));
        endField.setText(DateTimeUtil.format(trip.getEndTime()));
        detailsArea.setText(trip.getAdditionalDetails());
        setFieldsEditable(false);

        viewFooter.setVisible(true);
        viewFooter.setManaged(true);
        editFooter.setVisible(false);
        editFooter.setManaged(false);
    }

    private void setupNew() {
        titleLabel.setText("New trip");
        destinationField.clear();
        startField.clear();
        endField.clear();
        detailsArea.clear();
        setFieldsEditable(true);

        viewFooter.setVisible(false);
        viewFooter.setManaged(false);
        editFooter.setVisible(true);
        editFooter.setManaged(true);
        saveButton.setText("Save trip");
    }

    private void setupEdit() {
        titleLabel.setText("Edit trip");
        destinationField.setText(trip.getDestination());
        startField.setText(DateTimeUtil.format(trip.getStartTime()));
        endField.setText(DateTimeUtil.format(trip.getEndTime()));
        detailsArea.setText(trip.getAdditionalDetails());
        setFieldsEditable(true);

        viewFooter.setVisible(false);
        viewFooter.setManaged(false);
        editFooter.setVisible(true);
        editFooter.setManaged(true);
        saveButton.setText("Save changes");
    }

    private void setFieldsEditable(boolean editable) {
        destinationField.setDisable(!editable);
        startField.setDisable(!editable);
        endField.setDisable(!editable);
        detailsArea.setDisable(!editable);
        assignBusButton.setVisible(editable);
        assignBusButton.setManaged(editable);
        assignChauffeurButton.setVisible(editable);
        assignChauffeurButton.setManaged(editable);
    }

    private void refreshAssignedLabels() {
        boolean hasBus = pendingBusNumber != null && !pendingBusNumber.isBlank();
        boolean hasChauffeur = pendingChauffeurName != null && !pendingChauffeurName.isBlank();
        assignedBusLabel.setText(hasBus ? pendingBusNumber : "Unassigned");
        assignBusButton.setText(hasBus ? "Change" : "Assign bus");
        assignedChauffeurLabel.setText(hasChauffeur ? pendingChauffeurName : "Unassigned");
        assignChauffeurButton.setText(hasChauffeur ? "Change" : "Assign chauffeur");
    }

    private void clearBanner() {
        bannerBox.setVisible(false);
        bannerBox.setManaged(false);
        bannerSubtitle.setVisible(false);
        bannerSubtitle.setManaged(false);
    }

    private void clearFieldErrors() {
        setError(destinationField, destinationError, null);
        setError(startField, startError, null);
        setError(endField, endError, null);
        busConflictText.setVisible(false);
        busConflictText.setManaged(false);
        chauffeurConflictText.setVisible(false);
        chauffeurConflictText.setManaged(false);
    }

    private void setError(Control field, Label errorLabel, String message) {
        if (message == null) {
            field.getStyleClass().remove("field-error");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        } else {
            if (!field.getStyleClass().contains("field-error")) {
                field.getStyleClass().add("field-error");
            }
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    @FXML
    private void onAssignBus() {
        openAssignDialog();
    }

    @FXML
    private void onAssignChauffeur() {
        openAssignDialog();
    }

    private void openAssignDialog() {
        Optional<LocalDateTime> start = DateTimeUtil.tryParse(startField.getText());
        Optional<LocalDateTime> end = DateTimeUtil.tryParse(endField.getText());
        if (start.isEmpty() || end.isEmpty() || !end.get().isAfter(start.get())) {
            clearBanner();
            bannerBox.getStyleClass().setAll("banner", "banner-error");
            bannerTitle.setText("Enter a valid start and end date/time before assigning a bus or chauffeur.");
            bannerBox.setVisible(true);
            bannerBox.setManaged(true);
            return;
        }

        String excludeTripId = trip == null ? null : trip.getTripId();
        String label = trip == null ? "New trip" : trip.getTripId();
        AssignResourcesController.Result result = AssignResourcesController.showDialog(
                dialogStage, busList, chauffeurList, tripList,
                label, destinationField.getText(), start.get(), end.get(), excludeTripId);

        if (result.busNumber != null) {
            pendingBusNumber = result.busNumber;
        }
        if (result.chauffeurName != null) {
            pendingChauffeurName = result.chauffeurName;
        }
        refreshAssignedLabels();
    }

    @FXML
    private void onSave() {
        clearBanner();
        clearFieldErrors();

        String destination = destinationField.getText() == null ? "" : destinationField.getText().trim();
        Optional<LocalDateTime> start = DateTimeUtil.tryParse(startField.getText());
        Optional<LocalDateTime> end = DateTimeUtil.tryParse(endField.getText());

        boolean hasFieldError = false;
        if (destination.isEmpty()) {
            setError(destinationField, destinationError, "Destination is required.");
            hasFieldError = true;
        }
        if (start.isEmpty()) {
            setError(startField, startError, "Enter a valid start date/time, e.g. 15 Jul 2026, 08:00.");
            hasFieldError = true;
        }
        if (end.isEmpty()) {
            setError(endField, endError, "Enter a valid end date/time, e.g. 15 Jul 2026, 18:00.");
            hasFieldError = true;
        } else if (start.isPresent() && !end.get().isAfter(start.get())) {
            setError(endField, endError, "End date/time must be after the start date/time.");
            hasFieldError = true;
        }

        if (hasFieldError) {
            bannerBox.getStyleClass().setAll("banner", "banner-error");
            bannerTitle.setText("Please fill in destination, start date/time, and end date/time.");
            bannerBox.setVisible(true);
            bannerBox.setManaged(true);
            return;
        }

        LocalDateTime newStart = start.get();
        LocalDateTime newEnd = end.get();
        String excludeTripId = trip == null ? null : trip.getTripId();

        boolean busConflict = hasBusConflict(pendingBusNumber, newStart, newEnd, excludeTripId);
        boolean chauffeurConflict = hasChauffeurConflict(pendingChauffeurName, newStart, newEnd, excludeTripId);

        if (busConflict || chauffeurConflict) {
            bannerBox.getStyleClass().setAll("banner", "banner-warning");
            bannerTitle.setText("Update time conflicts with assigned bus or chauffeur");
            bannerSubtitle.setText("Change the time or reassign resources.");
            bannerSubtitle.setVisible(true);
            bannerSubtitle.setManaged(true);
            bannerBox.setVisible(true);
            bannerBox.setManaged(true);

            if (busConflict) {
                busConflictText.setText(pendingBusNumber + " is already booked for this time.");
                busConflictText.setVisible(true);
                busConflictText.setManaged(true);
            }
            if (chauffeurConflict) {
                chauffeurConflictText.setText(
                        pendingChauffeurName + " is already assigned to another trip during this time.");
                chauffeurConflictText.setVisible(true);
                chauffeurConflictText.setManaged(true);
            }
            return;
        }

        try {
            if (mode == Mode.NEW) {
                String newId = generateTripId();
                Trip newTrip = new Trip(newId, destination, newStart, newEnd,
                        detailsArea.getText(),
                        pendingBusNumber == null ? "" : pendingBusNumber,
                        pendingChauffeurName == null ? "" : pendingChauffeurName);
                tripList.addTrip(newTrip);
            } else {
                // Apply the resource assignment directly on the live entity,
                // then go through TripList#editTrip so the change is
                // persisted - editTrip re-saves the whole trip, including
                // the busNumber/chauffeurName fields set just below (it
                // does not itself accept them as parameters).
                trip.setBusNumber(pendingBusNumber == null ? "" : pendingBusNumber);
                trip.setChauffeurName(pendingChauffeurName == null ? "" : pendingChauffeurName);
                tripList.editTrip(trip.getTripId(), destination, newStart, newEnd, detailsArea.getText());
            }
        } catch (IllegalArgumentException e) {
            bannerBox.getStyleClass().setAll("banner", "banner-error");
            bannerTitle.setText(e.getMessage());
            bannerBox.setVisible(true);
            bannerBox.setManaged(true);
            return;
        }

        saved = true;
        dialogStage.close();
    }

    private boolean hasBusConflict(String busNumber, LocalDateTime start, LocalDateTime end, String excludeTripId) {
        if (busNumber == null || busNumber.isBlank()) {
            return false;
        }
        for (Trip other : tripList.getAllTrips()) {
            if (excludeTripId != null && other.getTripId().equalsIgnoreCase(excludeTripId)) {
                continue;
            }
            if (other.hasBusAssigned() && other.getBusNumber().equalsIgnoreCase(busNumber)
                    && other.overlaps(start, end)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasChauffeurConflict(String chauffeurName, LocalDateTime start, LocalDateTime end,
                                          String excludeTripId) {
        if (chauffeurName == null || chauffeurName.isBlank()) {
            return false;
        }
        for (Trip other : tripList.getAllTrips()) {
            if (excludeTripId != null && other.getTripId().equalsIgnoreCase(excludeTripId)) {
                continue;
            }
            if (other.hasChauffeurAssigned() && other.getChauffeurName().equalsIgnoreCase(chauffeurName)
                    && other.overlaps(start, end)) {
                return true;
            }
        }
        return false;
    }

    private String generateTripId() {
        int max = 100;
        for (Trip existing : tripList.getAllTrips()) {
            String id = existing.getTripId();
            if (id != null && id.toUpperCase().startsWith("TRP-")) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(4)));
                } catch (NumberFormatException ignored) {
                    // non-numeric suffix - ignore for id generation purposes
                }
            }
        }
        return "TRP-" + (max + 1);
    }

    @FXML
    private void onCancel() {
        saved = false;
        dialogStage.close();
    }

    @FXML
    private void onClose() {
        saved = false;
        dialogStage.close();
    }

    @FXML
    private void onSwitchToEdit() {
        this.mode = Mode.EDIT;
        clearBanner();
        clearFieldErrors();
        setupEdit();
        refreshAssignedLabels();
    }

    @FXML
    private void onDeleteTrip() {
        boolean confirmed = ConfirmDeleteController.showDialog(dialogStage,
                "Delete trip", trip.getTripId() + " (" + trip.getDestination() + ")", "Delete trip");
        if (confirmed) {
            tripList.deleteTrip(trip.getTripId());
            saved = true;
            dialogStage.close();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public static boolean showDialog(Window owner, BusList busList, ChauffeurList chauffeurList,
                                      TripList tripList, Mode mode, Trip trip) {
        try {
            String title = switch (mode) {
                case VIEW -> "Trip details";
                case NEW -> "New trip";
                case EDIT -> "Edit trip";
            };
            DialogUtil.Loaded<TripFormController> loaded =
                    DialogUtil.load("/library/resep1/tripFormView.fxml", title, owner);
            loaded.controller.init(busList, chauffeurList, tripList, mode, trip, loaded.stage);
            loaded.stage.showAndWait();
            return loaded.controller.isSaved();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open trip dialog", e);
        }
    }
}
