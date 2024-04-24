package uk.ac.soton.comp1206.scene;

import java.util.HashSet;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  protected Game game;
  private MediaPlayer musicPlayer;
  private PieceBoard currentPiece;
  private PieceBoard nextPiece;
  private VBox sideBar;
  private Grid currentPieceGrid;
  private Grid nextPieceGrid;
  private GameBoard board;
  private AnchorPane anchorPane;
  private Rectangle timerBar;
  private Timeline timeline;


  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    playMusic();

    logger.info("Creating Challenge Scene");
  }

  /** Build the Challenge window */
  @Override
  public void build() {

    logger.info("Building " + this.getClass().getName());

    setupGame();

    buildBoard();

    // ScoreContainer holds the score on the top left of the screen
    var scoreContainer = new VBox();
    setUpScoreContainer(scoreContainer);

    // LivesContainer
    var livesContainer = new VBox();
    setUpLivesContainer(livesContainer);

    // Title
    var titleContainer = new VBox();
    setUpTitle(titleContainer);

    // Generate sideBar
    generateSideBar(anchorPane);

    populateSideBar();

    startTimerBar();
    anchorPane.getChildren().addAll(scoreContainer, livesContainer, titleContainer);
  }

  /**
   * Handle when a block is clicked
   *
   * @param gameBlock the Game Block that was clocked
   */
  private void blockClicked(GameBlock gameBlock) {
    game.blockClicked(gameBlock);
    refreshPieceBoards();
  }

  /** Setup the game object and model */
  public void setupGame() {
    logger.info("Starting a new challenge");

    // Start new game
    game = new Game(5, 5);
  }

  /** Initialise the scene and start the game */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");

    // Handle keyboard controls
    scene.setOnKeyPressed(
        keyEvent -> {

          // Piece Rotation (right)
          if (keyEvent.getCode() == KeyCode.C | keyEvent.getCode() == KeyCode.E) {
            game.rotateCurrentPiece(true);
          }

          //Piece Rotation (left)
          if (keyEvent.getCode() == KeyCode.Q | keyEvent.getCode() == KeyCode.Z) {
            game.rotateCurrentPiece(false);
          }
          // Piece swap event
          if (keyEvent.getCode() == KeyCode.SPACE | keyEvent.getCode() == KeyCode.R) {
            game.swapPieces();
          }

          int x = board.getHoveredBlock().getX();
          int y = board.getHoveredBlock().getY();

          //Handle arrow keys to move the hovered piece
          switch (keyEvent.getCode()) {
            case RIGHT, D -> board.setHoveredBlockk(board.getBlock(Math.min(4, x + 1), y));
            case LEFT, A -> board.setHoveredBlockk(board.getBlock(Math.max(0, x - 1), y));
            case DOWN, S -> board.setHoveredBlockk(board.getBlock(x, Math.min(4, y + 1)));
            case UP, W -> board.setHoveredBlockk(board.getBlock(x, Math.max(0, y - 1)));
            case ENTER, X -> game.blockClicked(board.getBlock(x, y));
          }

          refreshGraphics();
        });

    //Setup more listeners
    game.setOnLineCleared(this::lineCleared);
    game.setOnGameLoop(this::gameLoopTriggered);

    game.setOnPiecePlayed(this::refreshPieceBoards);
    game.setOnPiecePlayed(this::startTimerBar);
    game.setOnGameEnded(this::endChallengeScene);

    game.start();
  }

  /**
   * This  method builds the UI of the ChallengeScene by building necessary panes and positioning them wherever needed.
   */
  private void buildBoard() {
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var challengePane = new StackPane();
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("menu-background");
    root.getChildren().add(challengePane);

    board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
    board.setGame(game);
    anchorPane = new AnchorPane();
    positionNode(50.0, 120.0, board);

    anchorPane.getChildren().add(board);
    root.getChildren().addAll(anchorPane);

    // Handle block on gameboard grid being clicked
    board.setOnBlockClick(this::blockClicked);
  }


  /**
   * Sets up a VBox on the right of the screen and adds the lives property to it.
   * @param livesContainer VBox where the property should be placed in
   */
  private void setUpLivesContainer(VBox livesContainer) {

    // Lives
    Text livesTitle = new Text("lives");
    livesTitle.getStyleClass().add("lives");
    Text lives = new Text();
    lives.textProperty().bind(game.getLives());
    lives.getStyleClass().add("lives");

    positionNode((double) gameWindow.getWidth() - 150.0, 10.0, livesContainer);

    livesContainer.getChildren().addAll(livesTitle, lives);
  }
  /**
   * Sets up a VBox on the right of the screen and adds the score property to it.
   * @param scoreContainer VBox where the property should be placed in
   */
  private void setUpScoreContainer(VBox scoreContainer) {
    positionNode(50.0, 10.0, scoreContainer);

    // Score
    Text scoreTitle = new Text("score");
    scoreTitle.getStyleClass().add("score");

    var score = new Text();
    score.textProperty().bind(game.getScore());
    score.getStyleClass().add("score");

    scoreContainer.getChildren().addAll(scoreTitle, score);
  }

  /**
   * Sets up the title of the ChallengeScene. Adds a style to it and positions it in the centre of the screen
   * @param titleContainer VBox in which the title can be found.
   */
  private void setUpTitle(VBox titleContainer) {
    positionNode(250.0, 10.0, titleContainer);
    Text title = new Text("Challenge Mode");
    title.getStyleClass().add("title");

    titleContainer.getChildren().add(title);
  }

  /**
   * Two pieceboards are built and positioned in the bottom right of the screen.
   * Listener is also set up to call rotateBlock method when the pieceboard has been pressed.
   */
  private void setUpPieceBoards() {
    // Build current PieceBoard
    currentPieceGrid = new Grid(3, 3);
    currentPiece = new PieceBoard(currentPieceGrid, 150.0, 150.0, true);
    currentPiece.setPiece(game.getCurrentPiece());
    currentPiece.setOnBlockClick(this::rotateBlock);

    // Build next PieceBoard
    nextPieceGrid = new Grid(3, 3);
    nextPiece = new PieceBoard(nextPieceGrid, 100.0, 100.0);
    nextPiece.setPiece(game.getNextPiece());
  }

  /**
   * This method builds a sideBar where all the pieceboards, livescontainers, scorecontainer all sit.
   * @param pane AnchorPane which holds the entire screen as a whole
   */
  private void generateSideBar(AnchorPane pane) {

    sideBar = new VBox();
    sideBar.setStyle("\"-fx-border-color: yellow; -fx-border-width: 2px;\"");
    sideBar.setAlignment(Pos.CENTER);
    pane.getChildren().add(sideBar);
    positionNode(600.0, 130, sideBar);
  }

  /**
   * Adds relevant text objects to the sidebar and binds the score and level properties to the text objects.
   */
  private void populateSideBar() {
    Text levelTitle = new Text("Level");
    levelTitle.getStyleClass().add("highscore-label");
    Text level = new Text();
    level.getStyleClass().add("level");
    level.textProperty().bind(game.getLevel());

    Text highScoreLabel = new Text("High Score");
    highScoreLabel.getStyleClass().add("highscore-label");
    Text highScore = new Text();
    highScore.textProperty().bind(game.getHighScore());
    highScore.getStyleClass().add("level");

    sideBar.setSpacing(10.0);

    //Uses helper method to build 2 pieceBoards for currentPiece and nextPiece
    setUpPieceBoards();

    Text incoming = new Text("Incoming");
    incoming.getStyleClass().add("incoming-text");
    sideBar
        .getChildren()
        .addAll(
            levelTitle, level, highScoreLabel, highScore, incoming, currentPiece, nextPiece);
  }

  /**
   * This method rotates the current piece. Also refreshes the graphics by calling refreshPieceBoards().
   * @param gameBlock GameBlock which we need to rotate
   */
  private void rotateBlock(GameBlock gameBlock) {
    logger.info("Rotating piece...");
    game.rotateCurrentPiece(true);
    refreshPieceBoards();
  }

  /**
   * Clearing the grid and playing the piece which should be now played.
   */
  private void refreshPieceBoards() {

    logger.info("Refreshing Pieceboards.");
    currentPieceGrid.clearGrid();
    currentPieceGrid.playPiece(game.getCurrentPiece(), 1, 1);

    nextPieceGrid.clearGrid();
    nextPieceGrid.playPiece(game.getNextPiece(), 1, 1);
  }

  /** Stop any existing music playing and play the challenge scene music. */
  private void playMusic() {
    musicPlayer = myMultimedia.playGameMusic();
  }

  /**
   * Getter method for the MediaPlayer which plays music.
   * @return MediaPlayer object
   */
  @Override
  public MediaPlayer getMusicPlayer() {
    return musicPlayer;
  }

  /**
   * This method adds a fade effect to the blocks which are being cleared due to a line clear.
   * @param blocks Hashet of blocks which will be faded out.
   */
  private void lineCleared(HashSet<GameBlockCoordinate> blocks) {
    board.fade(blocks);
  }

  /**
   * This method initiates a timer bar at the bottom and starts a Timeline transitino, making the bar smaller
   * as gameLoop progresses.
   */
  private void startTimerBar() {
    logger.info("starting new timer bar!");
    if (timeline != null) {
      timeline.stop();
    }

    anchorPane.getChildren().remove(timerBar);

    timerBar = new Rectangle(800, 30);
    timerBar.setFill(Color.ORANGE);
    positionNode(0.0, 575, timerBar);
    anchorPane.getChildren().add(timerBar);


    // Create a timeline animation
    timeline = new Timeline();
    timeline.getKeyFrames().add(
            new KeyFrame(Duration.millis(game.getTimerDelay()), new KeyValue(timerBar.widthProperty(), 0))
    );

    timeline.play(); // Start the animation
  }

  /**
   * This method loads a new ScoreScene. Called after the game is finished.
   */
  private void endChallengeScene() {
    gameWindow.loadScene(new ScoreScene(gameWindow, game));
  }

  /**
   * This method is called each time a gameLoop occurs. Graphics are updated and timer is restarted.
   */

  private void gameLoopTriggered() {
    refreshGraphics();
    startTimerBar();
  }

  /**
   * This method refreshes the graphics of the pieceBoards and the hovering blocks.
   */
  private void refreshGraphics() {
    refreshPieceBoards();
    board.resetHovered();
    board.hovered(board.getHoveredBlock());
  }
}
