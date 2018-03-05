package com.oualid.jMonkeyWizard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    static Stage dependencies = new Stage();
    static  Stage primaryStage;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage =primaryStage;
        Controller controller = new Controller();
        primaryStage.setScene(new Scene(controller));
        primaryStage.setTitle("jMonkey Wizard");
        primaryStage.setResizable(false);
       // primaryStage.getIcons().add(new Image("/icons/iconx.png"));
        primaryStage.show();
        primaryStage.setAlwaysOnTop(true);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Dependencies.fxml"));
            dependencies.setTitle("Hello World");
            dependencies.setResizable(false);
            dependencies.initOwner(primaryStage);
            dependencies.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}