package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class CannotDeleteController {

    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private Label detailLabel;

    private Stage dialogStage;

    public void init(String itemType, String itemName, int upcomingTrips, Stage dialogStage) {
        this.dialogStage = dialogStage;
        titleLabel.setText("Cannot delete " + itemType);
        String tripsWord = upcomingTrips == 1 ? "upcoming trip" : "upcoming trips";
        messageLabel.setText(itemName + " is assigned to " + upcomingTrips + " " + tripsWord + ".");
        detailLabel.setText("Reassign or delete " + (upcomingTrips == 1 ? "that trip" : "those trips")
                + " before removing this " + itemType + ".");
    }

    @FXML
    private void onClose() {
        dialogStage.close();
    }

    public static void showDialog(Window owner, String itemType, String itemName, int upcomingTrips) {
        try {
            DialogUtil.Loaded<CannotDeleteController> loaded =
                    DialogUtil.load("/library/resep1/cannotDeleteView.fxml", "Cannot delete " + itemType, owner);
            loaded.controller.init(itemType, itemName, upcomingTrips, loaded.stage);
            loaded.stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open cannot delete dialog", e);
        }
    }
}
