package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import library.resep1.Model.Collections.ChauffeurList;
import library.resep1.Model.Entities.Chauffeur;

import java.io.IOException;

public class AddChauffeurController {

    @FXML private Label titleLabel;
    @FXML private VBox bannerBox;
    @FXML private Label bannerLabel;
    @FXML private TextField nameField;
    @FXML private Label nameError;
    @FXML private TextField experienceField;
    @FXML private Label experienceError;
    @FXML private TextField preferencesField;

    private ChauffeurList chauffeurList;
    private Stage dialogStage;
    private String editingName; // null while creating a new chauffeur
    private boolean saved;

    public void init(ChauffeurList chauffeurList, Chauffeur chauffeurToEdit, Stage dialogStage) {
        this.chauffeurList = chauffeurList;
        this.dialogStage = dialogStage;
        clearErrors();

        if (chauffeurToEdit == null) {
            editingName = null;
            titleLabel.setText("New chauffeur");
            nameField.clear();
            experienceField.clear();
            preferencesField.clear();
        } else {
            editingName = chauffeurToEdit.getName();
            titleLabel.setText("Edit chauffeur: " + chauffeurToEdit.getName());
            nameField.setText(chauffeurToEdit.getName());
            experienceField.setText(String.valueOf(chauffeurToEdit.getExperience()));
            preferencesField.setText(chauffeurToEdit.getPreferences());
        }
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

    @FXML
    private void onSave() {
        clearErrors();

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String experienceText = experienceField.getText() == null ? "" : experienceField.getText().trim();
        String preferences = preferencesField.getText() == null ? "" : preferencesField.getText().trim();

        boolean hasFieldError = false;

        if (name.isEmpty()) {
            setError(nameField, nameError, "Name is required.");
            hasFieldError = true;
        }

        Integer experience = null;
        try {
            experience = Integer.parseInt(experienceText);
        } catch (NumberFormatException ignored) {
            // handled by the null check below
        }
        if (experience == null || experience < 0) {
            setError(experienceField, experienceError, "Years of experience must be zero or a positive number.");
            hasFieldError = true;
        }

        if (hasFieldError) {
            showBanner("Please fix the highlighted fields.");
            return;
        }

        try {
            if (editingName == null) {
                chauffeurList.addChauffeur(new Chauffeur(name, experience, preferences));
            } else {
                boolean updated = chauffeurList.editChauffeur(editingName, name, experience, preferences);
                if (!updated) {
                    showBanner("Could not find " + editingName + " to update.");
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

    /** Opens the New/Edit chauffeur dialog. Pass {@code null} as chauffeurToEdit to create a new one. */
    public static boolean showDialog(Window owner, ChauffeurList chauffeurList, Chauffeur chauffeurToEdit) {
        try {
            String title = chauffeurToEdit == null ? "New chauffeur" : "Edit chauffeur";
            DialogUtil.Loaded<AddChauffeurController> loaded =
                    DialogUtil.load("/library/resep1/addChauffeurView.fxml", title, owner);
            loaded.controller.init(chauffeurList, chauffeurToEdit, loaded.stage);
            loaded.stage.showAndWait();
            return loaded.controller.isSaved();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open chauffeur dialog", e);
        }
    }
}
