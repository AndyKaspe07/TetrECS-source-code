package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.PieceBoard;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Checks whether piece can be played at chosen location
     * @return boolean value true or false
     * @param gamePiece
     * @param x_coord x oordinate of middle of piece
     * @param y_coord y coordinate of middle of piece
     */
    public Pair<Boolean, HashSet<GameBlockCoordinate>> canPlayPiece(GamePiece gamePiece, int x_coord, int y_coord) {

        Boolean canPlay = null;
        HashSet<GameBlockCoordinate> availableCoords = new HashSet<GameBlockCoordinate>();
        int[][] pieceShape = gamePiece.getBlocks();
        int[][] temp = new int[3][3];

        fillUnavailablePositions(temp, x_coord, y_coord);

        for (int x=0; x<3; x++) {
            for (int y=0; y<3; y++) {
                if (pieceShape[x][y] != 0) {
                    if (temp[x][y] == -1) {
                        canPlay = false;
                    } else if (grid[x_coord + x-1][y_coord+y-1].get() != 0) {
                        canPlay = false;
                    } else {
                        availableCoords.add(new GameBlockCoordinate(x_coord+x-1, y_coord+y-1));
                    }
                }
            }
        }

        if (canPlay == null) {
            canPlay = true;
        }
        return new Pair<Boolean, HashSet<GameBlockCoordinate>>(canPlay, availableCoords);
    }


    /**
     * Helper method for canPlayPiece()
     * In-place updates positions of 3x3 matrix which fall outside the gameBoard
     */
    private void fillUnavailablePositions(int[][] temp, int x_coord, int y_coord) {
        if (x_coord == 0) {
            for (int i=0; i<3; i++) {
                temp[0][i] = -1;
            }
        }
        if (x_coord == 4){
            for (int i=0; i<3; i++) {
                temp[2][i] = -1;
            }
        }
        if (y_coord == 4) {
            for (int i=0; i<3; i++) {
                temp[i][2] = -1;
            }
        }
        if (y_coord == 0) {
            for (int i=0; i<3; i++) {
                temp[i][0] = -1;
            }
        }
    }

    /**
     * Plays the piece on the GameBoard
     * In-place
     * @param gamePiece
     * @param x coordinate to play the piece
     * @param y coordinate to play the piece
     */
    public void playPiece(GamePiece gamePiece, int x, int y) {
        int[][] pieces = gamePiece.getBlocks();
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                if (pieces[i][j] != 0) {
                    set(i + x - 1, j + y - 1, gamePiece.getValue());
                }
            }
        }
    }
    public void clearGrid() {
        for (int i=0; i<cols; i++) {
            for (int j=0; j<rows; j++) {
                set(i, j, 0);
            }
        }
    }

    public Pair<Pair<Integer, Integer>, HashSet<GameBlockCoordinate>> clearLines() {
        HashSet<GameBlockCoordinate> coordinatesToRemove = new HashSet<GameBlockCoordinate>();
        int linesCleared = 0;
        for (int x = 0; x < 5; x++) {
            boolean fullRow = true;
            boolean fullColumn = true;

            for (int i = 0; i < 5; i++) {
                if (grid[x][i].get() == 0) {
                    fullRow = false;
                }
                if (grid[i][x].get() == 0) {
                    fullColumn = false;
                }
            }
            if (fullRow) {
                linesCleared++;
                for (int temp = 0; temp < 5; temp++) {
                    coordinatesToRemove.add(new GameBlockCoordinate(x, temp));
                }
            }
            if (fullColumn) {
                linesCleared++;
                for (int temp = 0; temp < 5; temp++) {
                    coordinatesToRemove.add(new GameBlockCoordinate(temp, x));
                }
            }
        }




        return new Pair<>(new Pair<>(linesCleared, coordinatesToRemove.size()), coordinatesToRemove);
    }




}
