package com.oualid.jmonkeywizard;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;


public class DependencyController {
    @FXML
    public TextField name, group, version, licence;
    @FXML
    ComboBox platform;

    @FXML
    public void initialize() {

    }

    @FXML
    void ok() {
        App.dependency.hide();
    }

}
