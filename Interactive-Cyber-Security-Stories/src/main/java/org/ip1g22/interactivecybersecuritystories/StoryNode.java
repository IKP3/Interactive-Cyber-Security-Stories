package org.ip1g22.interactivecybersecuritystories;

import java.util.Objects;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StoryNode extends StackPane {
  private double mouseX, mouseY;
  private double width = 60;
  private double height = 80;
  private StringProperty name;
  ScrollPane scrollPane;

  TextArea labelText;
  public StringProperty textProperty;
  boolean editing = false;
  public StoryNode(String text,String knotName) {
    this(text);
    name.setValue(knotName);
    if(name.getValue().equals("main")){
      scrollPane.getStyleClass().add("mainScrollPane");
    }
  }
  public StoryNode(String text) {
    textProperty = new SimpleStringProperty();
    name = new SimpleStringProperty();
    textProperty.setValue(text);
    name.setValue("New_Node");

    labelText = new TextArea(text);
    labelText.setFont(new Font(7));
    labelText.setWrapText(true);
    labelText.setPrefWidth(width - 10);
    labelText.setEditable(false);
    labelText.setFocusTraversable(false);
    labelText.setStyle("-fx-text-alignment: center");

    scrollPane = new ScrollPane(labelText);
    scrollPane.setPrefSize(width, height);
    scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
    scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    scrollPane.setFitToWidth(true);
    scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
      mouseX = event.getSceneX() - getLayoutX();
      mouseY = event.getSceneY() - getLayoutY();
    });

    scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
      double newX = event.getSceneX() - mouseX;
      double newY = event.getSceneY() - mouseY;

      setLayoutX(newX);
      setLayoutY(newY);
    });
    scrollPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
      if (event.getClickCount() == 2) {
        editPopUp();
      }
    });
    getChildren().add(scrollPane);
  }
  public String getText(){
    return textProperty.getValue();
  }
  public StringProperty getTextProperty(){
    return textProperty;
  }
  public void setText(String text){
    textProperty.setValue(text);
    labelText.setText(text);


  }
  private void editPopUp() {
    if(editing){
      return;
    }
    editing = true;
    Stage popupStage = new Stage();

    popupStage.setTitle(name.getValue());

    BorderPane borderPane = new BorderPane();
    TextArea editText = new TextArea();
    editText.setText(textProperty.getValue());
    TextField nameField = new TextField();
    nameField.setText(name.getValue());
    borderPane.setCenter(editText);
    Button saveButton = new Button("Save");
    saveButton.setOnAction(e -> {
      editing = false;
      setText(editText.getText());
      String newName = nameField.getText();
      if(!newName.trim().equals("main")){
        name.setValue(nameField.getText());
      }

      popupStage.setTitle(name.getValue());
      popupStage.close();
    });
    popupStage.setOnCloseRequest(event -> {
      editing = false;
    });
    if(!Objects.equals(name.getValue(), "main")){
      HBox hBox = new HBox();
      hBox.getChildren().addAll(saveButton, nameField);
      borderPane.setBottom(hBox);

    }
    else{
      borderPane.setBottom(saveButton);
    }

    Scene popupScene = new Scene(borderPane, 250, 150);
    popupStage.setScene(popupScene);

    popupStage.initOwner(SceneManager.getStage());
    popupStage.show();
  }
  protected void setName(String name) {
    this.name.setValue(name);
  }
  public StringProperty getNameProperty() {
    return name;
  }
  public String getName(){
    return name.getValue();
  }
}
