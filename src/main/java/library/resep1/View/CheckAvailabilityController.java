package library.resep1.View;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.ViewModel.BusViewModel;
import library.resep1.ViewModel.ChauffeurViewModel;
import library.resep1.ViewModel.TripViewModel;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CheckAvailabilityController {

    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<LocalTime> startTimePicker;

    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<LocalTime> endTimePicker;

    @FXML private Label errorLabel;

    @FXML private VBox emptyStateBox;
    @FXML private Label emptyTimeSlotLabel;

    @FXML private VBox resultsBox;
    @FXML private VBox busesListBox;
    @FXML private VBox chauffeursListBox;

    private final TripViewModel tripVM = TripViewModel.getInstance();
    private final BusViewModel busVM = BusViewModel.getInstance();
    private final ChauffeurViewModel chauffeurVM = ChauffeurViewModel.getInstance();

    private Stage dialogStage;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML
    public void initialize() {
        populateTimePickers();
        configureTimeConverters();
    }

    public void init(Stage dialogStage) {
        this.dialogStage = dialogStage;

        hideError();
        hideResults();
        hideEmptyState();
    }

    private void populateTimePickers() {
        List<LocalTime> times = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                times.add(LocalTime.of(hour, minute));
            }
        }

        startTimePicker.getItems().setAll(times);
        endTimePicker.getItems().setAll(times);
    }

    private void configureTimeConverters() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm");

        StringConverter<LocalTime> converter =
                new StringConverter<>() {

                    @Override
                    public String toString(LocalTime time) {
                        return time == null
                                ? ""
                                : time.format(formatter);
                    }

                    @Override
                    public LocalTime fromString(String text) {
                        return text == null || text.isBlank()
                                ? null
                                : LocalTime.parse(text, formatter);
                    }
                };

        startTimePicker.setConverter(converter);
        endTimePicker.setConverter(converter);
    }

    @FXML
    private void onCheck() {
        hideError();
        hideResults();
        hideEmptyState();

        Optional<LocalDateTime> start =
                getDateTime(startDatePicker, startTimePicker);

        Optional<LocalDateTime> end =
                getDateTime(endDatePicker, endTimePicker);

        if (start.isEmpty() || end.isEmpty()) {
            showError(
                    "Please enter both a start and end date/time."
            );
            return;
        }

        if (!end.get().isAfter(start.get())) {
            showError(
                    "The end date/time must be after the start date/time."
            );
            return;
        }

        List<Bus> availableBuses =
                tripVM.getAvailableBuses(
                        start.get(),
                        end.get(),
                        busVM.getAllBuses()
                );

        List<Chauffeur> availableChauffeurs =
                tripVM.getAvailableChauffeurs(
                        start.get(),
                        end.get(),
                        chauffeurVM.getAllChauffeurs()
                );

        if (availableBuses.isEmpty()
                && availableChauffeurs.isEmpty()) {

            showEmptyState(start.get(), end.get());
            return;
        }

        showResults(
                availableBuses,
                availableChauffeurs
        );
    }

    private void showResults(
            List<Bus> availableBuses,
            List<Chauffeur> availableChauffeurs
    ) {
        resultsBox.setVisible(true);
        resultsBox.setManaged(true);

        populateBuses(availableBuses);
        populateChauffeurs(availableChauffeurs);
    }

    private void populateBuses(List<Bus> buses) {
        busesListBox.getChildren().clear();

        if (buses.isEmpty()) {
            busesListBox.getChildren().add(
                    helperLabel(
                            "No buses available for this time slot."
                    )
            );
            return;
        }

        for (Bus bus : buses) {
            String details =
                    Formatting.prettifyBusType(bus.getType())
                            + " · "
                            + bus.getCapacity()
                            + " seats · "
                            + bus.getPurpose();

            busesListBox.getChildren().add(
                    infoRow(
                            bus.getBusNumber(),
                            details
                    )
            );
        }
    }

    private void populateChauffeurs(
            List<Chauffeur> chauffeurs
    ) {
        chauffeursListBox.getChildren().clear();

        if (chauffeurs.isEmpty()) {
            chauffeursListBox.getChildren().add(
                    helperLabel(
                            "No chauffeurs available for this time slot."
                    )
            );
            return;
        }

        for (Chauffeur chauffeur : chauffeurs) {

            String preferences =
                    chauffeur.getPreferences() == null
                            || chauffeur.getPreferences().isBlank()
                            ? "No preferences noted"
                            : chauffeur.getPreferences();

            String details =
                    chauffeur.getExperience()
                            + " yrs experience · "
                            + preferences;

            chauffeursListBox.getChildren().add(
                    infoRow(
                            chauffeur.getName(),
                            details
                    )
            );
        }
    }

    private void showEmptyState(
            LocalDateTime start,
            LocalDateTime end
    ) {
        emptyStateBox.setVisible(true);
        emptyStateBox.setManaged(true);

        emptyTimeSlotLabel.setText(
                "Time slot: "
                        + start.format(DATE_TIME_FORMATTER)
                        + " – "
                        + end.format(DATE_TIME_FORMATTER)
        );
    }

    private Label helperLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("helper-text");
        return label;
    }

    private HBox infoRow(
            String name,
            String details
    ) {
        VBox info = new VBox(2);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("resource-name");

        Label detailsLabel = new Label(details);
        detailsLabel.getStyleClass().add("resource-details");

        info.getChildren().addAll(
                nameLabel,
                detailsLabel
        );

        HBox row = new HBox(info);
        row.getStyleClass().add("resource-row");
        row.setAlignment(Pos.CENTER_LEFT);

        return row;
    }

    private Optional<LocalDateTime> getDateTime(
            DatePicker datePicker,
            ComboBox<LocalTime> timePicker
    ) {
        if (datePicker.getValue() == null
                || timePicker.getValue() == null) {

            return Optional.empty();
        }

        return Optional.of(
                LocalDateTime.of(
                        datePicker.getValue(),
                        timePicker.getValue()
                )
        );
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void hideResults() {
        resultsBox.setVisible(false);
        resultsBox.setManaged(false);
    }

    private void hideEmptyState() {
        emptyStateBox.setVisible(false);
        emptyStateBox.setManaged(false);
    }

    @FXML
    private void onClose() {
        dialogStage.close();
    }

    public static void showDialog(Window owner) {
        try {
            DialogUtil.Loaded<CheckAvailabilityController> loaded =
                    DialogUtil.load(
                            "/library/resep1/checkAvailabilityView.fxml",
                            "Check availability",
                            owner
                    );

            loaded.controller.init(loaded.stage);

            loaded.stage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to open check availability dialog",
                    e
            );
        }
    }
}