package library.resep1.View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
      FXMLLoader fxmlLoader =
          new FXMLLoader(HelloApplication.class.getResource("/library/resep1/Dashboard.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        scene.getStylesheets().add(
                getClass().getResource("/library/resep1/styles.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();
    }
}
