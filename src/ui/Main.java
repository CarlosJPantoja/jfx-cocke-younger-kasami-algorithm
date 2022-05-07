/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @Authors: Juan Esteban Caicedo and Carlos Jimmy Pantoja.
 * @Date: May, 15th 2022
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/
package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    /**
	 * Name: main <br>
	 * <br> Main method. <br>
	 * @param args - Java command line arguments - args = String[]
	*/
    public static void main(String[] args) {
        launch(args);
    }

    /**
	 * Name: start <br>
	 * <br> GUI start method. <br>
	 * @param primaryStage - GUI primary stage - primaryStage = Stage
	 * @throws Exception - to indicate the conditions this program might want to catch.
	*/
	@Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CYK Algorithm");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}