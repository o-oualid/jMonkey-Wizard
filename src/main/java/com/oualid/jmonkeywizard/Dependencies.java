package com.oualid.jmonkeywizard;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class Dependencies {
    @FXML
    CheckBox lemur;

    @FXML
    public void initialize() {

    }

    @FXML
    void ok() {
        App.dependencies.hide();
    }
}
