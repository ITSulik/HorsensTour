module library.resep1 {
  requires javafx.controls;
  requires javafx.fxml;
  requires org.kordamp.bootstrapfx.core;
  requires com.google.gson;

  opens library.resep1.View to javafx.fxml;
  opens library.resep1.Model.Entities to com.google.gson;
  exports library.resep1;
  exports library.resep1.View to javafx.graphics;
}