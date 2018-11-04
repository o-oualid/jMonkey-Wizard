package com.oualid.jmonkeywizard;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.util.HashMap;

public class DependenciesController {
    @FXML
    GridPane map;

    static HashMap<CheckBox, Dependency> dependencies = new HashMap<>();

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
        App.dependency.show();
    }

    void addDependency(Dependency dependency) {
        CheckBox checkBox = new CheckBox(dependency.name);
        dependencies.put(checkBox, dependency);
        int row = dependencies.keySet().size();

        map.add(checkBox, 0, row);
        map.add(new Label(dependency.group), 1, row);
        map.add(new Label(dependency.version), 2, row);
        map.add(new Label(dependency.license), 3, row);
        map.add(new Label(dependency.platform.name()), 4, row);
        saveDependencies();
    }

    private void saveDependencies() {
        try {
            FileOutputStream fos = new FileOutputStream("dependencies");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dependencies.values().toArray());
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void loadDependencies() {
        Object[] dependencies;
        try {
            FileInputStream fis = new FileInputStream("dependencies");
            ObjectInputStream ois = new ObjectInputStream(fis);
            dependencies = (Object[]) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            addDependency(new Dependency("lemur", "com.simsilica",
                    "1.10.1", "BSD New", Dependency.Platform.ALL,
                    "maven { url \"https://dl.bintray.com/simsilica/Sim-tools\" }"));

            addDependency(new Dependency("SkyControl", "jme3utilities",
                    "0.9.14", "BSD New", Dependency.Platform.ALL,
                    "maven { url \"https://dl.bintray.com/stephengold/jme3utilities\" }"));

            addDependency(new Dependency("jfx", "com.jme3", "2.0.0", "Apache-2.0",
                    Dependency.Platform.ALL, "maven { url \"https://dl.bintray.com/javasabr/maven\" }"));
            ioe.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }
        for (Object dependency : dependencies) {

            addDependency((Dependency) dependency);
        }
    }

}
