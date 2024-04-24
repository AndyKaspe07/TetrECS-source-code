package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import javafx.scene.control.TextField;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScoreScene extends BaseScene{
    private AnchorPane anchor;
    private final Game game;
    private final ArrayList<Pair<String, Integer>> scores = new ArrayList<>();
    private final Communicator communicator;




    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoreScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;

        communicator = gameWindow.getCommunicator();
    }

    /**
     * Initalise
     */
    @Override
    public void initialise() {
    }

    /**
     * Builds the scene and adds containers
     */
    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        anchor = new AnchorPane();
        setUpBackground();
        root.getChildren().add(anchor);
        setUpTitle();

        populateScores();

        addScore();
    }

    /**
     * Adds the title to anchorPane
     */

    private void setUpTitle() {
        Text title = new Text("Game Over!");
        title.getStyleClass().add("title");
        anchor.getChildren().add(title);
        positionNode(300.0,30.0, title);
    }

    /**
     * Creates VBox to hold the scores and calls appropriate methods to populate the VBox
     */
    private void setUpScores() {
        var local = new VBox();
        var online = new VBox();

        Text localScoresTitle = new Text("Local Scores");
        localScoresTitle.getStyleClass().add("title");
        local.getChildren().add(localScoresTitle);
        addLocalScores(local);

        Text onlineScoresTitle = new Text("Online Scores");
        onlineScoresTitle.getStyleClass().add("title");
        local.getChildren().add(onlineScoresTitle);


        positionNode(100, 200, local);
        positionNode(400, 200, online);

        anchor.getChildren().addAll(local, online);

    }

    /**
     * Background image
     */
    private void setUpBackground() {

        anchor.setLayoutX(1600);
        anchor.setLayoutY(500);
        anchor.getStyleClass().add("scores-background");
    }

    /**
     * Reads scores from ArrayList scores and makses Text objects for each score
     * @param local VBox to add the scores in
     */
    private void addLocalScores(VBox local) {

        for (int i=0; i<6; i++) {
            Pair<String, Integer> score = scores.get(i);
            String name = score.getKey();
            int value = score.getValue();
            Text display = new Text(name + ": " + value);
            display.getStyleClass().add("score");
            display.getStyleClass().add("highscore");
            local.getChildren().add(display);
        }
    }

    /**
     * Reads scores from text file and adds it to scores ArrayList
     */
    private void populateScores() {

        //Get all existing scores from text file and add to ArrayList scores
        try (BufferedReader br = new BufferedReader(new FileReader(scoresFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 2) { // Ensure there are at least two values in the line
                    String name = values[0].trim(); // Assume the name is the first value
                    int score = Integer.parseInt(values[1].trim()); // Assume the score is the second value
                    scores.add(new Pair<>(name, score));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method prompts user for their name and when submit button is clicked, adds the score to text file
     */
    private void addScore() {

        HBox nameBox = new HBox();
        Text enterName = new Text("Enter name: ");
        enterName.getStyleClass().add("instructions");

        TextField textField = new TextField();
        Button submit = new Button("Submit");
        nameBox.getChildren().addAll(new Text("Enter name:"), textField, submit);

        submit.setOnAction(e -> {
            String name = textField.getText();
            scores.add(new Pair<>(name, game.getIntScore()));
            sortScores();
            anchor.getChildren().remove(nameBox);
            setUpScores();

            writeNewScores();

        });

        positionNode(100.0, 100.0, nameBox);
        anchor.getChildren().add(nameBox);
    }

    /**
     * All scores from the ArrayList are writen back into the text file
     */
    private void writeNewScores() {
        clearScoresFile();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(scoresFile, true))) {
            for (Pair<String, Integer> score : scores) {

                // Writing the new score onto a new line in the CSV file

                bw.write(score.getKey() + "," + score.getValue());
                bw.newLine(); // Write a new line after the new score
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the scores text file. Used between each read/write to avoid duplication
     */
    private void clearScoresFile() {
        try (FileWriter fw = new FileWriter(scoresFile)) {
            // File is opened in write mode, which truncates the file if it exists
            // Since no data is written, the file is effectively cleared
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sorts the pairs in scores using the score.
     */
    private void sortScores() {
        scores.sort((pair1, pair2) -> pair2.getValue().compareTo(pair1.getValue()));
    }

}


