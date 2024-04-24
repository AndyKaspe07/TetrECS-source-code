package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

public class PieceBoard extends GameBoard {


    public PieceBoard(Grid grid, double width, double height) {
        super(grid, width, height);

    }
    public PieceBoard(Grid grid, double width, double height, boolean dot) {
        super(grid, width, height);

        if (dot) {
            blocks[1][1].setDot();
        }
    }

    public void setPiece(GamePiece piece) {
        grid.playPiece(piece, 1, 1);
    }


}
