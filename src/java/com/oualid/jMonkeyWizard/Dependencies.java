package com.oualid.jMonkeyWizard;

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
        Main.dependencies.hide();
    }
}
