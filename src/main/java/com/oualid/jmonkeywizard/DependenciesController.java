package com.oualid.jmonkeywizard;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class DependenciesController {
    @FXML
    GridPane map;

    private List<Dependency> dependencies = new ArrayList<>();

    @FXML
    public void initialize() {
        dependencies.add(new Dependency("jme3-android", "org.jmonkeyengine",
                "3.2.1-stable", "BSD New", Dependency.Platform.ANDROID));
        dependencies.add(new Dependency("jme3-desktop", "org.jmonkeyengine",
                "3.2.1-stable", "BSD New", Dependency.Platform.DESKTOP));
        dependencies.add(new Dependency("jme3-ios", "org.jmonkeyengine",
                "3.2.1-stable", "BSD New", Dependency.Platform.IOS));

        dependencies.add(new Dependency("jme3-core", "org.jmonkeyengine",
                "3.2.1-stable", "BSD New", Dependency.Platform.ALL));
        dependencies.add(new Dependency("jme3-vr", "org.jmonkeyengine",
                "3.2.1-stable", "BSD New", Dependency.Platform.VR));


        dependencies.add(new Dependency("SkyControl", "jme3utilities",
                "0.9.14", "BSD New", Dependency.Platform.VR));
        dependencies.add(new Dependency("lemur", "com.simsilica",
                "1.10.1", "BSD New", Dependency.Platform.VR));

        int row = 1;
        for (Dependency dependency : dependencies) {
            map.add(new CheckBox(dependency.name), 0, row);
            map.add(new Label(dependency.group), 1, row);
            map.add(new Label(dependency.version), 2, row);
            map.add(new Label(dependency.license), 3, row);
            map.add(new Label(dependency.platform.name()), 4, row);
            row++;
        }
    }

    @FXML
    void ok() {
        App.dependencies.hide();
    }

    @FXML
    void addCustomDependency() {
        App.dependency.show();
    }


}
