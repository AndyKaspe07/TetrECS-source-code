package uk.ac.soton.comp1206.scene;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class Multimedia {

    private MediaPlayer audioPlayer;
    private MediaPlayer musicPlayer;

    private void playAudio(String path) {
        Media media = new Media(new File(path).toURI().toString());
        audioPlayer = new MediaPlayer(media);
        audioPlayer.play();
    }

    /**
     * Initiates a new MediaPlayer object and plays the appropriate muscic.
     * @param path Path of the music file
     * @return Returns the MediaPlayer object.
     */
    private MediaPlayer playMusic(String path) {
        Media media = new Media(new File(path).toURI().toString());
        musicPlayer = new MediaPlayer(media);
        musicPlayer.setStartTime(Duration.seconds(1.0));

        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        musicPlayer.play();

        return musicPlayer;
    }

    /**
     * Method called to play the sound when a piece has been placed on the board
     */
    public void successfulPlacement() {
        playAudio("src/main/resources/sounds/place.wav");
    }

    /**
     * Piece was unable to placed. A different sound is played
     */
    public void unsuccessfulPlacement() {
        playAudio("src/main/resources/sounds/fail.wav");
    }

    /**
     * This method plays audio when a line has been cleared.
     */
    public void linesCleared() {
        playAudio("src/main/resources/sounds/clear.wav");
    }
    /**
     * This method plays audio when the game loop has been triggered and a life is lost.
     */
    public void lifeLost() {playAudio("src/main/resources/sounds/lifelose.wav");}
    /**
     * This method plays the ChallengeScene music.
     */
    public MediaPlayer playGameMusic() {
        return playMusic("src/main/resources/music/game.wav");
    }
    /**
     * This method plays the MenuScene music.
     */
    public MediaPlayer playMenuMusic() {
        return playMusic("src/main/resources/music/menu.mp3");
    }

    /**
     * This method plays audio when a selection has been made in the menu scene and a new scene is being loaded.
     */
    public void playButtonClick() {
        playAudio("src/main/resources/sounds/transition.wav");
    }
}
