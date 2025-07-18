package org.ip1g22.interactivecybersecuritystories;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    SceneManager sceneManager = new SceneManager(stage,scene);
    scene.getStylesheets().add(getClass().getResource(
        "/org/ip1g22/interactivecybersecuritystories/stylesheets/LightTheme.css").toExternalForm());
    stage.setTitle("Interactive Cyber Security Stories");
    stage.setScene(scene);
    stage.show();
  }
  public static void main(String[] args) {
    launch();
  }
}