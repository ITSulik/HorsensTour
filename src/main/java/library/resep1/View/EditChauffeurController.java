package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import library.resep1.Model.Entities.Chauffeur;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import library.resep1.ViewModel.ChauffeurViewModel;

import java.io.IOException;


public class EditChauffeurController {
  @FXML private VBox bannerBox;
  @FXML private TextField nameField;
  @FXML private Label nameError;
  @FXML private TextField experienceField;
  @FXML private Label experienceError;
  @FXML private TextField preferencesField;

  @FXML
  private Label errorLabel;

  private Chauffeur chauffeur;
  private final ChauffeurViewModel chaufVM = new ChauffeurViewModel();
  private Stage dialogStage;
  private boolean saved;

  public void init(Chauffeur chauffeur ,Stage dialogStage) {
    this.dialogStage = dialogStage;
    this.chauffeur = chauffeur;
    clearErrors();
  }

  private void clearErrors() {
    bannerBox.setVisible(false);
    bannerBox.setManaged(false);
    setError(nameField, nameError, null);
    setError(experienceField, experienceError, null);
  }

  private void setError(TextField field, Label errorLabel, String message) {
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

  public void setChauffeur(Chauffeur chauffeur) {
    this.chauffeur = chauffeur;

    nameField.setText(chauffeur.getName());
    experienceField.setText(
        String.valueOf(chauffeur.getExperience())
    );
    preferencesField.setText(chauffeur.getPreferences());
  }

  @FXML
  private void cancel(ActionEvent event) {

    saved = false;
    dialogStage.close();
  }
  @FXML
  private void saveChauffeur(ActionEvent event) {
    try {
      chaufVM.editChauffeur(
          chauffeur.getChauffeurID(),
          nameField.getText(),
          Integer.parseInt(experienceField.getText()),
          preferencesField.getText()
      );

      errorLabel.setVisible(false);
      saved = true;
      dialogStage.close();

    } catch (NumberFormatException exception) {
      errorLabel.setText("Experience must be a number.");
      errorLabel.setVisible(true);

    } catch (IllegalArgumentException exception) {
      errorLabel.setText(exception.getMessage());
      errorLabel.setVisible(true);
    }
  }

  public boolean isSaved() {
    return saved;
  }

  public static boolean showDialog(Window owner, Chauffeur chauffeur) {
    try {
      String title = "Edit chauffeur";
      DialogUtil.Loaded<EditChauffeurController> loaded =
              DialogUtil.load("/library/resep1/editChauffeurView.fxml", title, owner);
      loaded.controller.init(chauffeur ,loaded.stage);
      loaded.stage.showAndWait();
      return loaded.controller.isSaved();
    } catch (IOException e) {
      throw new RuntimeException("Failed to open chauffeur dialog", e);
    }
  }
}