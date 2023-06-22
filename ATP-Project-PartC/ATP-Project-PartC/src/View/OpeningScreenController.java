package View;

import Model.IModel;
import ViewModel.MyViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OpeningScreenController implements Initializable {

    private Stage primaryStage;
    @FXML
    private ChoiceBox<String> characterChoiceBox;
    @FXML
    private ImageView selectedCharacterImageView;
    @FXML
    private ImageView background_openScreen;
    @FXML
    private Button startGameButton;
    private String selectedCharacter;
    private String characterPath;
    private SoundController soundController;

    public String getSelectedCharacter() {
        return selectedCharacter;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        soundController = SoundController.getInstance();
        characterChoiceBox.getItems().addAll("Pikachu", "Charmander", "Squirtle", "Jigglypuff", "Eevee", "Bulbasaur", "Charizard", "Meowth", "Jolteon");
        characterChoiceBox.setOnAction(event -> setSelectedCharacter(characterChoiceBox.getValue()));
        startGameButton.setStyle("-fx-background-color: #7FB882;");
        startGameButton.setPrefWidth(80);
        startGameButton.setText("Start Game");
        characterChoiceBox.setStyle("-fx-background-color: #CFD8DC");
    }

    public void setSelectedCharacter(String character) {

        selectedCharacter = character;
        showSelectedCharacter();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void showSelectedCharacter() {
        if (selectedCharacter != null) {
            try {
                characterPath = "/images/" + selectedCharacter + ".png";
                String imagePath = getClass().getResource(characterPath).toExternalForm();
                Image characterImage = new Image(imagePath);

                selectedCharacterImageView.setFitHeight(300);
                selectedCharacterImageView.setFitWidth(280);

                selectedCharacterImageView.setImage(characterImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void openOtherWindow() {
        if (characterChoiceBox.getValue() != null) {
            // Close the current window
            Stage currentStage = (Stage) characterChoiceBox.getScene().getWindow();

            // Open the other window
            try {
                // Load the FXML file for the other window
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MyView.fxml"));
                Parent root = loader.load();


                IModel model = new Model.MyModel();
                MyViewModel myViewModel = new MyViewModel(model);
                // Get the controller for the other window
                MyViewController my_view = loader.getController();
                // Pass any required data to the controller

                my_view.setViewModel(myViewModel);
                my_view.setMazeCharacter("./resources" + characterPath);

                currentStage.close();

                // Create a new stage for MyViewController
                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                // Set the width and height of the window
                stage.setWidth(1220);  // Set the desired width
                stage.setHeight(812); // Set the desired heigh

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!");
            alert.setHeaderText("Error!");
            alert.setContentText("You have to choice a character to play with in the maze game");

            // Show the alert and wait for user response
            alert.showAndWait();
        }
    }

    public String getCharacterPath() {
        return characterPath;
    }
}
