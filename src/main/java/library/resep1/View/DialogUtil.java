package library.resep1.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;

/**
 * Shared helper for opening the modal popups (trip form, bus/chauffeur
 * forms, availability + assignment dialogs, delete confirmations) used
 * throughout the dashboard. Centralizes the FXMLLoader/Stage boilerplate
 * so each dialog controller only has to load its own FXML and hand back
 * its controller instance.
 */
final class DialogUtil {

    private DialogUtil() {
    }

    /**
     * Loads {@code fxmlPath}, wraps it in a modal utility Stage owned by
     * {@code owner}, and returns the controller together with the (not yet
     * shown) stage so the caller can finish wiring the controller before
     * calling {@code stage.showAndWait()}.
     */
    static <T> Loaded<T> load(String fxmlPath, String title, Window owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(DialogUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();

        Stage stage = new Stage();
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setTitle(title);

        Scene scene = new Scene(root);
        URL css = DialogUtil.class.getResource("/library/resep1/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.setOnShown(e -> stage.centerOnScreen());

        return new Loaded<>(controller, stage);
    }

    static final class Loaded<T> {
        final T controller;
        final Stage stage;

        Loaded(T controller, Stage stage) {
            this.controller = controller;
            this.stage = stage;
        }
    }
}
