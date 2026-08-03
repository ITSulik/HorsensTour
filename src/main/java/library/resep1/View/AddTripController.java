package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

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

public class AddTripController {

    @FXML private Label titleLabel;

    @FXML private VBox bannerBox;
    @FXML private Label bannerTitle;
    @FXML private Label bannerSubtitle;

    @FXML private TextField destinationField;
    @FXML private Label destinationError;

    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<LocalTime> startTimePicker;
    @FXML private Label startError;

    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<LocalTime> endTimePicker;
    @FXML private Label endError;

    @FXML private TextArea detailsArea;

    @FXML private Label assignedBusLabel;
    @FXML private Button assignBusButton;
    @FXML private Label busConflictText;

    @FXML private Label assignedChauffeurLabel;
    @FXML private Button assignChauffeurButton;
    @FXML private Label chauffeurConflictText;

    private final TripViewModel tripVM = TripViewModel.getInstance();
    private final BusViewModel busVM = BusViewModel.getInstance();
    private final ChauffeurViewModel chauffeurVM = ChauffeurViewModel.getInstance();

    private String pendingBusNumber;
    private String pendingChauffeurName;

    private Stage dialogStage;
    private boolean saved;

    @FXML
    private void initialize() {
        setupTimePickers();
    }

    public void init(Stage dialogStage) {
        this.dialogStage = dialogStage;

        clearBanner();
        clearFieldErrors();
        refreshAssignedLabels();
    }

    private void setupTimePickers() {

        List<LocalTime> times = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                times.add(LocalTime.of(hour, minute));
            }
        }

        startTimePicker.getItems().setAll(times);
        endTimePicker.getItems().setAll(times);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("HH:mm");

        StringConverter<LocalTime> converter =
                new StringConverter<>() {

                    @Override
                    public String toString(LocalTime time) {
                        return time == null ? "" : time.format(formatter);
                    }
                    @Override
                    public LocalTime fromString(String text) {
                        return LocalTime.parse(text, formatter);
                    }
                };

        startTimePicker.setConverter(converter);
        endTimePicker.setConverter(converter);
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

        Optional<LocalDateTime> start =
                getDateTime(
                        startDatePicker,
                        startTimePicker
                );

        Optional<LocalDateTime> end =
                getDateTime(
                        endDatePicker,
                        endTimePicker
                );

        if (start.isEmpty() || end.isEmpty() || !end.get().isAfter(start.get())
        ) {
            showBanner(
                    "Enter a valid start and end date/time "
                            + "before assigning resources."
            );

            return;
        }

        AssignResourcesController.Result result =
                AssignResourcesController.showDialog(
                        dialogStage,
                        busVM,
                        chauffeurVM,
                        tripVM,
                        "New trip",
                        destinationField.getText(),
                        start.get(),
                        end.get(),
                        null
                );

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

        Optional<LocalDateTime> start =
                getDateTime(
                        startDatePicker,
                        startTimePicker
                );

        Optional<LocalDateTime> end =
                getDateTime(
                        endDatePicker,
                        endTimePicker
                );

        boolean hasErrors = false;

        if (destination.isEmpty()) {

            setError(
                    destinationField,
                    destinationError,
                    "Destination is required."
            );

            hasErrors = true;
        }

        if (start.isEmpty()) {

            setError(
                    startDatePicker,
                    startError,
                    "Enter a valid start date/time."
            );

            hasErrors = true;
        }

        if (end.isEmpty()) {

            setError(
                    endDatePicker,
                    endError,
                    "Enter a valid end date/time."
            );

            hasErrors = true;

        } else if (start.isPresent() && !end.get().isAfter(start.get()))
        {

            setError(
                    endDatePicker,
                    endError,
                    "End must be after start."
            );

            hasErrors = true;
        }

        if (hasErrors) {

            showBanner("Please fill in the required fields.");

            return;
        }

        LocalDateTime newStart =
                start.get();

        LocalDateTime newEnd =
                end.get();

        if (
                tripVM.hasBusConflict(
                        pendingBusNumber,
                        newStart,
                        newEnd,
                        null
                )
        ) {

            showBanner("The selected bus is already assigned during this time.");

            return;
        }

        if (tripVM.hasChauffeurConflict(
                        pendingChauffeurName,
                        newStart,
                        newEnd,
                        null
                )
        ) {

            showBanner(
                    "The selected chauffeur is already assigned "
                            + "during this time."
            );

            return;
        }

        try {

            tripVM.addTrip(
                    destination,
                    newStart,
                    newEnd,
                    detailsArea.getText(),
                    pendingBusNumber == null
                            ? ""
                            : pendingBusNumber,
                    pendingChauffeurName == null
                            ? ""
                            : pendingChauffeurName
            );

            saved = true;
            dialogStage.close();

        } catch (IllegalArgumentException e) {

            showBanner(
                    e.getMessage()
            );
        }
    }

    private void refreshAssignedLabels() {

        boolean hasBus =
                pendingBusNumber != null
                        && !pendingBusNumber.isBlank();

        boolean hasChauffeur =
                pendingChauffeurName != null
                        && !pendingChauffeurName.isBlank();

        assignedBusLabel.setText(
                hasBus
                        ? pendingBusNumber
                        : "Unassigned"
        );

        assignedChauffeurLabel.setText(
                hasChauffeur
                        ? pendingChauffeurName
                        : "Unassigned"
        );

        assignBusButton.setText(
                hasBus
                        ? "Change"
                        : "Assign bus"
        );

        assignChauffeurButton.setText(
                hasChauffeur
                        ? "Change"
                        : "Assign chauffeur"
        );
    }

    private Optional<LocalDateTime> getDateTime(
            DatePicker datePicker,
            ComboBox<LocalTime> timePicker
    ) {

        if (
                datePicker.getValue() == null
                        || timePicker.getValue() == null
        ) {
            return Optional.empty();
        }

        return Optional.of(
                LocalDateTime.of(
                        datePicker.getValue(),
                        timePicker.getValue()
                )
        );
    }

    private void clearBanner() {

        bannerBox.setVisible(false);
        bannerBox.setManaged(false);

        bannerSubtitle.setVisible(false);
        bannerSubtitle.setManaged(false);
    }

    private void showBanner(String message) {

        bannerTitle.setText(message);

        bannerBox.getStyleClass().setAll(
                "banner",
                "banner-error"
        );

        bannerBox.setVisible(true);
        bannerBox.setManaged(true);
    }

    private void clearFieldErrors() {

        setError(
                destinationField,
                destinationError,
                null
        );

        setError(
                startDatePicker,
                startError,
                null
        );

        setError(
                endDatePicker,
                endError,
                null
        );

        busConflictText.setVisible(false);
        busConflictText.setManaged(false);

        chauffeurConflictText.setVisible(false);
        chauffeurConflictText.setManaged(false);
    }

    private void setError(
            Control field,
            Label errorLabel,
            String message
    ) {

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
    private void onCancel() {

        saved = false;
        dialogStage.close();
    }

    public boolean isSaved() {
        return saved;
    }

    public static boolean showDialog(Window owner) {

        try {

            DialogUtil.Loaded<AddTripController> loaded =
                    DialogUtil.load(
                            "/library/resep1/addTripView.fxml",
                            "New trip",
                            owner
                    );

            loaded.controller.init(
                    loaded.stage
            );

            loaded.stage.showAndWait();

            return loaded.controller.isSaved();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to open trip dialog",
                    e
            );
        }
    }
}