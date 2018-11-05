package com.oualid.jmonkeywizard;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;


public class DependencyController {

    @FXML
    public TextField name, group, version, licence, repository;
    @FXML
    ComboBox platform;

    int index = -1;

    void init(Dependency dependency) {
        name.setText(dependency.name);
        group.setText(dependency.group);
        version.setText(dependency.version);
        licence.setText(dependency.license);
        repository.setText(dependency.repository);
        platform.setValue(dependency.platform.toString());

    }


    @FXML
    public void initialize() {

    }

    @FXML
    void ok() {
        Dependency dependency = new Dependency(name.getText(), group.getText(), version.getText(), licence.getText(),
                Dependency.Platform.valueOf(platform.getValue().toString()), repository.getText());
        if (index == -1) App.dependenciesController.addDependency(dependency);
        else {
            App.dependenciesController.dependencies.set(index, dependency);
            App.dependenciesController.refreshMap();
        }
        App.dependency.hide();
        clear();
    }

    private void clear() {
        index = -1;
        name.setText("");
        group.setText("");
        version.setText("");
        licence.setText("");
        repository.setText("");
        platform.setValue("ALL");
    }

    @FXML
    void cancel() {
        App.dependency.hide();
        clear();
    }

}
