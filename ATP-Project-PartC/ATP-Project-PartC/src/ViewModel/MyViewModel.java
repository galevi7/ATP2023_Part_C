package ViewModel;

import Model.IModel;
import Model.MovementDirection;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this); //Observe the Model for it's changes
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public int getPlayerRow() {
        return model.getPlayerRow();
    }

    public int getPlayerCol() {
        return model.getPlayerCol();
    }

    public Solution getSolution() {
        return model.getSolution();
    }


    public void generateMaze(int cols, int rows) {
        model.generateMaze(cols, rows);
        model.solveMaze();
        Solution solution = model.getSolution();
        while(solution == null){
            model.generateMaze(cols, rows);
            model.solveMaze();
            solution = model.getSolution();
        }
        setChanged();
        notifyObservers("maze generated");
    }

    public void movePlayer(KeyEvent keyEvent) {
        MovementDirection direction;
        switch (keyEvent.getCode()) {
            case UP, NUMPAD8 -> direction = MovementDirection.UP;
            case DOWN, NUMPAD2 -> direction = MovementDirection.DOWN;
            case LEFT, NUMPAD4 -> direction = MovementDirection.LEFT;
            case RIGHT, NUMPAD6 -> direction = MovementDirection.RIGHT;
            case NUMPAD7 -> {
                direction = MovementDirection.UP_LEFT;
            }
            case NUMPAD9 -> {
                direction = MovementDirection.UP_RIGHT;
            }
            case NUMPAD1 -> {
                direction = MovementDirection.DOWN_LEFT;
            }
            case NUMPAD3 -> {
                direction = MovementDirection.DOWN_RIGHT;
            }
            default -> {
                // no need to move the player...
                return;
            }
        }
        model.updatePlayerLocation(direction);
    }


    // Handle mouse press event
    public void handleMousePress(MouseEvent event, double canvasHeight,  double canvasWidth) {
        int rows = model.getMaze().getRows_number();
        int cols = model.getMaze().getColumn_number();

        double cellHeight = canvasHeight / rows;
        double cellWidth = canvasWidth / cols;

        int row = (int) (event.getY() / cellHeight);
        int col = (int) (event.getX() / cellWidth);

        if(row >=0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() - 1 && col == getPlayerCol() - 1){
            model.updatePlayerLocation(MovementDirection.UP_LEFT);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() - 1 && col == getPlayerCol() + 1) {
            model.updatePlayerLocation(MovementDirection.UP_RIGHT);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() + 1 && col == getPlayerCol() - 1) {
            model.updatePlayerLocation(MovementDirection.DOWN_LEFT);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() + 1 && col == getPlayerCol() + 1) {
            model.updatePlayerLocation(MovementDirection.DOWN_RIGHT);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() - 1 && col == getPlayerCol()) {
            model.updatePlayerLocation(MovementDirection.UP);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() + 1 && col == getPlayerCol()) {
            model.updatePlayerLocation(MovementDirection.DOWN);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() && col == getPlayerCol() - 1) {
            model.updatePlayerLocation(MovementDirection.LEFT);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() && col == getPlayerCol() + 1) {
            model.updatePlayerLocation(MovementDirection.RIGHT);
        }
    }

    // Handle mouse drag event
    public void handleMouseDrag(MouseEvent event, double canvasHeight,  double canvasWidth) {
        int rows = model.getMaze().getRows_number();
        int cols = model.getMaze().getColumn_number();

        double cellHeight = canvasHeight / rows;
        double cellWidth = canvasWidth / cols;

        int row = (int) (event.getY() / cellHeight);
        int col = (int) (event.getX() / cellWidth);

        if(row >=0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() - 1 && col == getPlayerCol() - 1){
            model.updatePlayerLocation(MovementDirection.UP_LEFT);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() - 1 && col == getPlayerCol() + 1) {
            model.updatePlayerLocation(MovementDirection.UP_RIGHT);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() + 1 && col == getPlayerCol() - 1) {
            model.updatePlayerLocation(MovementDirection.DOWN_LEFT);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() + 1 && col == getPlayerCol() + 1) {
            model.updatePlayerLocation(MovementDirection.DOWN_RIGHT);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() - 1 && col == getPlayerCol()) {
            model.updatePlayerLocation(MovementDirection.UP);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() + 1 && col == getPlayerCol()) {
            model.updatePlayerLocation(MovementDirection.DOWN);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() && col == getPlayerCol() - 1) {
            model.updatePlayerLocation(MovementDirection.LEFT);
        } else if (row >= 0 && row <= rows && col >= 0 && col <= cols && row == getPlayerRow() && col == getPlayerCol() + 1) {
            model.updatePlayerLocation(MovementDirection.RIGHT);
        }
    }

    public void solveMaze() {
        model.solveMaze();
    }

    public void Save(String name, String character_image_path, String wall_image_path, String dir, String stage) {
        model.saveGame(name, character_image_path, wall_image_path, dir, stage);
    }

    public void Load(File file) {
        model.loadGame(file);
    }

    public String getCharacterImageFromLoad() {
        return model.getCharacter_path();
    }

    public String getStageFromLoad() {
        return model.getStage();
    }


    public String getWallImageFromLoad() {
        return model.getWall_path_image();
    }
    public void exit(){
        model.exitProgram();
    }

    public void about(){
        model.aboutDetails();
    }
    public void backgroundStory(){
        model.gameBackgroundStory();
    }

    public void goal() {
        model.gameGoal();
    }

    public void controls() {
        model.gameControls();
    }


    public void tips() {
        model.gameplayTips();
    }

    public void properties(){
        model.getProperties();
    }



}
