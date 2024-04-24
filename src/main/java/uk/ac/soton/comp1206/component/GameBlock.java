package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);
    private static AnimationTimer timer;
    private boolean hovered = false;

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;

    private final double width;
    private final double height;
    /**
     * Lets us know if a dot needs to be rendered on this GameBlock
     */
    private boolean dot;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
          paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }

        if (dot) {
            paintDot();
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

//        //Fill
//        gc.setFill(Color.WHITE);
//        gc.fillRect(0,0, width, height);

        var start = new Stop(0, Color.color(0, 0, 0, 0.3));
        var end = new Stop(1, Color.color(0, 0, 0, 0.5));
        var gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.REFLECT, start, end);

        gc.setFill(gradient);
        gc.fillRect(0, 0, width, height);
        //Border
        gc.setStroke(Color.GREY);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        gc.setFill(colour);
        gc.fillRect(0,0, width, height);

        //Light side
        gc.setFill(Color.color(1,1,1, 0.3));
        gc.fillPolygon(new double[] {0, width, 0}, new double[] {0,0,height}, 3);

        //Dark side
        gc.setFill(Color.color(1,1,1, 0.4));
        gc.fillRect(0,0,width, 3);
        gc.setFill(Color.color(1,1,1, 0.4));
        gc.fillRect(0, 0, 3, height);
        //Border
        gc.setStroke(Color.color(0, 0, 0, 0.6));
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Helper method to paint the center dot on incoming piece.
     */
    protected void paintDot() {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(Color.color(1, 1, 1, 0.7));
        gc.fillOval(width/4, height/4, width/2, height/2);
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    /**
     * Setter method for boolean field dot.
     */
    public void setDot() {
        dot=true;
    }

    /**
     * This method paints hovered GameBlocks either red or green
     * @param canPlay Boolean value letting us know if the chosen position is valid.
     */
    public void paintHovered(boolean canPlay) {
        hovered=true;

        var gc = getGraphicsContext2D();

        gc.clearRect(0, 0, width, height);

        //Fill the block depending on whether piece can be played or not
        if (canPlay) {
            gc.setFill(Color.color(0.56, 0.93, 0.56, 0.4));
        } else {
            gc.setFill(Color.color(1, 0.44, 0.44, 0.4));
        }

        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
        gc.fillRect(0,0, width, height);
    }

    /**
     * Paints GameBlock in its normal state without hovering effect.
     */
    public void resetHovered() {
        paint();
    }

    /**
     * Used when lines are being cleared. using an AnimationTimer object, we slowly reduce the opacity until it
     * reaches 0.2.
     */
    public void fadeOut() {
        timer = new AnimationTimer() {
            double opacity = 1;
            @Override
            public void handle(long l) {
                paintEmpty();
                opacity -= 0.03;
                if (opacity <= 0.2) {
                    this.stop();
                    timer=null;
                } else {
                    var gc = getGraphicsContext2D();
                    gc.setFill(Color.color(0.196, 0.78, 0.78, opacity));
                    gc.fillRect(0, 0, width, height);
                }
            }
        };

        timer.start();
    }



}
