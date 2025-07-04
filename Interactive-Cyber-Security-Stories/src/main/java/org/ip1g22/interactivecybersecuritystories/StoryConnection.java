package org.ip1g22.interactivecybersecuritystories;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class StoryConnection extends Line {
  StoryNode start;
  StoryNode end;
  Integer score;
  Text scoreText;

  public StoryConnection(StoryNode start, StoryNode end, Integer score) {
    this(start, end);
    this.score = score;
    updateScorePosition();
  }

  public StoryConnection(StoryNode start, StoryNode end) {
    this.start = start;
    this.end = end;
    scoreText = new Text();
    getStyleClass().add("story-connection-line");
    scoreText.getStyleClass().add("story-connection-text");

    start.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
      setStartX(newBounds.getCenterX());
      setStartY(newBounds.getMaxY());
      updateScorePosition();
    });

    end.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
      setEndX(newBounds.getCenterX());
      setEndY(newBounds.getMinY());
      updateScorePosition();
    });

    setStrokeWidth(2);
  }

  public void refresh() {
    var startBounds = start.getBoundsInParent();
    var endBounds = end.getBoundsInParent();

    setStartX(startBounds.getCenterX());
    setStartY(startBounds.getMaxY());
    setEndX(endBounds.getCenterX());
    setEndY(endBounds.getMinY());
    updateScorePosition();
  }

  private void updateScorePosition() {
    if (score == null) {
      scoreText.setText("");
      return;
    }

    double endX = getEndX();
    double endY = getEndY();

    double offsetY = -10;

    scoreText.setX(endX);
    scoreText.setY(endY + offsetY);
    scoreText.setText(Integer.toString(score));

    scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
    scoreText.setFill(Color.WHITE);
    scoreText.setStroke(Color.BLACK);
    scoreText.setStrokeWidth(0.5);
  }



  public Text getScoreText() {
    return scoreText;
  }
}
