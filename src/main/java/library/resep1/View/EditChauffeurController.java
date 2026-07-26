package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import library.resep1.Model.Entities.Chauffeur;
import library.resep1.ViewModel.ChauffeurViewModel;

import java.io.IOException;

public class EditChauffeurController {

  @FXML private Label titleLabel;

  @FXML private VBox bannerBox;
  @FXML private Label bannerLabel;

  @FXML private TextField nameField;
  @FXML private Label nameError;

  @FXML private TextField experienceField;
  @FXML private Label experienceError;

  @FXML private TextField preferencesField;

  private final ChauffeurViewModel chVM = ChauffeurViewModel.getInstance();

  private Chauffeur chauffeur;
  private Stage dialogStage;
  private boolean saved;

  public void init(
          Chauffeur chauffeur,
          Stage dialogStage
  ) {
    this.chauffeur = chauffeur;
    this.dialogStage = dialogStage;

    setChauffeur(chauffeur);
    clearErrors();
  }

  private void setChauffeur(Chauffeur chauffeur) {
    nameField.setText(
            chauffeur.getName()
    );

    experienceField.setText(
            String.valueOf(
                    chauffeur.getExperience()
            )
    );

    preferencesField.setText(
            chauffeur.getPreferences() == null
                    ? ""
                    : chauffeur.getPreferences()
    );
  }

  private void clearErrors() {
    bannerBox.setVisible(false);
    bannerBox.setManaged(false);

    setError(
            nameField,
            nameError,
            null
    );

    setError(
            experienceField,
            experienceError,
            null
    );
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
  private void onSave() {
    clearErrors();

    String name =
            nameField.getText() == null
                    ? ""
                    : nameField.getText().trim();

    String experienceText =
            experienceField.getText() == null
                    ? ""
                    : experienceField.getText().trim();

    String preferences =
            preferencesField.getText() == null
                    ? ""
                    : preferencesField.getText().trim();

    boolean hasFieldError = false;

    if (name.isEmpty()) {
      setError(
              nameField,
              nameError,
              "Name is required."
      );

      hasFieldError = true;
    }

    Integer experience = null;

    try {
      experience = Integer.parseInt(
              experienceText
      );

    } catch (NumberFormatException ignored) {
      // Handled by the null check below
    }

    if (experience == null || experience < 0) {
      setError(
              experienceField,
              experienceError,
              "Years of experience must be zero or a positive number."
      );

      hasFieldError = true;
    }

    if (hasFieldError) {
      showBanner(
              "Please fix the highlighted fields."
      );

      return;
    }

    try {
      chVM.editChauffeur(
              chauffeur.getChauffeurID(),
              name,
              experience,
              preferences
      );

      saved = true;
      dialogStage.close();

    } catch (IllegalArgumentException e) {
      showBanner(
              e.getMessage()
      );
    }
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

  public static boolean showDialog(
          Window owner,
          Chauffeur chauffeur
  ) {
    try {
      String title = "Edit chauffeur";

      DialogUtil.Loaded<EditChauffeurController> loaded =
              DialogUtil.load(
                      "/library/resep1/editChauffeurView.fxml",
                      title,
                      owner
              );

      loaded.controller.init(
              chauffeur,
              loaded.stage
      );

      loaded.stage.showAndWait();

      return loaded.controller.isSaved();

    } catch (IOException e) {
      throw new RuntimeException(
              "Failed to open chauffeur dialog",
              e
      );
    }
  }
}