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


final class DialogUtil {

    private DialogUtil() {
    }

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
