package uk.ac.soton.comp1206.event;

/**
 * This class handles the game ended event.
 */
public interface GameEndedListener {
    /**
     * Called when all lives have been lost and the game comes to a close.
     */
    void gameEnded();
}
