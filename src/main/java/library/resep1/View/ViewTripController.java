package library.resep1.View;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import library.resep1.Model.Entities.Trip;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ViewTripController {

    @FXML private Label tripIdLabel;
    @FXML private Label destinationLabel;
    @FXML private Label startLabel;
    @FXML private Label endLabel;
    @FXML private Label detailsLabel;
    @FXML private Label busLabel;
    @FXML private Label chauffeurLabel;

    private Stage dialogStage;
    private boolean deleted;

    public void init(
            Trip trip,
            Stage dialogStage
    ) {
        this.dialogStage = dialogStage;

        tripIdLabel.setText(
                trip.getTripId()
        );

        destinationLabel.setText(
                trip.getDestination()
        );

        startLabel.setText(
                trip.getStartTime().format(
                        DateTimeFormatter.ofPattern(
                                "dd MMM yyyy, HH:mm"
                        )
                )
        );

        endLabel.setText(
                trip.getEndTime().format(
                        DateTimeFormatter.ofPattern(
                                "dd MMM yyyy, HH:mm"
                        )
                )
        );

        detailsLabel.setText(
                trip.getAdditionalDetails()
        );

        busLabel.setText(
                trip.hasBusAssigned()
                        ? trip.getBusNumber()
                        : "Unassigned"
        );

        chauffeurLabel.setText(
                trip.hasChauffeurAssigned()
                        ? trip.getChauffeurName()
                        : "Unassigned"
        );
    }

    @FXML
    private void onClose() {
        dialogStage.close();
    }

    public static void showDialog(
            Window owner,
            Trip trip
    ) {
        try {
            DialogUtil.Loaded<ViewTripController> loaded =
                    DialogUtil.load(
                            "/library/resep1/viewTripView.fxml",
                            "Trip details",
                            owner
                    );

            loaded.controller.init(
                    trip,
                    loaded.stage
            );

            loaded.stage.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to open trip dialog",
                    e
            );
        }
    }
}