package org.ip1g22.interactivecybersecuritystories;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ExportController extends MainController{
  private String scorePage;
  String textArea;
  @FXML
  public void initialize() {
    scorePage = "";
    textArea = "";
  }
  public void exportStoryToLatex(String text){
    saveToLatex(processText(text,true));
  }
  public void exportStoryToPdf(String text,Boolean isElectronicExport){
    String pdfFileName = saveFileExplorer("pdf");
    if(pdfFileName == null){
      return;
    }
    String latexFileName = pdfFileName.replace(".pdf", ".tex");
    saveToLatex(processText(text,isElectronicExport), latexFileName);

    try {
      File texFile = new File(latexFileName);
      ProcessBuilder pb = new ProcessBuilder("pdflatex", texFile.getName());
      pb.redirectOutput(Redirect.INHERIT);
      pb.redirectError(Redirect.INHERIT);
      pb.directory(texFile.getParentFile());

      Process process = pb.start();
      process.waitFor();
      if(!isElectronicExport) {
        ProcessBuilder pb2 = new ProcessBuilder("pdflatex", texFile.getName());
        pb2.redirectOutput(Redirect.INHERIT);
        pb2.redirectError(Redirect.INHERIT);
        pb2.directory(texFile.getParentFile());

        Process process2 = pb2.start();
        process2.waitFor();
      }
      System.out.println("PDF Generated: " + pdfFileName);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void exportToPhysicalPdf(){

  }
  private String processText(String textArea,Boolean isElectronicExport) {
    this.textArea = textArea;
    scorePage = "";
    String currentKnot = "";
    uiElements = FXCollections.observableArrayList();
    knots = new HashMap<>();
    knotsInstances = new HashMap<>();
    String[] tokens = textArea.split("(?<=\\n)");
    loadKnots(textArea);

    String text = "\\section*{}\n";
    boolean button = false;
    String token="";
    boolean beginItemize = false;
    for(int i = 0; i<tokens.length; i++){
      token = tokens[i];
      if(!button&& token.contains("->")){
        if(knots.containsKey(token.replace("->",""))){
         text += knots.get(token);
        }
      }
      else if(token.contains("+") && isButtonValid(token)){
        String buttonScore = "";
        String command = "";
        if(token.replace("+","").trim().matches(".*\\[\\d+\\].*")){
          int start = token.indexOf("[");
          int end = token.indexOf("]");
          int score = tryParseInt(token.substring(start+1,end).trim(),0);
          token = token.replace("+","").replace("["+score+"]","").replace("\n","");
          buttonScore = token + " ";
          if(score >= 0){
            buttonScore += "(+";
          }
          else{
            buttonScore += "(-";
          }
          buttonScore = escapeForLatex(buttonScore) + score + " Points)\\\\";
        }
        if(token.contains("{") && token.contains("}")){
          int start = token.indexOf("{");
          int end = token.indexOf("}");
          boolean negative = false;
          String condition = token.substring(start+1,end);
          if(condition.contains("not")){
            command = "\\textit{[If not visited "+condition.replace("not","")+"]  }" + command;
          }
          else{
            command = "\\textit{[If visited "+condition+"]  }" + command;
          }
          token = token.replace("{"+condition+"}","");
        }
        button = true;
        if(!beginItemize){
          text += "\\begin{itemize}\n";
          beginItemize = true;
        }

        String buttonText = token.replace("+","").trim();
        String knotName = "";
        if(i+1<tokens.length && tokens[i+1].contains("->")){
          knotName = tokens[i+1].replace("->","").trim();
          i = i+1;
        }
        if(isElectronicExport){
          command += "\\hyperlink{"+knotName+"}{"+buttonText+"}\n";
        }
        else{
          command += buttonText + "; See page: " + "\\pageref{"+knotName+"}\n";
        }
        text += "\\item " + command;
        scorePage += "\n" + buttonScore;

        if(i + 1 >= tokens.length || !tokens[i + 1].contains("+")){
          text += "\\end{itemize}\n";
          beginItemize = false;
        }
      }
      else if(token.matches("^===.+===$\n")){
        button = false;
        String knotName = token.replace("===","").replace("\n","").trim();
        text += "\n\\newpage\n";
        if(isElectronicExport){
          text += "\\section*{"+escapeForLatex(knotName)+"}" + "\\hypertarget{"+knotName+"}{}\n";
        }
        else{
          text += "\\section*{"+escapeForLatex(knotName)+"}" + "\\label{"+knotName+"}\n";
        }
        if (knots.containsKey(knotName)) {
          String knotText = knots.get(knotName);
          while (knotText.contains("[") && knotText.contains("]")) {
            int start = knotText.indexOf("[");
            int end = knotText.indexOf("]", start);
            if (start == -1 || end == -1) break;

            String score = knotText.substring(start + 1, end).trim();
            try {
              Integer.parseInt(score);
              scorePage += "\n" + "\\subsection*{" + escapeForLatex(knotName) + "}";
              break;
            } catch (Exception ignored) {
              knotText = knotText.substring(0, start) + knotText.substring(end + 1);
            }
          }
        }

      }
      else{
        token = token.replace("\n","\\\\\n");
        if(token.equals("\\\\\n")){
          token = "\n";
        }
        text += token;
      }
    }
    return text;
  }
  @Override
  protected String[] loadKnots(String textArea){
    String[] tokens = textArea.replaceFirst("\\s++$", "").split("(?<=\\n)");
    List<String> mainKnot = new ArrayList<>();
    boolean knot = false;
    String knotName = "";
    for(int i=0; i<tokens.length; i++){
      if(tokens[i].matches("^===.+===$\n")){
        knot = true;
        knotName = tokens[i].replace("===","").replace("\n","").trim();
        knots.put(knotName,"");
        knotsInstances.put(knotName,0);
      }
      else if(knot){
        knots.merge(knotName, tokens[i].replace("\n","\\\\n"), String::concat);
      }
      else{
        mainKnot.add(tokens[i]);
      }
    }
    return mainKnot.toArray(new String[0]);
  }
  private void saveToLatex(String story) {
    String fileName = saveFileExplorer("tex");
    if(fileName == null){
      return;
    }
    this.saveToLatex(story, fileName);
  }
  private void saveToLatex(String story,String fileName) {
    try (FileWriter writer = new FileWriter(fileName)) {
      writer.write("\\documentclass{article}\n");
      writer.write("\\usepackage{hyperref}\n");
      writer.write("\\begin{document}\n");
      writer.write(story);
      writer.write("\n\\newpage\n");
      int maxScore = calculateMaxScore(textArea);
      if(maxScore != 0){
        writer.write("\\section*{Score}");
        writer.write("\n\\subsection*{Main}");
        writer.write(scorePage);
        writer.write("\n"+"\\par");
        writer.write("\n"+"\\textbf{"+"Maximum Score: " + calculateMaxScore(textArea)+"}");
      }
      writer.write("\n\\end{document}\n");
      System.out.println("Story exported to " + fileName);
    } catch (IOException e) {
      System.out.println("Error while exporting story");
      e.printStackTrace();
    }
  }

  public void exportStoryToTxt(String text) {
    String fileName = saveFileExplorer("txt");
    if(fileName == null){
      return;
    }
    try (FileWriter writer = new FileWriter(fileName)) {
      writer.write(text);
      System.out.println("File saved successfully: " + fileName);
    } catch (IOException e) {
      System.err.println("Error saving file: " + e.getMessage());
      e.printStackTrace();
    }
  }
  public String readTxtFile() {
    String fileName = openFileExplorer();
    try{
      return readTxtFile(fileName);
    }catch (Exception e){
      return null;
    }

  }
  public String readTxtFile(String fileName) {
    if(fileName == null){
      return null;
    }
    try {
      return new String(Files.readAllBytes(Paths.get(fileName)));
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
  public String openFileExplorer() {
    Stage stage = SceneManager.getStage();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open File");
    fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
    File selectedFile = fileChooser.showOpenDialog(stage);

    if (selectedFile != null) {
      System.out.println("Selected file: " + selectedFile.getAbsolutePath());
      return selectedFile.getAbsolutePath();
    } else {
      System.out.println("File selection canceled.");
      return null;
    }
  }
  public String saveFileExplorer(String fileType) {
    String[] allowedTypes = new String[]{"pdf","txt","tex"};
    if (!Arrays.asList(allowedTypes).contains(fileType)) {
      return "";
    }
    Stage stage = SceneManager.getStage();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save File");
    fileChooser.setInitialFileName("document.txt");
    switch (fileType){
      case "pdf":
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
        break;
      case "txt":
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
        break;
      case "tex":
        fileChooser.getExtensionFilters().add(new ExtensionFilter("LaTeX Files", "*.tex"));
        break;
    }

    File selectedFile = fileChooser.showSaveDialog(stage);
    if (selectedFile != null) {
      System.out.println("File will be saved at: " + selectedFile.getAbsolutePath());
      return selectedFile.getAbsolutePath();
    } else {
      System.out.println("Save operation canceled.");
      return null;
    }
  }
  private String escapeForLatex(String input) {
    if (input == null) return null;

    return input
        .replace("\\", "\\textbackslash{}")
        .replace("&", "\\&")
        .replace("%", "\\%")
        .replace("$", "\\$")
        .replace("#", "\\#")
        .replace("_", "\\_")
        .replace("{", "\\{")
        .replace("}", "\\}")
        .replace("~", "\\textasciitilde{}")
        .replace("^", "\\textasciicircum{}")
        .replace("[", "{[")
        .replace("]", "]}");
  }

}

