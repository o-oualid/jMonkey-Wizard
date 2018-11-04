package com.oualid.jmonkeywizard;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class MainUiController {
    private final String slash = File.separator;
    @FXML
    TextField gamePackage, gameName, gameDirectory, jmeVersion, gradleVersion, tmpPath;
    @FXML
    ComboBox jmeRelease, javaVersion, gradleType, kotlinVersion;
    @FXML
    RadioButton jogl, lwjgl, lwjgl3;
    @FXML
    CheckBox desktop, android, ios, vr, useKotlin, jBullet, bullet, jogg, plugins,
            terrain, effects, blender, niftyGUI, examples, networking, customTmp;
    @FXML
    Button buildProject;
    @FXML
    ProgressBar progressBar;
    @FXML
    TextArea messages;

    File projectDir;

    @FXML
    public void initialize() {
        projectDir = new File(gameName.getText());
        gameDirectory.setText(projectDir.getAbsolutePath());
        projectDir = new File(projectDir.getAbsolutePath());
        jmeRelease.getSelectionModel().select(0);
        javaVersion.getSelectionModel().select(0);
        gradleType.getSelectionModel().select(1);
        kotlinVersion.getSelectionModel().select(0);
        update();
    }


    @FXML
    private void build() {
        if (projectDir.getAbsoluteFile().exists()) {
            printMessage("Directory already exist please choose an other one");
            return;
        }

        App.projectBuilder.build();
    }


    @FXML
    private void update() {
        if (gameName.getText().isEmpty()) buildProject.setDisable(true);
        else buildProject.setDisable(false);

        if (gamePackage.getText().isEmpty()) buildProject.setDisable(true);
        else {
            gamePackage.setText(gamePackage.getText().toLowerCase());
            buildProject.setDisable(false);
        }

        if (gameDirectory.getText().isEmpty()) buildProject.setDisable(true);
        else buildProject.setDisable(false);

        if (desktop.isSelected() || android.isSelected() || ios.isSelected() || vr.isSelected())
            buildProject.setDisable(false);
        else buildProject.setDisable(true);


        if (desktop.isSelected()) {
            lwjgl3.setDisable(false);
            jogl.setDisable(false);
            lwjgl.setDisable(false);
        } else {
            lwjgl3.setDisable(true);
            jogl.setDisable(true);
            lwjgl.setDisable(true);
        }

        if (!desktop.isSelected() || android.isSelected() || ios.isSelected() || vr.isSelected())
            blender.setDisable(true);
        else blender.setDisable(false);

        if (ios.isSelected() || vr.isSelected()) {
            bullet.setDisable(true);
            if (bullet.isSelected()) {
                bullet.setSelected(false);
                jBullet.setSelected(true);
            }
        } else bullet.setDisable(false);


    }


    @FXML
    private void updateBullet() {
        jBullet.setSelected(false);
    }

    @FXML
    private void updateJBullet() {
        bullet.setSelected(false);
    }


    @FXML
    private void updateGameName() {
        if (!gameName.getText().isEmpty()) {
            gameDirectory.setText(projectDir.getParent() + slash + gameName.getText());
            projectDir = new File(gameDirectory.getText());
            gameDirectory.setText(
                    projectDir.getParentFile().getAbsolutePath() + slash +
                            gameName.getText()
                                    .replace(slash, "")
                                    .replace("/", ""));
            projectDir = new File(gameDirectory.getText());
            String[] packages = (gamePackage.getText()).split("\\.");
            packages[packages.length - 1] = gameName.getText()
                    .replace(" ", "")
                    .replace(".", "");
            StringBuilder p = new StringBuilder();
            for (String string : packages) {
                if (p.length() > 0) p.append(".");
                p.append(string);
            }
            gamePackage.setText(p.toString().toLowerCase());
        }
    }

    @FXML
    private void browse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(projectDir.getParentFile());
        File selectedFile = directoryChooser.showDialog(App.primaryStage);
        if (selectedFile != null) {
            gameDirectory.setText(selectedFile.getAbsolutePath() + slash + gameName.getText());
            updateDir();
        }
    }

    @FXML
    private void chooseTemplate() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(projectDir.getParentFile());
        File selectedFile = directoryChooser.showDialog(App.primaryStage);
        if (selectedFile != null) {
            tmpPath.setText(selectedFile.getAbsolutePath());
            updateDir();
        }
    }

    @FXML
    private void updateDir() {
        projectDir = new File(gameDirectory.getText());
        projectDir = new File(projectDir.getAbsolutePath());
    }


    @FXML
    private void more() {
        App.dependencies.show();
    }

    void printMessage(String text) {
        if (messages.getText().isEmpty()) messages.setText(text);
        else messages.setText(messages.getText() + "\n" + text);
    }

}