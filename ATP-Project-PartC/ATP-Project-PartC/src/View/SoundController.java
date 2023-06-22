package View;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundController {
    private static SoundController instance;
    private MediaPlayer backgroundMusic;
    private Map<String, MediaPlayer> soundMap;

    private SoundController() {
        soundMap = new HashMap<>();
    }

    public static SoundController getInstance() {
        if (instance == null) {
            instance = new SoundController();
        }
        return instance;
    }

    public void playBackgroundMusic(String musicFile) {
        try {
            URL resource = getClass().getResource(musicFile);
            if (resource != null) {
                Media music = new Media(resource.toString());
                if (backgroundMusic != null) {
                    backgroundMusic.stop(); // Stop the current background music
                }
                backgroundMusic = new MediaPlayer(music);
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusic.play();
            } else {
                System.out.println("Background music resource not found: " + musicFile);
            }
        } catch (Exception e) {
            System.out.println("Error playing background music: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }

    public void playSound(String soundFile) {
        try {
            URL resource = getClass().getResource(soundFile);
            if (resource != null) {
                Media sound = new Media(resource.toString());
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                soundMap.put(soundFile, mediaPlayer);
                mediaPlayer.play();
            } else {
                System.out.println("Sound resource not found: " + soundFile);
            }
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    public void stopSound(String soundFile) {
        MediaPlayer mediaPlayer = soundMap.get(soundFile);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            soundMap.remove(soundFile);
        }
    }
}
