package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MazeDisplayer extends Canvas {
    private Maze maze;
    private Solution solution;

    // player position:
    private int playerRow = 0;
    private int playerCol = 0;

    private MyViewModel  viewModel;

    // wall, player, and goal images:
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();
    StringProperty imageFileNameSolvedWay = new SimpleStringProperty();

    private double zoomFactor = 1.0;

    public MazeDisplayer() {
        // Add mouse pressed event handler
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MousePress(event);
            }
        });

        // Add mouse dragged event handler
        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MouseDrag(event);
            }
        });


    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public void setPlayerPosition(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
        draw();
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        draw();
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public StringProperty imageFileNameWallProperty() {
        return imageFileNameWall;
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public StringProperty imageFileNamePlayerProperty() {
        return imageFileNamePlayer;
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public void setImageFileNameSolvedWay(String imageFileNameSolutionWay) {
        this.imageFileNameSolvedWay.set(imageFileNameSolutionWay);
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public String getImageFileNameSolvedWay() {
        return imageFileNameSolvedWay.get();
    }

    public StringProperty imageFileNameGoalProperty() {
        return imageFileNameGoal;
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    public void zoomIn() {
        zoomFactor *= 1.1;
        draw();
    }

    public void zoomOut() {
        zoomFactor /= 1.1;
        draw();
    }

    public void drawMaze(Maze maze) {
        this.maze = maze;
        draw();
    }

    private void draw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getRows_number();
            int cols = maze.getColumn_number();

            double cellHeight = canvasHeight / rows * zoomFactor; // Apply zoomFactor
            double cellWidth = canvasWidth / cols * zoomFactor; // Apply zoomFactor

            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            drawMazeWalls(graphicsContext, cellHeight, cellWidth, rows, cols);
            drawPlayer(graphicsContext, cellHeight, cellWidth);
            drawGoal(graphicsContext, cellHeight, cellWidth, rows, cols);
            if (solution != null)
                drawSolvedWay(graphicsContext, cellHeight, cellWidth, rows, cols);
        }
    }


    public boolean is_possible_transition(int row, int col) {
        if (row >= 0 && row < maze.getRows_number() && col >= 0 && col < maze.getColumn_number() && maze != null) {
            return maze.is_possible_transition(row, col);
        } else {
            return false;
        }
    }

    private void drawSolvedWay(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.BLUE);

        Image solveImage = null;
        try {
            solveImage = new Image(new FileInputStream(getImageFileNameSolvedWay()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }
        ArrayList<AState> solutionPath = solution.getSolutionPath();
        solutionPath.remove(0);
        solutionPath.remove(solutionPath.size() - 1);
        for (AState state : solutionPath) {
            double x = ((MazeState) state).getColumn() * cellWidth;
            double y = ((MazeState) state).getRow() * cellHeight;
            if (solveImage == null)
                graphicsContext.fillRect(x, y, cellWidth, cellHeight);
            else
                graphicsContext.drawImage(solveImage, x, y, cellWidth, cellHeight);
        }
        solution = null;
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if (playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }

    private void drawGoal(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        double x = (cols - 1) * cellWidth;
        double y = (rows - 1) * cellHeight;

        Image goalImage = null;
        try {
            goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no goal image file");
        }

        if (goalImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(goalImage, x, y, cellWidth, cellHeight);
    }

    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.RED);

        Image wallImage = null;
        try {
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!maze.is_possible_transition(i, j)) {
                    // if it is a wall:
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if (wallImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    // Handle mouse press event
    private void MousePress(MouseEvent event) {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        viewModel.handleMousePress(event, canvasHeight, canvasWidth);
    }

    // Handle mouse drag event
    private void MouseDrag(MouseEvent event) {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        viewModel.handleMouseDrag(event, canvasHeight, canvasWidth);
    }
}
