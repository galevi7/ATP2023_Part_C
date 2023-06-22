package Model;

import java.io.Serializable;

public class SavableGame implements Serializable {
    private Object game;
    private Object position;
    private String gameName;
    private String character_path;
    private String wall_path_image;

    private String stage;

    public SavableGame(Object game, Object position, String gameName, String character, String wall_image, String stage) {
        this.game = game;
        this.position = position;
        this.gameName = gameName;
        this.character_path = character;
        this.wall_path_image = wall_image;
        this.stage = stage;
    }

    public Object getGame() {
        return game;
    }

    public Object getPosition() {
        return position;
    }

    public String getGameName() {
        return gameName;
    }

    public String getCharacter_path() {
        return character_path;
    }

    public String getWall_path_image() {
        return wall_path_image;
    }

    public String getStage() {
        return stage;
    }
}
