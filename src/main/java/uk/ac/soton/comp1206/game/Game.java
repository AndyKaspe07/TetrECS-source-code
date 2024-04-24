package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameEndedListener;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.PiecePlacedListener;
import uk.ac.soton.comp1206.scene.Multimedia;
import uk.ac.soton.comp1206.scene.ScoreScene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);
    private final Multimedia myMultimedia = new Multimedia();

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;
    protected GamePiece currentPiece;
    protected GamePiece nextPiece;
    /**
     * The grid model linked to the game
     */
    protected Grid grid;

    protected SimpleIntegerProperty score;
    protected SimpleIntegerProperty level;
    protected SimpleIntegerProperty multiplier;
    protected SimpleIntegerProperty lives;
    protected SimpleIntegerProperty highScore;
    protected LineClearedListener lineClearedListener;
    protected GameLoopListener gameLoopListener;
    protected PiecePlacedListener piecePlayedListener;
    protected GameEndedListener gameEndedListener;
    protected Timer timer;
    private final String scoresFile = "src/main/resources/scores";


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        initialiseGame();

    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
        score = new SimpleIntegerProperty(0);
        lives = new SimpleIntegerProperty(3);
        multiplier = new SimpleIntegerProperty(1);
        level = new SimpleIntegerProperty(0);

        currentPiece = spawnPiece();
        nextPiece = spawnPiece();

        initialiseHighScore();

        startGameLoop();

    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        Pair temp = grid.canPlayPiece(currentPiece, x, y);
        if (Boolean.TRUE.equals(temp.getKey())) {
            startGameLoop();
            playPiece(x, y);
        } else {
            myMultimedia.unsuccessfulPlacement();
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Randmo generates a piece from the available GamePieces and returns it.
     * @return GamePiece
     */
    private GamePiece spawnPiece() {
        Random myRandom = new Random();
        int random = myRandom.nextInt(15);

        return GamePiece.createPiece(random);
    }


    /**
     * Replaces currentpiece with a random piece from the list of GamePieces
     */
    private void nextPiece() {
        currentPiece = nextPiece;
        nextPiece = spawnPiece();
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * This method updates the score by taking in the linesCleared and blocksCleared parameters
     * @param linesCleared
     * @param blocksCleared
     */
    private void updateScore (int linesCleared, int blocksCleared) {
        score.set(score.get() + (linesCleared * blocksCleared * 10 * multiplier.get()));
        System.out.println(score);
    }

    private void updateLevel() {
        int level = (int) Math.round(Math.floor(this.level.get()/1000));
        System.out.println(level);
    }

    /**
     * Helper method to update multiplier
     * @param linesCleared pass in number of lines cleared so we can calculate multiplier accordingly
     */
    private void updateMultiplier(int linesCleared) {
        if (linesCleared == 0) {
            multiplier.set(1);
        } else {
            multiplier.set(multiplier.get() + 1);
        }
    }

    /**
     * Getter method for score
     * @return Returns score property as a string
     */
    public StringBinding getScore() {
        return score.asString();
    }
    public Integer getIntScore() {
        return score.get();
    }
    public StringBinding getHighScore() {
        return highScore.asString();
    }

    /**
     * Getter method for lives
     * @return Returns lives property as a string
     */
    public StringBinding getLives() {
        return lives.asString();
    }

    /**
     * Getter method for level
     * @return Returns level property as a string
     */
    public StringBinding getLevel() {
        return level.asString();
    }

    /**
     * Using linesCleared and blocksCleared, we update all game variables accordingly
     * @param linesCleared Number of lines cleared after a piece has been played
     * @param blocksCleared Number of blocks cleared after a piece has been played
     */
    private void updateVariables(int linesCleared, int blocksCleared) {
        updateScore(linesCleared, blocksCleared);
        updateMultiplier(linesCleared);
        updateLevel();
        nextPiece();
    }


    /**
     * This method handles all the changes that needs to happen when a piece is played including sound effects
     * @param x X coordinate of where the piece is going to be played
     * @param y Y coordinate of where the piece is going to be played
     */
    private void playPiece(int x, int y) {
        //Audio
        myMultimedia.successfulPlacement();

        grid.playPiece(currentPiece, x, y);
        Pair<Pair<Integer, Integer>, HashSet<GameBlockCoordinate>> info = grid.clearLines();
        HashSet<GameBlockCoordinate> coordinatesToClear = info.getValue();

        //Listener updates
        lineClearedListener.lineCleared(coordinatesToClear);
        piecePlayedListener.piecePlayed();


        Pair<Integer, Integer> pair = info.getKey();
        int linesCleared = pair.getKey();
        int blocksCleared = pair.getValue();

        if (linesCleared > 0) {
            myMultimedia.linesCleared();
        }
        // Update all the variables (score, multiplier, level)
        updateVariables(linesCleared, blocksCleared);



    }

    /**
     * Calles the rotate method on the currentPiece property
     */
    public void rotateCurrentPiece(boolean right) {
        if (right) {
            currentPiece.rotate();
        } else {
            currentPiece.rotate(3);
        }
    }

    /**
     * Getter method for the current piece
     * @return Returns GamePiece current piece object
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Getter methof for the next piece
     * @return Returns next GamePiece object
     */
    public GamePiece getNextPiece() {
        return nextPiece;
    }

    /**
     * Using a temp variable, current piece is swapped with next piece.
     */
    public void swapPieces() {
        GamePiece temp = currentPiece;
        currentPiece = nextPiece;
        nextPiece = temp;
    }

    /**
     * Initialising listener
     * @param listener Takes in a LineClearedListener
     */
    public void setOnLineCleared(LineClearedListener listener) {
        lineClearedListener = listener;
    }
    /**
     * Initialising listener
     * @param listener Takes in a LineClearedListener
     */
    public void setOnPiecePlayed(PiecePlacedListener listener) {
        piecePlayedListener = listener;
    }
    /**
     * Initialising listener
     * @param listener Takes in a gameEndedListener
     */
    public void setOnGameEnded(GameEndedListener listener) {
        gameEndedListener = listener;
    }

    /**
     * Initialising listener
     * @param listener Takes in a GameLoopListener
     */
    public void setOnGameLoop(GameLoopListener listener) {
        gameLoopListener = listener;
    }

    /**
     * Calculates timer delay
     * @return Returns a long of the calculated delay
     */
    public long getTimerDelay() {
        long delay = Math.max(2500, 12000 - (500 * level.get()));
        logger.info(delay/1000 + " second delay");
        return delay;
    }


    /**
     * Checks if any lives left. If so, gameLoop is called and the game continues.
     */
    private void gameLoop() {


        //Timer has run out and gameLoop has been triggered
        logger.info("GameLoop triggered!");



        if (lives.get() == 3) {
            gameEndedListener.gameEnded();
            endGame();
        } else {
            nextPiece();
            myMultimedia.lifeLost();

            //Set variables
            lives.set(lives.get() - 1);
            multiplier.set(1);

            //Let the UI know that loop has been triggered
            gameLoopListener.gameLoopTriggered();

            startGameLoop();


        }

    }

    /**
     * Schedules a gameLoop call after a certain delay.
     */
    private void startGameLoop() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    gameLoop();
                });
            }
        }, getTimerDelay());
    }

    /**
     * Game end.
     */
    private void endGame() {
        logger.info("Game Over");
    }

    /**
     * Gets high score from the text file and binds it to the UI
     */
    private void initialiseHighScore() {
        try (BufferedReader br = new BufferedReader(new FileReader(scoresFile))) {
            String line;
            String lastLine = null;
            while ((line = br.readLine()) != null) {
                // Store the current line in lastLine
                lastLine = line;
            }
            String[] parts = lastLine.split(",");
            highScore =  new SimpleIntegerProperty(Integer.parseInt(parts[1]));
        } catch (Exception e) {
        }
    }
}
