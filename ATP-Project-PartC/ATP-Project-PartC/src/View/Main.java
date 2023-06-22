package View;

// MainApplication.java

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the first screen FXML file
        FXMLLoader firstScreenLoader = new FXMLLoader(getClass().getResource("FirstScreen.fxml"));
        Parent firstScreenRoot = firstScreenLoader.load();

        // Create the first screen controller
        FirstScreenController firstScreenController = firstScreenLoader.getController();
        firstScreenController.show(primaryStage);

        // Set the first screen as the root of the scene
        Scene firstScreenScene = new Scene(firstScreenRoot);
        primaryStage.setScene(firstScreenScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
