package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.HashSet;

/**
 * Listener interface used to handle the event where a line is cleared
 */
public interface LineClearedListener {

    /**
     * Handling the event of a line cleared
     * @param coordinates Set of coordinates which need to be removed as a follow-up of the line being cleared.
     */
    void lineCleared(HashSet<GameBlockCoordinate> coordinates);
}
