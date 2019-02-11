package com.oualid.jmonkeywizard;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    static ProjectBuilder projectBuilder;

    static Stage dependencies;
    static Stage dependency;
    private List<String> list = new ArrayList<>();
    static Stage primaryStage;

    static DependencyController dependencyController;
    static DependenciesController dependenciesController;

    static void startApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        App.primaryStage = primaryStage;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent parent = fxmlLoader.load(getClass().getResource("/gui/Main.fxml").openStream());
            MainUiController uiController = fxmlLoader.getController();
            primaryStage.setTitle("jMonkey Wizard");
            primaryStage.setScene(new Scene(parent));
            primaryStage.setResizable(false);
            primaryStage.getIcons().add(new Image("/icons/icon.png"));
            primaryStage.show();
            // primaryStage.setAlwaysOnTop(true);

            fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("/gui/Dependencies.fxml").openStream());
            dependenciesController = fxmlLoader.getController();
            dependencies = new Stage();
            dependencies.setTitle("More Dependencies");
            dependencies.setResizable(true);
            dependencies.initOwner(primaryStage);
            dependencies.setScene(new Scene(root));
            dependencies.setResizable(false);
            dependencies.getIcons().add(new Image("/icons/icon.png"));

            fxmlLoader = new FXMLLoader();
            Parent DependencyUIRoot = fxmlLoader.load(getClass().getResource("/gui/Dependency.fxml").openStream());
            dependencyController = fxmlLoader.getController();
            dependency = new Stage();
            dependency.setTitle("Dependency");
            dependency.setResizable(true);
            dependency.setResizable(false);
            dependency.initOwner(dependencies);
            dependency.setScene(new Scene(DependencyUIRoot));
            dependency.getIcons().add(new Image("/icons/icon.png"));

            projectBuilder = new ProjectBuilder(uiController);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}