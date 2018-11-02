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
    static ProjectBuilder projectBuilder;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        App.primaryStage = primaryStage;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent parent = fxmlLoader.load(getClass().getClassLoader().getResource("gui/MainUI.fxml").openStream());
            MainUi uiController = fxmlLoader.getController();
            primaryStage.setTitle("jMonkey Wizard");
            primaryStage.setScene(new Scene(parent));
            primaryStage.setResizable(false);
            //primaryStage.getIcons().add(new Image("/icons/icon.png"));
            primaryStage.show();
            // primaryStage.setAlwaysOnTop(true);

            fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getClassLoader().getResource("gui/Dependencies.fxml").openStream());
            Dependencies dependenciesController = fxmlLoader.getController();
            dependencies = new Stage();
            dependencies.setTitle("More Dependencies");
            dependencies.setResizable(false);
            dependencies.initOwner(primaryStage);
            dependencies.setScene(new Scene(root));
            //dependencies.getIcons().add(new Image("/icons/icon.png"));
            if (uiController==null){
                System.out.println("ui controller is NULL");
            }
            projectBuilder = new ProjectBuilder(uiController,dependenciesController);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}