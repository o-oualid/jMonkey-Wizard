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
        primaryStage.setWidth(500);
        primaryStage.setHeight(650);
        primaryStage.setResizable(false);
       // primaryStage.getIcons().add(new Image("/icons/icon.png"));
        primaryStage.show();
       // primaryStage.setAlwaysOnTop(true);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Dependencies.fxml"));
            dependencies.setTitle("More Dependencies");
            dependencies.setResizable(false);
            dependencies.initOwner(primaryStage);
            dependencies.setScene(new Scene(root));
            dependencies.setWidth(600);
            dependencies.setHeight(300);
            //dependencies.getIcons().add(new Image("/icons/icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}