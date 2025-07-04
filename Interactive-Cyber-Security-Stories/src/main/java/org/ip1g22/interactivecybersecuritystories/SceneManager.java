package org.ip1g22.interactivecybersecuritystories;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
  private static Stage stage;
  private static Scene scene;
  private static final Map<String, Scene> sceneCache = new HashMap<>();

  public SceneManager(Stage primaryStage,Scene primaryScene) {
    stage = primaryStage;
    scene = primaryScene;
  }
  public static Stage getStage() {
    return stage;
  }
  public static Scene getScene() {
    return scene;
  }
}
