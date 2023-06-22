package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.*;
import Server.ServerStrategyGenerateMaze;
import Client.IClientStrategy;
import Server.ServerStrategySolveSearchProblem;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import Server.Configurations;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


public class MyModel extends Observable implements IModel {
    private Maze maze;
    private int playerRow;
    private int playerCol;
    private Solution solution;
    private MyMazeGenerator generator;
    private SavableGame savableGame;
    private String character_path;
    private String wall_path_image;
    private String stage;
    private int threadPoolSize;
    private String generate_algorithm;
    private String search_algorithm;
//    private final Logger LOG = LogManager.getLogger();

    public MyModel() {

        generator = new MyMazeGenerator();
        Configurations configurations = Configurations.getInstance();
        this.generate_algorithm = configurations.getMazeGeneratingAlgorithm();
        this.search_algorithm = configurations.getMazeSearchingAlgorithm();
        this.threadPoolSize = configurations.getThreadPoolSize();
    }

    @Override
    public void generateMaze(int cols, int rows) {
        Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        mazeGeneratingServer.start();

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{cols, rows};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[100000000];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        maze.print();
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
//        LOG.info("Maze generated");
        setChanged();
        notifyObservers("maze generated");
        // start position:
        movePlayer(0, 0);
    }

    @Override
    public Maze getMaze() {
        return this.maze;
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        switch (direction) {
            case UP -> {
                if (playerRow > 0 && maze.is_possible_transition(playerRow - 1, playerCol)) {
                    movePlayer(playerRow - 1, playerCol);
//                    LOG.info("Player moved up");
                }
            }
            case DOWN -> {
                if (playerRow < maze.getRows_number() - 1 && maze.is_possible_transition(playerRow + 1, playerCol)) {
                    movePlayer(playerRow + 1, playerCol);
//                    LOG.info("Player moved down");
                }
            }
            case LEFT -> {
                if (playerCol > 0 && maze.is_possible_transition(playerRow, playerCol - 1)) {
                    movePlayer(playerRow, playerCol - 1);
//                    LOG.info("Player moved left");
                }
            }
            case RIGHT -> {
                if (playerCol < maze.getColumn_number() - 1 && maze.is_possible_transition(playerRow, playerCol + 1)) {
                    movePlayer(playerRow, playerCol + 1);
//                    LOG.info("Player moved right");
                }
            }
            case UP_LEFT -> {
                if (playerRow > 0 && playerCol > 0 && maze.is_possible_transition(playerRow - 1, playerCol - 1) && (maze.is_possible_transition(playerRow - 1, playerCol) || maze.is_possible_transition(playerRow, playerCol - 1))) {
                    movePlayer(playerRow - 1, playerCol - 1);
//                    LOG.info("Player moved up left");
                }
            }
            case UP_RIGHT -> {
                if (playerRow > 0 && playerCol < maze.getColumn_number() - 1 && maze.is_possible_transition(playerRow - 1, playerCol + 1) && (maze.is_possible_transition(playerRow - 1, playerCol) || maze.is_possible_transition(playerRow, playerCol + 1))) {
                    movePlayer(playerRow - 1, playerCol + 1);
//                    LOG.info("Player moved up right");
                }
            }
            case DOWN_LEFT -> {
                if (playerRow < maze.getRows_number() - 1 && playerCol > 0 && maze.is_possible_transition(playerRow + 1, playerCol - 1) && (maze.is_possible_transition(playerRow + 1, playerCol) || maze.is_possible_transition(playerRow, playerCol - 1))) {
                    movePlayer(playerRow + 1, playerCol - 1);
//                    LOG.info("Player moved down left");
                }
            }
            case DOWN_RIGHT -> {
                if (playerRow < maze.getRows_number() - 1 && playerCol < maze.getColumn_number() - 1 && maze.is_possible_transition(playerRow + 1, playerCol + 1) && (maze.is_possible_transition(playerRow + 1, playerCol) || maze.is_possible_transition(playerRow, playerCol + 1))) {
                    movePlayer(playerRow + 1, playerCol + 1);
//                    LOG.info("Player moved down right");
                }
            }
        }

    }

    private void movePlayer(int row, int col){
        this.playerRow = row;
        this.playerCol = col;
//        LOG.info("Player moved to: " + row + ", " + col);
        setChanged();
        notifyObservers("player moved");
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveMaze() {
        Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze);
                        toServer.flush();
                        solution = (Solution)fromServer.readObject();
                        if(solution != null) {
                            System.out.println(String.format("Solution steps: %s", solution));
                            ArrayList<AState> mazeSolutionSteps = solution.getSolutionPath();


                            for (int i = 0; i < mazeSolutionSteps.size(); ++i) {
                                System.out.println(String.format("%s. %s", i, ((AState) mazeSolutionSteps.get(i)).toString()));
                            }
                        }
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
//        LOG.info("Maze solved");
        setChanged();
        notifyObservers("maze solved");
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public void loadGame(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            savableGame = (SavableGame) objectInputStream.readObject();
            this.maze = (Maze)savableGame.getGame();
            int[] position = (int[]) savableGame.getPosition();
            movePlayer(position[1], position[0]);
            this.character_path = savableGame.getCharacter_path();
            this.wall_path_image = savableGame.getWall_path_image();
            this.stage = savableGame.getStage();
//            LOG.info("Maze File has been loaded");
            setChanged();
            notifyObservers("maze loaded");
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveGame(String name, String character, String wall, String dir, String stage) {
        int[] position = {playerCol, playerRow};
        savableGame = new SavableGame(maze, position, name, character, wall, stage);
        String path = dir + File.separator + savableGame.getGameName() + ".maze";
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(savableGame);
//            LOG.info("Maze File has been saved");
            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getCharacter_path() {
        return character_path;
    }

    @Override
    public String getWall_path_image() {
        return wall_path_image;
    }

    @Override
    public String getStage() {
        return stage;
    }

    @Override
    public void exitProgram() {
//        LOG.info("The Game window has been closed by the player");
        // Exit the program
        Platform.exit();

        // Terminate the Java Virtual Machine
        System.exit(0);
    }

    @Override
    public void aboutDetails(){
//        LOG.info("The about details has been shown to the player");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("About Information");
        alert.setTitle("About");
        alert.setContentText("This is a maze game developed by: \n" + "Oded Atias \n" + "Gal Levi");
        alert.show();
    }

    @Override
    public void gameBackgroundStory() {
//        LOG.info("The background story has been shown to the player");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Background Story: The Maze Runner");
        alert.setTitle("Game Instructions");

        String instructions = "Welcome to the Maze Adventure! You find yourself trapped in a mysterious maze filled with twists, turns, and challenging puzzles. Legend has it that deep within the heart of the maze lies a hidden treasure that holds unimaginable wealth and power. You, as the brave explorer, have decided to embark on this perilous journey to claim the treasure and become a legend yourself. But beware, the maze is guarded by supernatural forces and cunning traps. Only the most cunning and determined can navigate through the maze and emerge victorious. Are you ready to test your skills and unravel the secrets of the maze?\n\n";

        alert.setContentText(instructions);
        alert.show();
    }

    @Override
    public void gameGoal() {
//        LOG.info("The game goal has been shown to the player");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Objective: Treasure Hunt");
        alert.setTitle("Game Instructions");

        String instructions = "Your objective is to navigate through the maze, overcome obstacles, and reach the treasure hidden deep within. Along the way, you'll encounter walls that block your path. Use your wit, problem-solving skills, and a little bit of luck to overcome these obstacles and find the treasure.\n\n";

        alert.setContentText(instructions);
        alert.show();
    }

    @Override
    public void gameControls() {
//        LOG.info("The game controls has been shown to the player");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Controls");
        alert.setTitle("Game Instructions");

        String instructions = "1. Generate Maze: Before starting the game, make sure to generate the maze by clicking the 'Generate Maze' button in the game interface. This will create a new maze layout for you to explore.\n\n" +
                "2. Movement: Use the arrow keys or WASD keys to move the player character within the maze. You can also use the mouse to move the player character within the maze by dragging and dropping the player character or clicking near the player.\n\n" +
                "3. Zoom In/Out: Use the scroll wheel on your mouse to zoom in or out of the maze for a better view. (hold ctrl button + scroll the wheel of the mouse)\n\n" +
                "4. Solving the Maze: If you're feeling stuck and need some assistance, you can click the 'Solve Maze' button located in the game interface to reveal the optimal path to the treasure. However, use this option sparingly, as it may diminish your sense of achievement.\n\n" +
                "5. Save/Load: You can save your progress at any time by clicking the 'Save' button located in the game interface (notice: You have to enter a maze name for saving the maze), this allows you to continue your adventure from where you left off.\nTo load a saved game, click the 'Load' button and select the desired saved file (.maze format only).\n\n" +
                "6. Exit Game: To exit the game, simply click the 'Exit' button located in the game interface or close the application window.\n\n";
        String decoratedInstructions = instructions;

        alert.setContentText(decoratedInstructions);
        alert.show();
    }



    @Override
    public void gameplayTips() {
//        LOG.info("The game tips has been shown to the player");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Gameplay Tips");
        alert.setTitle("Game Instructions");

        String instructions = "Explore and Observe: Take your time to explore the maze and observe the surroundings. Look for clues, hidden paths, and objects that might aid you in your journey.\n" +
                "Map Awareness: Pay attention to the mini-map displayed on the screen. It provides an overview of the maze and your current position. Use it to plan your route and avoid unnecessary detours.\n" +
                "Collect Keys: Throughout the maze, you'll come across locked doors. Look for keys hidden in chests, behind obstacles, or guarded by enemies. Collect these keys to unlock doors and progress further.\n" +
                "Avoid Traps and Enemies: Beware of traps and enemies lurking in the maze. Traps can be triggered by stepping on pressure plates or interacting with certain objects. Enemies may block your path or engage in battles. Plan your moves carefully to avoid unnecessary harm.\n" +
                "Trial and Error: Don't be discouraged by setbacks. Maze navigation requires trial and error. If you encounter a dead-end, retrace your steps and try a different path. Persistence is key to conquering the maze.\n" +
                "Celebrate Milestones: Each time you reach a new stage or overcome a significant challenge, take a moment to celebrate your progress. It's a testament to your determination and problem-solving skills.\n" +
                "Immerse Yourself: Let yourself be immersed in the atmosphere of the maze. Enjoy the ambient sounds, captivating visuals, and the sense of adventure. Remember, it's not only about reaching the treasure but also the journey itself.\n\n";

        alert.setContentText(instructions);
        alert.show();
    }

    @Override
    public void getProperties(){
//        LOG.info("The properties has been shown to the player");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Properties");
        alert.setTitle("Properties");
        alert.setContentText("ThreadPool Size: " + this.threadPoolSize + "\n"
                + "Maze Generating Algorithm: " + this.generate_algorithm + "\n"
                + "Maze Searching Algorithm: " + this.search_algorithm + "\n");
        alert.show();
    }
}
