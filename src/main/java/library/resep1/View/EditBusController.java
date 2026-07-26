package library.resep1.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Enums.BusType;

import java.io.IOException;

public class EditBusController {

  @FXML private TextField busNumberField;
  @FXML private ComboBox<BusType> busTypeBox;
  @FXML private TextField capacityField;
  @FXML private TextField purposeField;
  @FXML private Label titleLabel;
  @FXML private VBox bannerBox;
  @FXML private Label bannerLabel;
  @FXML private TextField numberField;
  @FXML private Label numberError;
  @FXML private ComboBox<BusType> typeCombo;
  @FXML private Label capacityError;

  private Bus bus;

  private final BusList busList =
      new BusList();
  private Stage dialogStage;
  private boolean saved;

  @FXML
  public void initialize() {
    busTypeBox.getItems().addAll(
        BusType.values()
    );
  }

  public void init(Bus bus, Stage dialogStage) {
    this.dialogStage = dialogStage;
    this.bus = bus;
    clearErrors();
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

  public void setBus(Bus bus) {
    this.bus = bus;

    busNumberField.setText(
        bus.getBusNumber()
    );

    busTypeBox.setValue(
        bus.getType()
    );

    capacityField.setText(
        String.valueOf(bus.getCapacity())
    );

    purposeField.setText(
        bus.getPurpose()
    );
  }

  @FXML
  private void cancel(ActionEvent event) {

    saved = false;
    dialogStage.close();
  }

  @FXML
  private void saveBus(ActionEvent event) {
    clearErrors();

    String number = numberField.getText() == null ? "" : numberField.getText().trim();
    BusType type = typeCombo.getValue();
    String capacityText = capacityField.getText() == null ? "" : capacityField.getText().trim();
    String purpose = purposeField.getText() == null ? "" : purposeField.getText().trim();

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
      boolean edited = busList.editBus(
          bus.getBusNumber(),
          busNumberField.getText(),
          busTypeBox.getValue(),
          Integer.parseInt(capacityField.getText()),
          purposeField.getText()
      );

      if (!edited) {
        showError("The bus could not be found.");
        return;
      }
      clearErrors();
      saved = true;
      dialogStage.close();

    } catch (NumberFormatException exception) {
      showError("Capacity must be a number.");

    } catch (IllegalArgumentException e) {
      showBanner(e.getMessage());
      return;
    }
  }

  private void showBanner(String message) {
    bannerLabel.setText(message);
    bannerBox.setVisible(true);
    bannerBox.setManaged(true);
  }

  public boolean isSaved() {
    return saved;
  }

  public static boolean showDialog(Window owner, Bus bus) {
    try {
      String title = "Edit bus" ;
      DialogUtil.Loaded<EditBusController> loaded =
              DialogUtil.load("/library/resep1/editBusView.fxml", title, owner);
      loaded.controller.init(bus, loaded.stage);
      loaded.stage.showAndWait();
      return loaded.controller.isSaved();
    } catch (IOException e) {
      throw new RuntimeException("Failed to open bus dialog", e);
    }
  }

}
