package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class ConfirmDeleteController {

    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private Button confirmButton;

    private Stage dialogStage;
    private boolean confirmed;

    public void init(String title, String itemDescription, String confirmButtonText, Stage dialogStage) {
        this.dialogStage = dialogStage;
        titleLabel.setText(title);
        messageLabel.setText("Are you sure you want to delete " + itemDescription + "? This cannot be undone.");
        confirmButton.setText(confirmButtonText);
    }

    @FXML
    private void onConfirm() {
        confirmed = true;
        dialogStage.close();
    }

    @FXML
    private void onCancel() {
        confirmed = false;
        dialogStage.close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public static boolean showDialog(Window owner, String title, String itemDescription, String confirmButtonText) {
        try {
            DialogUtil.Loaded<ConfirmDeleteController> loaded =
                    DialogUtil.load("/library/resep1/confirmDeleteView.fxml", title, owner);
            loaded.controller.init(title, itemDescription, confirmButtonText, loaded.stage);
            loaded.stage.showAndWait();
            return loaded.controller.isConfirmed();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open confirm delete dialog", e);
        }
    }
}
