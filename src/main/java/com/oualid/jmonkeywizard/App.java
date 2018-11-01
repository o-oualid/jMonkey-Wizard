package com.oualid.jmonkeywizard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    static Stage dependencies;
    static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        App.primaryStage = primaryStage;
        try {
            Parent parent = FXMLLoader.load(getClass().getClassLoader().getResource("gui/MainUI.fxml"));
            primaryStage.setTitle("jMonkey Wizard");
            primaryStage.setScene(new Scene(parent));
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //primaryStage.getIcons().add(new Image("/icons/icon.png"));
        primaryStage.show();
        // primaryStage.setAlwaysOnTop(true);
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("gui/Dependencies.fxml"));
            dependencies = new Stage();
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