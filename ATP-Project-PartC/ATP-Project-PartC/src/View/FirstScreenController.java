package View;

import Model.IModel;
import ViewModel.MyViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FirstScreenController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button startNewGameButton;

    @FXML
    private Button loadOldGameButton;

    @FXML
    private Button exitButton;

    @FXML
    private ImageView imageView;

    @FXML
    private ImageView backgroundImage;

    private SoundController soundController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        Font font = Font.font("Arial", 24);
//        startNewGameButton.setFont(font);
//        loadOldGameButton.setFont(font);
//        exitButton.setFont(font);
//        startNewGameButton.setStyle("-fx-background-color: blue;");
        soundController = SoundController.getInstance();
        soundController.playBackgroundMusic("/sounds/onlymp3.to - The Maze Runner Soundtrack - 01 The Maze Runner-dI25KCY9-Gg-192k-1687169713.mp3");


        // Load and set the image
        Image image = new Image("/images/678-6787093_maze-runner-png.png"); // Replace "your_image.png" with the actual path or URL of your PNG image
        imageView.setImage(image);

        // Set the dimensions of the image view
        imageView.setFitWidth(300); // Set the desired width
        imageView.setFitHeight(160); // Set the desired height
    }

    @FXML
    private void startNewGame(ActionEvent event) {
        // Get the current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();

        try {
            // Create a new stage for the OpeningScreenController
            Stage primaryStage = new Stage();

            // Set the window size
            primaryStage.setWidth(800); // Set the desired width
            primaryStage.setHeight(500); // Set the desired height

            // Load the FXML file for the OpeningScreen window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OpeningScreen.fxml"));
            Parent openingScreenRoot = loader.load();

            // Get the controller for the OpeningScreen window
            OpeningScreenController openingScreenController = loader.getController();
            openingScreenController.setPrimaryStage(primaryStage);

            Scene openingScreenScene = new Scene(openingScreenRoot);
            primaryStage.setScene(openingScreenScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    @FXML
    private void loadOldGame(ActionEvent event) {
        System.out.println("Load Old Game From File clicked");
        // Add your code to load the old game from file or perform related actions

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Open the other window
        try {
            // Load the FXML file for the other window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MyView.fxml"));
            Parent root = loader.load();

            IModel model = new Model.MyModel();
            MyViewModel myViewModel = new MyViewModel(model);
            // Get the controller for the other window
            MyViewController myView = loader.getController();
            // Pass any required data to the controller

            myView.setViewModel(myViewModel);
            myView.Load();
            myView.mazeDisplayer.requestFocus();
            currentStage.close();

            // Create a new stage for MyViewController
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void exit(ActionEvent event) {
        System.out.println("Exit clicked");
        // Add your code to close the application
        System.exit(0);
    }

    public void show(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FirstScreen.fxml"));
            AnchorPane pane = loader.load();

            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
