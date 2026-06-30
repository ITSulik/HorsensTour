module library.resep1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens library.resep1 to javafx.fxml;
    exports library.resep1;
}