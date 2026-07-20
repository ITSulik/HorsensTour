package library.resep1.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Enums.BusType;

public class EditBusController {

  @FXML
  private TextField busNumberField;

  @FXML
  private ComboBox<BusType> busTypeBox;

  @FXML
  private TextField capacityField;

  @FXML
  private TextField purposeField;

  @FXML
  private Label errorLabel;

  private Bus bus;

  private final BusList busList =
      new BusList();

  @FXML
  public void initialize() {
    busTypeBox.getItems().addAll(
        BusType.values()
    );
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
    closeWindow(event);
  }

  @FXML
  private void saveBus(ActionEvent event) {
    if (bus == null) {
      showError("No bus was selected.");
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

      errorLabel.setVisible(false);
      closeWindow(event);

    } catch (NumberFormatException exception) {
      showError("Capacity must be a number.");

    } catch (IllegalArgumentException exception) {
      showError(exception.getMessage());
    }
  }

  private void showError(String message) {
    errorLabel.setText(message);
    errorLabel.setVisible(true);
  }

  private void closeWindow(ActionEvent event) {
    Stage stage = (Stage) ((Node) event.getSource())
        .getScene()
        .getWindow();

    stage.close();
  }
}
