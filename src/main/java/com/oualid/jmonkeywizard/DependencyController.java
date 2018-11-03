package com.oualid.jmonkeywizard;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;


public class DependencyController {
    @FXML
    public TextField name, group, version, licence, repository;
    @FXML
    ComboBox platform;

    @FXML
    public void initialize() {

    }

    @FXML
    void ok() {
        App.dependenciesController.addDependency(
                new Dependency(name.getText(), group.getText(), version.getText(), licence.getText(),
                        Dependency.Platform.valueOf(platform.getValue().toString()), repository.getText()));
        App.dependency.hide();
    }

    @FXML
    void cancel() {
        App.dependency.hide();
    }

}
