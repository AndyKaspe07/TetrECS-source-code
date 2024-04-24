package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private MediaPlayer musicPlayer;

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        playMenuMusic();
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        HBox titlePane = new HBox();
        titlePane.setMaxHeight(5.0);
        titlePane.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
        titlePane.setAlignment(Pos.CENTER);
        titlePane.setSpacing(100.0);

        mainPane.setTop(titlePane);

        var title = new Text("TetrECS");
        title.getStyleClass().add("tetrECS");
        setupRotateAnimation(title);

        titlePane.getChildren().add(title);

        setupButtons(mainPane);

    }

    private void setupButtons(BorderPane mainPane) {

        // Layout of the buttons
        var singleplayerButton = new Button("Singleplayer");
        var multiplayerButton = new Button("Multiplayer");
        var howToPlayButton = new Button("How to Play");
        var quitButton = new Button("Quit");

        singleplayerButton.getStyleClass().add("menu-button");
        multiplayerButton.getStyleClass().add("menu-button");
        howToPlayButton.getStyleClass().add("menu-button");
        quitButton.getStyleClass().add("menu-button");


        VBox myVBox = new VBox(10);
        myVBox.getChildren().addAll(singleplayerButton, multiplayerButton, howToPlayButton, quitButton);
        myVBox.setAlignment(Pos.BOTTOM_CENTER);

        mainPane.setCenter(myVBox);

        //Bind the actionEvents to appropriate methods.
        singleplayerButton.setOnAction((e) -> {
            myMultimedia.playButtonClick();
            startGame(e);
        });
        quitButton.setOnAction(e -> {
            myMultimedia.playButtonClick();
            gameWindow.close();
        });
        howToPlayButton.setOnAction(e -> {
            myMultimedia.playButtonClick();
            loadHelpScene();
        });

    }

    private void loadHelpScene() {
        gameWindow.loadScene(new HowToPlayScene(gameWindow));
  }

  /** Initialise the menu */
  @Override
  public void initialise() {}

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        musicPlayer.stop();
        gameWindow.startChallenge();
    }
    private void playMenuMusic() {
        musicPlayer =  myMultimedia.playMenuMusic();
    }

    public MediaPlayer getMusicPlayer() {
        return musicPlayer;
    }
    private void setupRotateAnimation(Text text) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2.0), text);
        rotateTransition.setFromAngle(-10); // Rotate by 20 degrees
        rotateTransition.setToAngle(10); // Rotate by 20 degrees
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE); // Repeat indefinitely
        rotateTransition.setAutoReverse(true); // Auto-reverse

        rotateTransition.play();
    }
}
