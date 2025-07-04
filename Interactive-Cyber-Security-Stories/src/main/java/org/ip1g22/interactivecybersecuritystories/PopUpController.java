package org.ip1g22.interactivecybersecuritystories;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PopUpController {
  private static Scene scene;
  private static Stage stage;
  private static String stylesheet = "LightTheme";
  public static void scorePopUp(int maxScore,int Score) {
    stage = new Stage();
    stage.setTitle("Score");


    VBox vbox = new VBox(10);
    vbox.getStyleClass().add("pop-up-vbox");

    Text scoreText = new Text("Score: " + Score);
    scoreText.getStyleClass().add("pop-up-text-bold");

    Text maxScoreText = new Text("Max Score: " + maxScore);
    maxScoreText.getStyleClass().add("pop-up-text");

    double percentageScore = ((double) Score / maxScore) * 100;
    Text percentageScoreText = new Text(String.format("Percentage: %.2f%%", percentageScore));
    percentageScoreText.getStyleClass().add("pop-up-text");

    Button closeButton = new Button("Close");
    closeButton.getStyleClass().add("pop-up-button");
    closeButton.setOnAction(e -> stage.close());

    vbox.getChildren().addAll(scoreText, maxScoreText, percentageScoreText, closeButton);


    scene = new Scene(vbox, 250, 200);
    scene.getStylesheets().add(PopUpController.class.getResource("/org/ip1g22/interactivecybersecuritystories/stylesheets/"+stylesheet+".css").toExternalForm());
    stage.setScene(scene);

    stage.initOwner(SceneManager.getStage());
    stage.show();
  }
  public static void changeStylesheet(String newStylesheet){
    if(scene != null){
      scene.getStylesheets().clear();
    }
    stylesheet = newStylesheet;
  }
  public static void syntaxHelpPopUp(){
    Stage popupStage = new Stage();
    popupStage.setTitle("Syntax Help");

    TextArea syntaxHelpText = new TextArea();
    String documentation = "### Syntax Documentation\n\n" +

        "#### Button Definition (`+`)\n" +
        "- Syntax:\n"+
        "\n`+ {condition} [score] name\n"+
        "-> <knot>`\n\n" +
        "- Description: Adds a button. When clicked, all buttons are removed and the specified knot is displayed in their place. You can include conditions that control whether the button is visible, and award points when the button is clicked.\n" +
        "  - `{condition}`: (Optional) A condition specifying when the button should be displayed.\n" +
        "  - `[score]`: (Optional) The number of points awarded when the button is clicked.\n" +
        "  - `name`: The text shown on the button.\n" +
        "  - `-> <knot>`: (Optional) The knot that will be displayed after the button is clicked.It has to be defined in a new line\n\n" +

        "#### Knot Definition (`===name===`)\n" +
        "- Syntax: `=== <name> ===`\n" +
        "- Description: Defines a knot with the specified name, which can later be displayed using `-><name>`.\n\n" +

        "#### Displaying Knot (`-><name>`)\n" +
        "- Syntax: `-><name>`\n" +
        "- Description: Displays the content of the knot specified by `<name>`.";
    syntaxHelpText.setEditable(false);
    syntaxHelpText.setText(documentation);
    syntaxHelpText.setWrapText(true);
    syntaxHelpText.setPrefWidth(400);
    syntaxHelpText.setPrefHeight(300);

    scene = new Scene(syntaxHelpText);
    scene.getStylesheets().add(PopUpController.class.getResource("/org/ip1g22/interactivecybersecuritystories/stylesheets/"+stylesheet+".css").toExternalForm());
    scene.getRoot().layout();
    popupStage.setScene(scene);
    popupStage.initOwner(SceneManager.getStage());
    popupStage.show();
  }
  public static void aboutHelpPopUp(){
    stage = new Stage();
    stage.setTitle("About");

    TextArea syntaxHelpText = new TextArea();
    String documentation = "Interactive Cyber-Security Stories is a tool for creating branching, narrative-based scenarios aimed at cybersecurity education. It allows educators to create interactive stories that simulate real-world cyber scenarios, helping learners close the gap between their theoretical knowledge and hands-on experience.";
    syntaxHelpText.setEditable(false);
    syntaxHelpText.setText(documentation);
    syntaxHelpText.setWrapText(true);
    syntaxHelpText.setPrefWidth(400);
    syntaxHelpText.setPrefHeight(300);

    scene = new Scene(syntaxHelpText);
    scene.getStylesheets().add(PopUpController.class.getResource("/org/ip1g22/interactivecybersecuritystories/stylesheets/"+stylesheet+".css").toExternalForm());
    scene.getRoot().layout();
    stage.setScene(scene);

    stage.initOwner(SceneManager.getStage());
    stage.show();
  }
}
