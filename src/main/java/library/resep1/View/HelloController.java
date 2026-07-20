package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import library.resep1.Model.Collections.BusList;
import library.resep1.Model.Collections.ChauffeurList;
import library.resep1.Model.Entities.Bus;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.Model.Enums.BusType;

public class HelloController {

  private final BusList busList = new BusList();
  private final ChauffeurList chauffeurList = new ChauffeurList();

  @FXML
  private TextField busNumberField;
  @FXML
  private ComboBox<BusType> busTypeBox;
  @FXML
  private TextField capacityField;
  @FXML
  private TextField purposeField;
  @FXML
  private ListView<String> busListView;
  @FXML
  private TextField chauffeurNameField;
  @FXML
  private TextField experienceField;

  @FXML
  private TextField preferencesField;
  @FXML
  private ListView<String> chauffeurListView;

  @FXML
  public void initialize() {
    busTypeBox.getItems().addAll(BusType.values());
    showBuses();
    showChauffeurs();}

  @FXML
  private void addBus() {
    Bus bus = new Bus(
        busNumberField.getText(),
        busTypeBox.getValue(),
        Integer.parseInt(capacityField.getText()),
        purposeField.getText()
    );

    busList.addBus(bus);
    showBuses();
  }

  @FXML
  private void addChauffeur() {
    Chauffeur chauffeur = new Chauffeur(
        chauffeurNameField.getText(),
        Integer.parseInt(experienceField.getText()),
        preferencesField.getText()
    );

    chauffeurList.addChauffeur(chauffeur);
    showChauffeurs();
  }

  private void showBuses() {
    busListView.getItems().clear();

    for (Bus bus : busList.getAllBuses()) {
      busListView.getItems().add(bus.getBusNumber());}
  }

  private void showChauffeurs() {
    chauffeurListView.getItems().clear();

    for (Chauffeur chauffeur : chauffeurList.getAllChauffeurs()) {
      chauffeurListView.getItems().add(chauffeur.getName());}
  }
  @FXML
  private void editBus() {
    String selectedBusNumber =
        busListView.getSelectionModel().getSelectedItem();

    if (selectedBusNumber == null) {
      return;
    }

    busList.editBus(
        selectedBusNumber,
        busNumberField.getText(),
        busTypeBox.getValue(),
        Integer.parseInt(capacityField.getText()),
        purposeField.getText()
    );

    showBuses();
  }

  @FXML
  private void deleteBus() {

    String selectedBusNumber =
        busListView.getSelectionModel().getSelectedItem();

    if (selectedBusNumber == null) {
      return;
    }

    busList.deleteBus(selectedBusNumber);

    showBuses();
  }

  @FXML
  private void editChauffeur() {
    String selectedChauffeurName =
        chauffeurListView.getSelectionModel().getSelectedItem();

    if (selectedChauffeurName == null) {
      return;
    }

    chauffeurList.editChauffeur(
        selectedChauffeurName,
        chauffeurNameField.getText(),
        Integer.parseInt(experienceField.getText()),
        preferencesField.getText()
    );

    showChauffeurs();
  }

  @FXML
  private void deleteChauffeur() {
    String selectedChauffeurName =
        chauffeurListView.getSelectionModel().getSelectedItem();

    if (selectedChauffeurName == null) {
      return;
    }

    chauffeurList.deleteChauffeur(selectedChauffeurName);

    showChauffeurs();
  }
}