package View;

import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.ResourceBundle;

public class MyViewController implements Initializable, Observer {
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public TextField file_name_for_saving;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;
    public Label stage;
    public MyViewModel viewModel;
    private Stage mainStage;
    private File file_to_open;
    @FXML
    private Menu exitMenu;
    @FXML
    private Menu about;
    @FXML
    private Menu help;
    private boolean is_backgroundMusic_play = false;


    private SoundController soundController;
    private String path_to_save;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
        this.mazeDisplayer.setViewModel(viewModel);
        this.mazeDisplayer.setImageFileNameGoal("./resources/images/imgbin-buried-treasure-gold-gold-ycuDHwHWKmMTtE8tHtZUzZL93-fotor-bg-remover-20230620173245.png");
        this.mazeDisplayer.setImageFileNameSolvedWay("./resources/images/pin.png");
    }

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    public void set_wall(){
        Random random = new Random();
        int random_number = random.nextInt(0, 8);
        switch (random_number){
            case 0 -> {
                this.mazeDisplayer.setImageFileNameWall("./resources/images/26815297-fotor-bg-remover-20230615231438.png");
            }
            case 1 -> {
                this.mazeDisplayer.setImageFileNameWall("./resources/images/26815298-fotor-bg-remover-20230615231433.png");
            }
            case 2 -> {
                this.mazeDisplayer.setImageFileNameWall("./resources/images/26815302-fotor-bg-remover-20230615231343.png");
            }
            case 3 -> {
                this.mazeDisplayer.setImageFileNameWall("./resources/images/26815303-fotor-bg-remover-20230615231425.png");
            }
            case 4 -> {
                this.mazeDisplayer.setImageFileNameWall("./resources/images/brick-wall-clipart-xl.png");
            }
            case 5 -> {
                this.mazeDisplayer.setImageFileNameWall("./resources/images/red-brick-wall.png");
            }
            case 6 -> {
                this.mazeDisplayer.setImageFileNameWall("./resources/images/rg1024-brick-tile.png");
            }
            case 7 -> {
                this.mazeDisplayer.setImageFileNameWall("./resources/images/WIS12175.png");
            }
        }
    }

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    public void setMazeCharacter(String character_path) {
        mazeDisplayer.setImageFileNamePlayer(character_path);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        soundController = SoundController.getInstance();


        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);

        // Initialize stage label to 1
        stage.setText("1");

        // Add event handler for zoom in/out
        mazeDisplayer.setOnScroll(this::handleScroll);


        exitMenu.setOnAction(event -> exitProgram());

    }

    private void handleScroll(ScrollEvent event) {
        if (event.isControlDown()) {
            double deltaY = event.getDeltaY();

            // Zoom in
            if (deltaY > 0) {
                mazeDisplayer.zoomIn();
            }
            // Zoom out
            else {
                mazeDisplayer.zoomOut();
            }

            event.consume();
        }
    }

    public void generateMaze(ActionEvent actionEvent) {
        if(textField_mazeRows.getText().equals("") || textField_mazeColumns.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No input to maze size");
            alert.setHeaderText("Rows and Columns are not entered");
            alert.setContentText("Please enter the number of rows and columns!");
            alert.show();
            return;
        }
        set_wall();
        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());
        viewModel.generateMaze(cols, rows);
        mazeDisplayer.requestFocus();
        if(!is_backgroundMusic_play) {
            soundController.stopBackgroundMusic();
            soundController.playBackgroundMusic("/sounds/Wallpaper.mp3");
            is_backgroundMusic_play = true;
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Solving maze...");
        alert.show();
        viewModel.solveMaze();
        alert.setContentText("Maze Solved!");
        alert.show();
    }

    public void openFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null);
        file_to_open = chosen;
    }

    public void getPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose location to save maze");
        directoryChooser.setInitialDirectory(new File("./resources"));
        File chosen = directoryChooser.showDialog(null);
        path_to_save = chosen.getPath();
    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent);
        keyEvent.consume();
    }

    public void setPlayerPosition(int row, int col) {
        if(row == viewModel.getMaze().getRows_number() - 1 && col == viewModel.getMaze().getColumn_number() - 1){
            soundController.playSound("/sounds/mixkit-casino-bling-achievement-2067.mp3");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Congratulations! You solved the maze successfully!");
            alert.show();
            generateMaze(new ActionEvent());

            // Increment the stage by 1
            int currentStage = Integer.parseInt(stage.getText());
            int nextStage = currentStage + 1;
            stage.setText(String.valueOf(nextStage));
        }
        else {
            if (row != 0 || col != 0) {
                soundController.playSound("/sounds/mixkit-explainer-video-game-alert-sweep-236.mp3");
            }
            mazeDisplayer.setPlayerPosition(row, col);
            setUpdatePlayerRow(row);
            setUpdatePlayerCol(col);
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }


    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "maze generated" -> mazeGenerated();
            case "player moved" -> playerMoved();
            case "maze solved" -> mazeSolved();
            case "maze loaded" -> mazeLoaded();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    private void mazeLoaded() {
        this.mazeDisplayer.setImageFileNameWall(viewModel.getWallImageFromLoad());
        this.mazeDisplayer.setImageFileNamePlayer(viewModel.getCharacterImageFromLoad());
        mazeDisplayer.drawMaze(viewModel.getMaze());
    }

    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
    }

    private void playerMoved() {
        setPlayerPosition(viewModel.getPlayerRow(), viewModel.getPlayerCol());
    }

    private void mazeGenerated() {
        mazeDisplayer.drawMaze(viewModel.getMaze());
    }

    @FXML
    public void Save(){
        if(file_name_for_saving.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Saving Error");
            alert.setHeaderText("Maze Name has not been entered");
            alert.setContentText("Please enter a file name for saving.");
            alert.show();
            return;
        }
        getPath();
        viewModel.Save(file_name_for_saving.getText(), mazeDisplayer.getImageFileNamePlayer(), mazeDisplayer.getImageFileNameWall(), path_to_save, stage.getText());
    }

    @FXML
    public void Load(){
        openFile();
        viewModel.Load(file_to_open);
        this.stage.setText(viewModel.getStageFromLoad());
        if(!is_backgroundMusic_play){
            soundController.stopBackgroundMusic();
            soundController.playBackgroundMusic("/sounds/Wallpaper.mp3");
            is_backgroundMusic_play = true;
        }
    }

    @FXML
    public void exitProgram() {
        // Stop any playing sounds
        soundController.stopBackgroundMusic();
        // Exit the program
        viewModel.exit();
    }

    public void aboutDetails(){
        viewModel.about();
    }

    public void gameBackgroundStory() {
       viewModel.backgroundStory();
    }

    public void gameGoal() {
        viewModel.goal();
    }

    public void gameControls() {
        viewModel.controls();
    }

    public void gameplayTips() {
        viewModel.tips();
    }

    public void gameProperties() {
        viewModel.properties();
    }
}
