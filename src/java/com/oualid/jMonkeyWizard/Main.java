package com.oualid.jMonkeyWizard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    static Stage dependencies=new Stage();
    @Override
    public void start(Stage primaryStage) {
        Controller controller = new Controller();
        primaryStage.setScene(new Scene(controller));
        primaryStage.setTitle("jMonkey Wizard");
        primaryStage.setResizable(false);
        primaryStage.show();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/Dependencies.fxml"));
            dependencies.setTitle("Hello World");
            dependencies.setResizable(false);
            dependencies.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}