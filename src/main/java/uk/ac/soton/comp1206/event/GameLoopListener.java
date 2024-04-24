package uk.ac.soton.comp1206.event;

/**
 * This class handles the event of the Game Loop. Called when timer runs out and a piece hasn't been played in time.
 */
public interface GameLoopListener {
    /**
     * Called when the timer has run out and a life is lost.
     */
    void gameLoopTriggered();
}
