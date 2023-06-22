package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.io.File;
import java.util.Observer;

public interface IModel {
    void generateMaze(int cols, int rows);
    Maze getMaze();
    void updatePlayerLocation(MovementDirection direction);
    int getPlayerRow();
    int getPlayerCol();
    void assignObserver(Observer o);
    void solveMaze();
    Solution getSolution();
    void saveGame(String name, String character, String wall, String dir_path, String stage);
    void loadGame(File file);
    String getCharacter_path();
    String getWall_path_image();
    String getStage();
    void exitProgram();
    void aboutDetails();
    void gameBackgroundStory();
    void gameGoal();
    void gameControls();
    void gameplayTips();
    void getProperties();
}
