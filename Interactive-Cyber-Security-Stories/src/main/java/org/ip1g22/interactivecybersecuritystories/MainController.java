package org.ip1g22.interactivecybersecuritystories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainController {
  DiagramController diagramController;
  @FXML
  private VBox processedOutput;
  @FXML
  private TextArea inputTextArea;
  @FXML
  private SplitPane TextAreas;
  @FXML
  private MenuItem fileMenuExportLatex;
  @FXML
  private MenuItem fileMenuExportPdf;
  @FXML
  private MenuItem fileMenuExportPdfPrintable;
  @FXML
  private MenuItem fileMenuExportTxt;
  @FXML
  private MenuItem fileMenuOpenFile;
  @FXML
  private MenuItem helpSyntaxButton;
  @FXML
  private MenuItem helpAboutButton;
  @FXML
  private MenuItem preferencesLightThemeButton;
  @FXML
  private MenuItem preferencesDarkThemeButton;
  @FXML
  private MenuItem preferencesHighContrastThemeButton;
  @FXML
  private BorderPane mainBorderPane;
  @FXML
  private Button compileButton;
  @FXML
  private MenuItem theStrangeEmail;
  @FXML
  private MenuItem suspiciousNetwork;

  private int viewId = 0;
  protected ObservableList<Node> uiElements;
  protected Map<String, String> knots;
  protected Map<String,Integer> knotsInstances;

  private int Score;
  private boolean displayScore;
  protected int maxScore = 0;
  boolean inKnot = false;

  String mainKnot;
  private boolean buttonPressed;
  @FXML
  public void initialize() {
    ExportController exportController = new ExportController();
    diagramController = new DiagramController();
    uiElements = FXCollections.observableArrayList();
    knots = new LinkedHashMap<>();
    knotsInstances = new HashMap<>();

    uiElements.addListener((ListChangeListener<Node>) change -> {
      while (change.next()) {
        addUiElements();
      }
    });
    //File Menu Bar Tab
    fileMenuExportLatex.setOnAction(event -> {
      switchToMainView();
      exportController.exportStoryToLatex(inputTextArea.getText());});
    fileMenuExportPdf.setOnAction(event -> {
      switchToMainView();
      exportController.exportStoryToPdf(inputTextArea.getText(),true);});
    fileMenuExportPdfPrintable.setOnAction(event -> {
      switchToMainView();
      exportController.exportStoryToPdf(inputTextArea.getText(),false);});
    fileMenuExportTxt.setOnAction(event -> {
      switchToMainView();
      exportController.exportStoryToTxt(inputTextArea.getText());});
    fileMenuOpenFile.setOnAction(event -> {
      switchToMainView();
      String fileContent = exportController.readTxtFile();
      if(fileContent != null) {
        inputTextArea.setText(fileContent);
      }
    ;});
    //Preferences Menu Bar Tab
    preferencesLightThemeButton.setOnAction(event -> {
      SceneManager.getScene().getStylesheets().clear();
      SceneManager.getScene().getStylesheets().add(getClass().getResource("/org/ip1g22/interactivecybersecuritystories/stylesheets/LightTheme.css").toExternalForm());
      PopUpController.changeStylesheet("LightTheme");
    });
    preferencesDarkThemeButton.setOnAction(event -> {
      SceneManager.getScene().getStylesheets().clear();
      SceneManager.getScene().getStylesheets().add(getClass().getResource("/org/ip1g22/interactivecybersecuritystories/stylesheets/DarkTheme.css").toExternalForm());
      PopUpController.changeStylesheet("DarkTheme");
    });
    preferencesHighContrastThemeButton.setOnAction(event -> {
      SceneManager.getScene().getStylesheets().clear();
      SceneManager.getScene().getStylesheets().add(getClass().getResource("/org/ip1g22/interactivecybersecuritystories/stylesheets/HighContrastTheme.css").toExternalForm());
      PopUpController.changeStylesheet("HighContrastTheme");
    });
    //Help Menu Bar Tab
    helpSyntaxButton.setOnAction(event -> PopUpController.syntaxHelpPopUp());
    helpAboutButton.setOnAction(event -> PopUpController.aboutHelpPopUp());
    //Story Library
    theStrangeEmail.setOnAction(event -> {
      switchToMainView();
      String fileContent = exportController.readTxtFile("src/main/resources/org/ip1g22/interactivecybersecuritystories/Stories/phishingStory.txt");
      if(fileContent != null) {
        inputTextArea.setText(fileContent);
      }
    });
    suspiciousNetwork.setOnAction(event -> {
      switchToMainView();
      String fileContent = exportController.readTxtFile("src/main/resources/org/ip1g22/interactivecybersecuritystories/Stories/suspiciousNetwork.txt");
      if(fileContent != null) {
        inputTextArea.setText(fileContent);
      }
    });
    buttonPressed = false;
    displayScore = false;
  }
  @FXML
  protected void onCompileButtonClick() {
    buttonPressed = false;
    displayScore = false;
    Score = 0;
    processText(inputTextArea.getText(),true);
    addUiElements();
  }

  private void processText(String textArea,Boolean clear) {
    if (clear) {
      uiElements.clear();
      knots.clear();
      knotsInstances.clear();
      Score = 0;
      maxScore = 0;
    }
    String[] tokens = loadKnots(textArea);
    String text = "";
    if(clear){
      mainKnot = String.join(" ", tokens);
    }

    for(String token:tokens){
      if(token.isEmpty()) continue;
      if(token.replaceFirst("^\\s+", "").startsWith("->") && !uiElements.isEmpty() && uiElements.get(uiElements.size()-1) instanceof Button){
        Button temp = (Button) uiElements.get(uiElements.size()-1);
        temp.setOnAction(event->{
          String knotName = token.replace("->","").trim();
          incrementKnotsInstances(knotName);
          if(!getKnots(knotName).contains("->") && viewId == 2 && displayScore){
            PopUpController.scorePopUp(calculateMaxScore(mainKnot),Score);
          }
          processText(getKnots(knotName),false);
          mergeConsecutiveTextAreas();
        });
        uiElements.set(uiElements.size()-1,temp);
      }
      else if(token.replaceFirst("^\\s+", "").startsWith("->")){
        if(!text.isEmpty()){
          checkForTextAreaPredecessor(text);
          text = "";
        }
        String knotName = token.replace("->","").trim();
        inKnot = true;
        processText(getKnots(knotName),false);
        inKnot = false;
        incrementKnotsInstances(knotName);
      }
      else if(token.contains("+") && isButtonValid(token)){
        if(!text.isEmpty()){
          checkForTextAreaPredecessor(text);
          text = "";
        }
        int score;
        if(token.replace("+","").trim().matches(".*\\[\\d+\\].*")){
          int start = token.indexOf("[");
          int end = token.indexOf("]");
          score = tryParseInt(token.substring(start+1,end).trim(),0);
          displayScore = true;
        }
        else{
          score = 0;
        }
        Button newButton = new Button(token.replace("+","").replace("["+score+"]",""));
        int size = uiElements.size();
        newButton.addEventHandler(ActionEvent.ACTION, e -> {
          buttonPressed = true;
          Score += score;
          removeConsecutiveButtons(size);
        });
        if(token.contains("{") && token.contains("}")){
          int start = token.indexOf("{");
          int end = token.indexOf("}");
          boolean negative = false;
          String knotName = token.substring(start+1,end);
          newButton.setText(newButton.getText().replace("{"+knotName+"}",""));
          if(knotName.contains("not")){
            knotName = knotName.replace("not","").trim();
            negative = true;
          }
          else{
            knotName = knotName.trim();
          }
          if((getKnotsInstances(knotName) == 0 && !negative)||(getKnotsInstances(knotName) > 0 && negative)){
            newButton.setVisible(false);
            newButton.setMaxHeight(0);
            newButton.setManaged(false);
          }
        }
        uiElements.add(newButton);
      }
      else{
        text+=token;
      }
    }
    if(!text.isEmpty()){
      checkForTextAreaPredecessor(text);
    }
  }
  private void checkForTextAreaPredecessor(String text) {

    if (!uiElements.isEmpty() && uiElements.getLast() instanceof TextArea) {
      ((TextArea) uiElements.getLast()).setText(
          ((TextArea) uiElements.getLast()).getText() + text);
    } else if(uiElements.isEmpty()){
      TextArea newTextArea = new TextArea();
      newTextArea.setText(text);
      uiElements.add(newTextArea);
    }




  }
  protected String[] loadKnots(String textArea){
    String[] tokens = textArea.replaceFirst("\\s++$", "").split("(?<=\\n)");

    boolean knot = false;
    String knotName = "";
    for(int i=0; i<tokens.length; i++){
      if(tokens[i].matches("^===.+===$\n")){
        knot = true;
        knotName = tokens[i].replace("===","").replace("\n","").trim();
        knots.put(knotName,"");
        knotsInstances.put(knotName,0);
        tokens[i] = "";
      }
      else if(knot){
        knots.merge(knotName, tokens[i], String::concat);
        tokens[i] = "";
      }
    }

    return tokens;
  }
  private void removeConsecutiveButtons(int index){
    List<Node> toRemove = new ArrayList<>();
    for(int i=index;i<uiElements.size();i++){
      if(!(uiElements.get(i) instanceof Button)){
        break;
      }
      else{
        toRemove.add(uiElements.get(i));
      }
    }
    for(int i=index-1;i>=0;i--){
      if(!(uiElements.get(i) instanceof Button)){
        break;
      }
      else{
        toRemove.add(uiElements.get(i));
      }
    }
    uiElements.removeAll(toRemove);
  }
  private void mergeConsecutiveTextAreas(){
    TextArea textArea = (new TextArea());
    String text = "";
    int remove = 0;
    for(Node uiComponent : uiElements){
      if(uiComponent instanceof TextArea){
        text += ((TextArea) uiComponent).getText();
        remove++;
      }
      else {
        break;
      }
    }
    for(int i=0;i<remove;i++){
      uiElements.removeFirst();
    }
    if(!text.isEmpty()){
      textArea.setText(text);
      uiElements.add(0,textArea);
    }
  }
  private void addUiElements() {
    processedOutput.getChildren().clear();
    boolean buttonPresent = false;
    for(Node uiComponent : uiElements) {
      if(uiComponent instanceof Button) {
        buttonPresent = true;
        Button button = (Button) uiComponent;
        button.prefWidthProperty().bind(processedOutput.widthProperty());
        processedOutput.getChildren().add(button);
      }
      else if(uiComponent instanceof TextArea) {
        if(buttonPresent){
          break;
        }
        TextArea textArea = (TextArea) uiComponent;
        textArea.setWrapText(true);
        textArea.setEditable(false);
        VBox.setVgrow(textArea, Priority.ALWAYS);
        processedOutput.getChildren().add(textArea);

      }
    }
  /*  if(!buttonPresent && viewId == 2 && buttonPressed){
      PopUpController.scorePopUp(calculateMaxScore(mainKnot),Score);
    }*/
  }
  public static int tryParseInt(String str, int defaultValue) {
    try {
      return Integer.parseInt(str.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
  private String getKnots(String key) {
    return knots.getOrDefault(key, "\n");
  }
  private int getKnotsInstances(String key) {
    return knotsInstances.getOrDefault(key, 0);
  }
  private void incrementKnotsInstances(String key) {
    if(knotsInstances.containsKey(key)){
      knotsInstances.put(key,knotsInstances.get(key)+1);
    }
  }
  protected int calculateMaxScore(String textArea) {
    if(textArea == null || textArea.isEmpty()){
      return 0;
    }
    String[] tokens = textArea.trim().split("(?<=\\n)");
    int maxScore = 0;
    int tempScore = 0;
    boolean buttonFound = false;
    for(String token : tokens){
      if(token.contains("+")){
        if(token.contains("[") && token.contains("]")){
          int start = token.indexOf("[");
          int end = token.indexOf("]");
          tempScore = tryParseInt(token.substring(start+1,end).trim(),0);
        }
        buttonFound = true;
      }
      else if(buttonFound && token.contains("->")){
        String knotName = token.replace("->","").trim();
        maxScore = Math.max(maxScore,tempScore + calculateMaxScore(knots.get(knotName)));
        buttonFound = false;
        tempScore = 0;
      }
    }
    return maxScore + tempScore;

  }
  protected boolean isButtonValid(String input) {
    if (!input.startsWith("+")) {
      return false;
    }
    String remaining = input.substring(1).replaceFirst("\\s++$", "");
    if (remaining.isEmpty()) {
      return false;
    }
    boolean hasNumber = remaining.matches(".*\\[\\d+\\].*");
    boolean hasText = remaining.matches(".*\\{[^}]+\\}.*");
    if (hasNumber && hasText) {
      return true;
    }
    return hasText || hasNumber || remaining.matches(".*\\S.*");
  }
  @FXML
  protected void switchToMainView() {
    if(viewId == 1){
      String a = diagramController.getInput();
      inputTextArea.setText(a);
    }
    if(viewId != 0){
      onCompileButtonClick();

      mainBorderPane.setCenter(null);
      compileButton.setText("Compile");
      compileButton.setVisible(true);
      TextAreas.getItems().clear();
      TextAreas.getItems().addAll(inputTextArea,processedOutput);
      mainBorderPane.setCenter(TextAreas);
    }
    viewId = 0;
  }
  @FXML
  protected void switchToDiagramView() {
    if(viewId != 1){
      mainBorderPane.setCenter(null);
      compileButton.setVisible(false);
      //diagramController = new DiagramController();
      mainBorderPane.setCenter(diagramController.start(inputTextArea.getText()));
    }
    viewId = 1;
  }
  @FXML
  protected void switchToPlayView() {
    if(viewId == 1){
      inputTextArea.setText(diagramController.getInput());
    }
    if(viewId != 2){
      onCompileButtonClick();
      mainBorderPane.setCenter(null);
      compileButton.setVisible(false);
      mainBorderPane.setCenter(processedOutput);
      compileButton.setText("Replay");
      compileButton.setVisible(true);
    }
    viewId = 2;
  }
}