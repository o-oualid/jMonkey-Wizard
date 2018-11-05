package com.oualid.jmonkeywizard;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.util.ArrayList;

public class DependenciesController {
    @FXML
    GridPane map;

    protected ArrayList<Dependency> dependencies = new ArrayList<>();
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    @FXML
    public void initialize() {
        loadDependencies();
    }

    @FXML
    void ok() {
        App.dependencies.hide();
    }

    @FXML
    void addCustomDependency() {
        App.dependencyUIController.index = -1;
        App.dependency.show();
    }

    void addDependency(Dependency dependency) {
        dependencies.add(dependency);
        showDependency(dependency);

    }

    private void showDependency(Dependency dependency) {
        CheckBox checkBox = new CheckBox(dependency.name);
        checkBoxes.add(checkBox);
        int row = dependencies.indexOf(dependency) + 1;
        map.add(checkBox, 0, row);
        map.add(new Label(dependency.group), 1, row);
        map.add(new Label(dependency.version), 2, row);
        map.add(new Label(dependency.license), 3, row);
        map.add(new Label(dependency.platform.name()), 4, row);

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> {
            edit(row - 1);
        });
        map.add(editButton, 5, row);

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            deleteDependency(row - 1);
        });
        map.add(deleteButton, 6, row);
    }

    @FXML
    private void saveDependencies() {
        try {
            FileOutputStream fos = new FileOutputStream("dependencies");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dependencies);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void loadDependencies() {
        try {
            FileInputStream fis = new FileInputStream("dependencies");
            ObjectInputStream ois = new ObjectInputStream(fis);
            dependencies = (ArrayList<Dependency>) ois.readObject();
            ois.close();
            fis.close();
            for (Dependency dependency : dependencies)
                showDependency(dependency);
        } catch (IOException ioe) {
            addDefaultDependencies();
            ioe.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }

    }

    private void deleteDependency(int index) {
        dependencies.remove(index);
        checkBoxes.remove(index);
        map.getChildren().clear();
        for (Dependency dependency : dependencies)
            showDependency(dependency);
    }

    @FXML
    private void edit(int index) {
        App.dependencyUIController.index = index;
        App.dependencyUIController.init(dependencies.get(index));
        App.dependency.show();
    }

    ArrayList<Dependency> getSelectedDependencies() {
        ArrayList<Dependency> selectedDependencies = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++)
            if (checkBoxes.get(i).isSelected())
                selectedDependencies.add(dependencies.get(i));
        return selectedDependencies;
    }


    private void addDefaultDependencies() {
        addDependency(new Dependency("lemur", "com.simsilica",
                "1.10.1", "BSD New", Dependency.Platform.ALL,
                "maven { url \"https://dl.bintray.com/simsilica/Sim-tools\" }"));

        addDependency(new Dependency("SkyControl", "jme3utilities",
                "0.9.14", "BSD New", Dependency.Platform.ALL,
                "maven { url \"https://dl.bintray.com/stephengold/jme3utilities\" }"));

        addDependency(new Dependency("jfx", "com.jme3", "2.0.0", "Apache-2.0",
                Dependency.Platform.ALL, "maven { url \"https://dl.bintray.com/javasabr/maven\" }"));
    }

    void refreshMap() {
        map.getChildren().clear();
        for (Dependency dependency : dependencies)
            showDependency(dependency);
    }


}
