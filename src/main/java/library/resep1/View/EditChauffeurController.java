package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.Model.Collections.ChauffeurList;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.stage.Stage;


public class EditChauffeurController {

  @FXML
  private TextField nameField;

  @FXML
  private TextField experienceField;

  @FXML
  private TextField preferencesField;

  @FXML
  private Label errorLabel;

  private Chauffeur chauffeur;
  private final ChauffeurList chauffeurList = new ChauffeurList();

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
    Stage stage = (Stage) ((Node) event.getSource())
        .getScene()
        .getWindow();

    stage.close();
  }
  @FXML
  private void saveChauffeur(ActionEvent event) {
    try {
      chauffeurList.editChauffeur(
          chauffeur.getName(),
          nameField.getText(),
          Integer.parseInt(experienceField.getText()),
          preferencesField.getText()
      );

      errorLabel.setVisible(false);

      Stage stage = (Stage) ((Node) event.getSource())
          .getScene()
          .getWindow();

      stage.close();

    } catch (NumberFormatException exception) {
      errorLabel.setText("Experience must be a number.");
      errorLabel.setVisible(true);

    } catch (IllegalArgumentException exception) {
      errorLabel.setText(exception.getMessage());
      errorLabel.setVisible(true);
    }
  }
}