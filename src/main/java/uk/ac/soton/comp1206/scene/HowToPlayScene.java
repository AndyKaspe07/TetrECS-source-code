package uk.ac.soton.comp1206.scene;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class HowToPlayScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private MediaPlayer musicPlayer;

    public HowToPlayScene(GameWindow gameWindow) {
        super(gameWindow);
        playMenuMusic();
        logger.info("Creating Help Scene");
    }

    @Override
    public void initialise() {

    }

    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("howToPlay-background");

        loadInstructions(menuPane);

        root.getChildren().addAll(menuPane);

    }

    private void loadInstructions(StackPane menuPane) {

        AnchorPane anchor = new AnchorPane();
        Image instructions = new Image("file:src/main/resources/images/Instructions.png");
        ImageView myImageView = new ImageView(instructions);
        myImageView.setFitHeight(320.0);
        myImageView.setFitWidth(800.0);

        anchor.getChildren().add(myImageView);
        menuPane.getChildren().add(anchor);

        loadPieces(anchor);
    }

    private void setMenuScene() {
        gameWindow.loadScene(new MenuScene(gameWindow));
  }

    /**
     * Getter method for MediaPlayer object
     * @return
     */
    public MediaPlayer getMusicPlayer() {
        return musicPlayer;
    }

    /**
     * Calls the playMenuMusic method from Multimedia class.
     */
    private void playMenuMusic() {
        musicPlayer =  myMultimedia.playMenuMusic();
    }



    /**
     * This method takes creates a dynamic GameBoard for each piece in the game and displays it in the HowToPlay Scene.
     * @param anchor AnchorPane object used to position the nodes on the GUI.
     */
    private void loadPieces(AnchorPane anchor) {

        //Container 1 holds the first 7 gameboard on the first row
        HBox row1 = new HBox();
        row1.setSpacing(10.0);

        //Iterate through the integers, creating a new GamePiece for each integer and displaying it in the HBox.
        for (int i=0; i<8; i++) {
            Grid grid = new Grid(3, 3);
            GamePiece piece = GamePiece.createPiece(i);
            PieceBoard board = new PieceBoard(grid, 80.0, 80.0);
            grid.playPiece(piece, 1, 1);
            row1.getChildren().add(board);
        }
        positionNode(25.0, 350.0, row1);

        //Container 1 holds the next 7 gameBoards on the second row
        HBox row2 = new HBox();
        row2.setSpacing(10.0);

        //Iterate through the integers, creating a new GamePiece for each integer and displaying it in the HBox.
        for (int i=8; i<15; i++) {
            Grid grid = new Grid(3, 3);
            GamePiece piece = GamePiece.createPiece(i);
            PieceBoard board = new PieceBoard(grid, 80.0, 80.0);
            grid.playPiece(piece, 1, 1);
            row2.getChildren().add(board);
        }

        positionNode(25.0, 455.0, row2);



        anchor.getChildren().addAll(row1, row2);
    }
}

