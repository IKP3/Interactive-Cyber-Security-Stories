package org.ip1g22.interactivecybersecuritystories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class DiagramController extends MainController {
  private LinkedHashMap<String, StoryNode> knotsNodes;
  Map<Integer, List<StoryNode>> orderedNodes;
  private Group diagramPane;
  private List<StoryNode> nodes;
  private List<StoryConnection> connections;
  private Map<StoryConnection,StoryNode> connectionNodes;
  private List<Text> scoreTexts;
  private BorderPane root;
  private Button newNodeButton;
  private boolean isProgrammaticChange;
  private Rectangle anchor;
  public DiagramController() {
    connections = new ArrayList<>();
    connectionNodes = new HashMap<>();
    scoreTexts = new ArrayList<>();
    orderedNodes = new HashMap<>();
    anchor = new Rectangle(1, 1);
    anchor.setFill(Color.TRANSPARENT);
    isProgrammaticChange = false;

    root = new BorderPane();
    diagramPane = new Group();

    diagramPane.getStyleClass().add("diagramPane");
    newNodeButton = new Button("+");
    newNodeButton.setOnMouseClicked(event -> {
      addNode("New Node","New_Node");
      refreshView();
    });
    newNodeButton.setId("newNodeButton");
    ToolBar toolBar = new ToolBar(newNodeButton);
    root.setTop(toolBar);
  }
  public Pane start(String textArea) {
    ScrollPane diagramView = new ScrollPane();
    diagramView.setContent(diagramPane);
    diagramView.getStyleClass().add("diagram-scroll-pane");

    root.setCenter(diagramView);
    knotsNodes = new LinkedHashMap<>();
    nodes = new ArrayList<>();

    processText(textArea);
    getConnections();

    calculateNodeOrder(knotsNodes.get("main").getText());

    int startX = 100;
    int startY = 100;
    int spacingX = 250;
    int spacingY = 150;

    for (int i = 0; i < orderedNodes.size(); i++) {
      List<StoryNode> rowNodes = orderedNodes.get(i);
      int rowSize = rowNodes.size();
      for(int j = 0; j < rowSize; j++) {
        StoryNode currentNode = rowNodes.get(j);
        int newX = startX + j * spacingX;
        int newY = startY + i * spacingY;

        currentNode.setTranslateX(newX);
        currentNode.setTranslateY(newY);

      }

    }

    refreshView();

    return root;
  }
  private void processText(String textArea) {
    uiElements = FXCollections.observableArrayList();
    knots = new LinkedHashMap<>();
    knotsInstances = new HashMap<>();
    String[] tokens = loadKnots(textArea);
    addNode(String.join("", tokens),"main");
    for(String knot: knots.keySet()) {
      addNode(knots.get(knot),knot);
    }
  }
  private void getConnections() {
    connections.clear();
    connectionNodes.clear();
    scoreTexts.clear();
    orderedNodes.clear();

    String knotName = "";
    for(String knot : knotsNodes.keySet()) {
      String[] tokens = knotsNodes.get(knot).getText().replaceFirst("\\s++$", "").split("(?<=\\n)");
      for(int i = 0; i<tokens.length; i++) {
        String token = tokens[i];
        if(token.contains("->") && !token.contains("main")) {
          knotName = token.replace("->","").trim();
          if(knotsNodes.containsKey(knotName)) {
            StoryConnection connection;
            if(i-1>=0 && tokens[i-1].contains("+") && tokens[i-1].contains("[") && tokens[i-1].contains("]") && isButtonValid(tokens[i-1])) {
              int start = tokens[i-1].indexOf("[");
              int end = tokens[i-1].indexOf("]");
              int score;
              try{
                score = Integer.parseInt(tokens[i-1].substring(start+1,end).trim());
                connection = new StoryConnection(knotsNodes.get(knot),knotsNodes.get(knotName),score);
                scoreTexts.add(connection.getScoreText());
              }
              catch (NumberFormatException e){
                connection = new StoryConnection(knotsNodes.get(knot),knotsNodes.get(knotName));

              }
            }
            else{
              connection = new StoryConnection(knotsNodes.get(knot),knotsNodes.get(knotName));
            }
            connectionNodes.put(connection,knotsNodes.get(knot));
            connections.add(connection);
          }
        }
      }
    }
  }
  private void updateConnections() {
    String mainText = knotsNodes.get("main").getText();
    if(mainText != null) {
      maxScore = calculateMaxScore(mainText);
    }
    getConnections();
    for(StoryConnection connection : connections) {
      connection.refresh();
    }
    refreshView();
  }
  public String getInput() {
    String output = "";
    output += knotsNodes.get("main").getText();
    for(String knot: knotsNodes.keySet()) {
      if(knot.equals("main")){
        continue;
      }
      if(!output.endsWith("\n")){
        output += "\n";
      }
      output += "===" + knot + "===\n";
      output += knotsNodes.get(knot).getText().replaceFirst("\\s++$", "");

    }
    return output.toString();
  }
  public String addNode(String text,String name) {
    String tempName = name;
    int temp = 2;
    while (true){
      if(knotsNodes.containsKey(tempName)) {
        tempName = name + temp;
        temp++;
      }
      else{
        name = tempName;
        break;
      }
    }


    StoryNode node = new StoryNode(text,name);
    knotsNodes.put(name, node);
    nodes.add(node);

    node.getTextProperty().addListener((obs, oldText, newText) -> {
      if(isProgrammaticChange){
        isProgrammaticChange = false;
        return;
      }
      automaticKnot(oldText,newText,node);
      updateConnections();
      calculateNodeOrder(knotsNodes.get("main").getText());
    });
    node.getNameProperty().addListener((obs, oldName, newName) -> {
      List<StoryNode> nodes = new ArrayList<>(connectionNodes.values());
      for (StoryNode storyNode : nodes) {
        String nodeText = storyNode.getText();
        if (!nodeText.contains(oldName)) {
          continue;
        }
        nodeText = nodeText.replace("->" + oldName, "->" + newName);
        storyNode.setText(nodeText);
      }
      knotsNodes.put(newName, node);
      knotsNodes.remove(oldName);

      knots.put(newName,knots.get(oldName));
      knots.remove(oldName);

      knotsInstances.put(newName,knotsInstances.get(oldName));
      knotsInstances.remove(oldName);



      updateConnections();
      calculateNodeOrder(knotsNodes.get("main").getText());
    });
    return name;
  }
  private void refreshView(){
    diagramPane.getChildren().clear();
    diagramPane.getChildren().add(anchor);
    diagramPane.getChildren().addAll(nodes);
    diagramPane.getChildren().addAll(connections);
    diagramPane.getChildren().addAll(scoreTexts);
  }
  private String getNewTextDifference(String oldText, String newText) {
    if (newText.startsWith(oldText)) {
      return newText.substring(oldText.length());
    }
    return newText;
  }
  private void automaticKnot(String oldText, String newText, StoryNode node){
    String difference = getNewTextDifference(oldText, newText);
    String[] tokens = difference.replaceFirst("\\s++$", "").replaceFirst("^\\n+", "").split("(?<=\\n)");
    String updatedText = "";
    for(int i = 0; i<tokens.length; i++) {
      String token = tokens[i];
      if(token.contains("+") && isButtonValid(token) && (i + 1 >= tokens.length || !tokens[i+1].contains("->"))) {
        updatedText += token;
        String nodeName = addNode("New Node","New_Node");
        if(!updatedText.endsWith("\n")){
          updatedText += "\n";
        }
        updatedText += "->" + nodeName + "\n";
      }
      else{
        updatedText += token;
      }
    }
    if(difference.replaceFirst("\\s++$", "").equals(newText.replaceFirst("\\s++$", ""))){
      node.setText(updatedText);
    }
    else{
      isProgrammaticChange = true;
      node.setText(oldText.replaceFirst("\\s++$", "") + "\n" + updatedText);
    }
  }
  protected void calculateNodeOrder(String textArea) {
    orderedNodes.put(0,new ArrayList<>());
    orderedNodes.get(0).add(knotsNodes.get("main"));
    calculateNodeOrder(textArea,1);
  }
  private void calculateNodeOrder(String textArea, int index) {
    if(textArea == null || textArea.isEmpty()){
      return;
    }
    String[] tokens = textArea.trim().split("(?<=\\n)");
    for(String token : tokens){
      if(token.contains("->")){
        String knotName = token.replace("->","").trim();
        if(!orderedNodes.containsKey(index)){
          orderedNodes.put(index,new ArrayList<>());
        }
        if(knotsNodes.containsKey(knotName)){
          orderedNodes.get(index).add(knotsNodes.get(knotName));
        }
        calculateNodeOrder(knots.get(knotName),index+1);
      }
    }

  }
}


