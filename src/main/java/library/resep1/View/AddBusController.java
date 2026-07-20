package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Enums.BusType;

import java.io.IOException;

public class AddBusController {

    @FXML private Label titleLabel;
    @FXML private VBox bannerBox;
    @FXML private Label bannerLabel;
    @FXML private TextField numberField;
    @FXML private Label numberError;
    @FXML private ComboBox<BusType> typeCombo;
    @FXML private TextField capacityField;
    @FXML private Label capacityError;
    @FXML private ComboBox<String> purposeCombo;

    private BusList busList;
    private Stage dialogStage;
    private String editingBusNumber; // null while creating a new bus
    private boolean saved;

    @FXML
    public void initialize() {
        typeCombo.getItems().setAll(BusType.values());
        typeCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(BusType type) {
                return type == null ? "" : Formatting.prettifyBusType(type);
            }

            @Override
            public BusType fromString(String text) {
                return typeCombo.getValue();
            }
        });

        purposeCombo.setEditable(true);
        purposeCombo.getItems().setAll("Tourism", "Long-distance", "Private charter");
    }

    public void init(BusList busList, Bus busToEdit, Stage dialogStage) {
        this.busList = busList;
        this.dialogStage = dialogStage;
        clearErrors();

        if (busToEdit == null) {
            editingBusNumber = null;
            titleLabel.setText("New bus");
            numberField.clear();
            typeCombo.setValue(BusType.TOURIST_BUS);
            capacityField.clear();
            purposeCombo.setValue(null);
            purposeCombo.getEditor().clear();
        } else {
            editingBusNumber = busToEdit.getBusNumber();
            titleLabel.setText("Edit bus: " + busToEdit.getBusNumber());
            numberField.setText(busToEdit.getBusNumber());
            typeCombo.setValue(busToEdit.getType());
            capacityField.setText(String.valueOf(busToEdit.getCapacity()));
            purposeCombo.setValue(busToEdit.getPurpose());
            purposeCombo.getEditor().setText(busToEdit.getPurpose());
        }
    }

    private void clearErrors() {
        bannerBox.setVisible(false);
        bannerBox.setManaged(false);
        setError(numberField, numberError, null);
        setError(capacityField, capacityError, null);
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
    private void onSave() {
        clearErrors();

        String number = numberField.getText() == null ? "" : numberField.getText().trim();
        BusType type = typeCombo.getValue();
        String capacityText = capacityField.getText() == null ? "" : capacityField.getText().trim();
        String purpose = purposeCombo.getEditor().getText() == null ? "" : purposeCombo.getEditor().getText().trim();

        boolean hasFieldError = false;

        if (number.isEmpty()) {
            setError(numberField, numberError, "Bus number is required.");
            hasFieldError = true;
        }

        Integer capacity = null;
        try {
            capacity = Integer.parseInt(capacityText);
        } catch (NumberFormatException ignored) {
            // handled by the null check below
        }
        if (capacity == null || capacity <= 0) {
            setError(capacityField, capacityError, "Capacity must be a positive number.");
            hasFieldError = true;
        }

        if (hasFieldError) {
            showBanner("Please fix the highlighted fields.");
            return;
        }

        try {
            if (editingBusNumber == null) {
                busList.addBus(new Bus(number, type, capacity, purpose));
            } else {
                boolean updated = busList.editBus(editingBusNumber, number, type, capacity, purpose);
                if (!updated) {
                    showBanner("Could not find " + editingBusNumber + " to update.");
                    return;
                }
            }
        } catch (IllegalArgumentException e) {
            showBanner(e.getMessage());
            return;
        }

        saved = true;
        dialogStage.close();
    }

    private void showBanner(String message) {
        bannerLabel.setText(message);
        bannerBox.setVisible(true);
        bannerBox.setManaged(true);
    }

    @FXML
    private void onCancel() {
        saved = false;
        dialogStage.close();
    }

    public boolean isSaved() {
        return saved;
    }

    /** Opens the New/Edit bus dialog. Pass {@code null} as busToEdit to create a new bus. */
    public static boolean showDialog(Window owner, BusList busList, Bus busToEdit) {
        try {
            String title = busToEdit == null ? "New bus" : "Edit bus";
            DialogUtil.Loaded<AddBusController> loaded =
                    DialogUtil.load("/library/resep1/addBusView.fxml", title, owner);
            loaded.controller.init(busList, busToEdit, loaded.stage);
            loaded.stage.showAndWait();
            return loaded.controller.isSaved();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open bus dialog", e);
        }
    }
}
