module org.ip1g22.interactivecybersecuritystories {
  requires javafx.controls;
  requires javafx.fxml;
  requires jdk.compiler;

  opens org.ip1g22.interactivecybersecuritystories to javafx.fxml;
  exports org.ip1g22.interactivecybersecuritystories;
}