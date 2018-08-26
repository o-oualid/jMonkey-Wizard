package com.oualid.jMonkeyWizard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    static Stage dependencies = new Stage();
    static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        try {
            Parent primaryStageRoot = FXMLLoader.load(getClass().getResource("/gui/MainUI.fxml"));
            primaryStage.setTitle("jMonkey Wizard");
            primaryStage.setScene(new Scene(primaryStageRoot));
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // primaryStage.getIcons().add(new Image("/icons/icon.png"));
        primaryStage.show();
        // primaryStage.setAlwaysOnTop(true);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Dependencies.fxml"));
            dependencies.setTitle("More Dependencies");
            dependencies.setResizable(false);
            dependencies.initOwner(primaryStage);
            dependencies.setScene(new Scene(root));
            //dependencies.getIcons().add(new Image("/icons/icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}