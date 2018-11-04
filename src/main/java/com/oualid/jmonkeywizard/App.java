package com.oualid.jmonkeywizard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    //TODO: find better names this is confusing

    static Stage dependencies;
    static Stage dependency;
    static Stage primaryStage;
    static ProjectBuilder projectBuilder;

    static DependencyController dependencyUIController;
    static DependenciesController dependenciesController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        App.primaryStage = primaryStage;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent parent = fxmlLoader.load(getClass().getClassLoader().getResource("gui/MainUI.fxml").openStream());
            MainUiController uiController = fxmlLoader.getController();
            primaryStage.setTitle("jMonkey Wizard");
            primaryStage.setScene(new Scene(parent));
            primaryStage.setResizable(false);
            //primaryStage.getIcons().add(new Image("/icons/icon.png"));
            primaryStage.show();
            // primaryStage.setAlwaysOnTop(true);

            fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getClassLoader().getResource("gui/Dependencies.fxml").openStream());
            dependenciesController = fxmlLoader.getController();
            dependencies = new Stage();
            dependencies.setTitle("More Dependencies");
            dependencies.setResizable(true);
            dependencies.initOwner(primaryStage);
            dependencies.setScene(new Scene(root));
            dependencies.setResizable(false);
            //dependencies.getIcons().add(new Image("/icons/icon.png"));

            fxmlLoader = new FXMLLoader();
            Parent DependencyUIRoot = fxmlLoader.load(getClass().getClassLoader().getResource("gui/DependencyUI.fxml").openStream());
            dependencyUIController = fxmlLoader.getController();
            dependency = new Stage();
            dependency.setTitle("Add Dependency");
            dependency.setResizable(true);
            dependency.setResizable(false);
            dependency.initOwner(dependencies);
            dependency.setScene(new Scene(DependencyUIRoot));
            //dependency.getIcons().add(new Image("/icons/icon.png"));

            projectBuilder = new ProjectBuilder(uiController, dependenciesController);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}