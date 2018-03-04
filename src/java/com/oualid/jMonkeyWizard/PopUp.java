package com.oualid.jMonkeyWizard;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopUp extends Stage {
    PopUp(Stage parent, String text, boolean show) {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(parent);
        VBox dialogBox = new VBox(20);
        dialogBox.getChildren().add(new Text(text));
        Scene dialogScene = new Scene(dialogBox, 300, 200);
        setScene(dialogScene);
        show();
    }

}
