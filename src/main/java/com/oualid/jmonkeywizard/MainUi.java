package com.oualid.jmonkeywizard;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.HashMap;

import static com.oualid.jmonkeywizard.FileUtils.*;

public class MainUi {
    private final String slash = File.separator;
    static HashMap<String, String> specialWords = new HashMap<>();
    @FXML
    TextField gamePackage;
    @FXML
    private TextField gameName, gameDirectory, jmeVersion, gradleVersion;
    @FXML
    private ComboBox jmeRelease, javaVersion, gradleType;
    @FXML
    private RadioButton jogl, lwjgl, lwjgl3;
    @FXML
    private CheckBox desktop, android, ios, vr, jBullet, bullet, jogg, plugins,
            terrain, effects, blender, niftyGUI, examples, networking, customTmp;
    @FXML
    private Button buildProject;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextArea messages;
    @FXML
    private TextField tmpPath;

    private String modules;
    private StringBuilder coreDependencies = new StringBuilder();
    private StringBuilder desktopDependencies = new StringBuilder();
    private StringBuilder androidDependencies = new StringBuilder();
    private StringBuilder iosDependencies = new StringBuilder();
    private StringBuilder vrDependencies = new StringBuilder();
    private StringBuilder classPaths = new StringBuilder();
    private StringBuilder repositories = new StringBuilder();
    private File projectDir;


    @FXML
    public void initialize() {
        projectDir = new File(gameName.getText());
        gameDirectory.setText(projectDir.getAbsolutePath());
        projectDir = new File(projectDir.getAbsolutePath());
        jmeRelease.getSelectionModel().select(0);
        javaVersion.getSelectionModel().select(0);
        gradleType.getSelectionModel().select(0);
        update();
    }

    /**
     * called by {@link MainUi#buildProject} when clicked
     */

    @FXML
    private void build() {
        if (projectDir.getAbsoluteFile().exists()) {
            printMessage("Directory already exist choose an other one!");
            return;
        }
        if (customTmp.isSelected() && !new File(tmpPath.getText()).exists()) {
            printMessage("Custom template path not found!");
            return;
        }
        printMessage("Build started");

        progressBar.setProgress(10);
        modules = "";
        specialWords.put("package", gamePackage.getText());
        specialWords.put("gameName", gameName.getText());
        specialWords.put("javaVersion", "" + javaVersion.getValue());
        specialWords.put("gradleVersion", gradleVersion.getText() + "-" + gradleType.getValue());
        specialWords.put("jmeV", jmeVersion.getText() + "-" + jmeRelease.getValue());

        if (customTmp.isSelected()) {
            newDir(gameDirectory.getText());
            copyDirectory(tmpPath.getText(), gameDirectory.getText(), gamePackage.getText());
            printMessage("Build End");
            progressBar.setProgress(0);
            return;
        }

        addDependencies();
        specialWords.put("coreDependencies", coreDependencies.toString());
        specialWords.put("desktopDependencies", desktopDependencies.toString());
        specialWords.put("androidDependencies", androidDependencies.toString());
        specialWords.put("iosDependencies", iosDependencies.toString());
        specialWords.put("vrDependencies", vrDependencies.toString());
        if (android.isSelected()) {
            addClasspath("com.android.tools.build:gradle:3.0.1");
            addRepository("google()");
        }
        specialWords.put("classPaths", classPaths.toString());
        specialWords.put("repositories", repositories.toString());

        addCore();
        progressBar.setProgress(10);
        if (desktop.isSelected()) addDesktop();


        progressBar.setProgress(20);
        if (android.isSelected()) addAndroid();

        progressBar.setProgress(40);
        if (ios.isSelected()) addIos();

        progressBar.setProgress(60);
        if (vr.isSelected()) addVr();

        progressBar.setProgress(80);
        createFileFromTmp(projectDir, "build.gradle", "template/build.gradle");
        createFileFromContent(projectDir, "settings.gradle", "include 'core','assets'" + modules);
        progressBar.setProgress(100);
        printMessage("Build end");
        progressBar.setProgress(0);
    }


    @FXML
    private void update() {
        if (projectDir.getAbsoluteFile().exists()) buildProject.setDisable(true);
        else buildProject.setDisable(false);

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

    /**
     * called by {@link MainUi#bullet} when modified
     */
    @FXML
    private void updateBullet() {
        jBullet.setSelected(false);
    }

    /**
     * called by {@link MainUi#jBullet} when modified
     */
    @FXML
    private void updateJBullet() {
        bullet.setSelected(false);
    }


    /**
     * called by {@link MainUi#build()} when the build start
     */

    private void addCore() {
        projectDir = newDir(projectDir.getPath());
        File gradleDir = newDir(projectDir.getPath() + "/gradle/wrapper");
        File assetsDir = newDir(projectDir.getPath() + "/assets");

        // sub assets folders
        newDir(assetsDir.getPath() + "/Interface");
        newDir(assetsDir.getPath() + "/MatDefs");
        newDir(assetsDir.getPath() + "/Materials");
        newDir(assetsDir.getPath() + "/Models");
        newDir(assetsDir.getPath() + "/Scenes");
        newDir(assetsDir.getPath() + "/Shaders");
        newDir(assetsDir.getPath() + "/Sounds");
        newDir(assetsDir.getPath() + "/Textures");

        File coreDir = newDir(projectDir.getPath() + "/core"); //core folder
        File javaDir = newDir(coreDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));// core java folder

        createFileFromTmp(javaDir, "App.java", "template/core/Main.java");
        createFileFromTmp(coreDir, "build.gradle", "template/core/build.gradle");

        // make gradle wrapper files
        createFileFromTmp(gradleDir, "gradle-wrapper.properties", "template/gradle/wrapper/gradle-wrapper.properties");
        copyFile("template/gradle/wrapper/gradle-wrapper.jar", gradleDir + "/gradle-wrapper.jar");
        progressBar.setProgress(30);

    }

    /**
     * called by {@link MainUi#build()} if {@link MainUi#desktop} is selected
     */

    private void addDesktop() {
        File desktopDir = newDir(projectDir.getPath() + "/desktop");

        File desktopJavaDir = newDir(desktopDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        createFileFromTmp(desktopJavaDir, "DesktopLauncher.java", "template/desktop/DesktopLauncher.java");
        createFileFromTmp(desktopDir, "build.gradle", "template/desktop/build.gradle");
        modules = modules + ", 'desktop'";
    }

    /**
     * called by {@link MainUi#build()} if {@link MainUi#android} is selected
     */
    private void addAndroid() {
        File androidDir = newDir(projectDir.getPath() + "/android");
        File androidMainDir = newDir(androidDir.getPath() + "/src/main");
        File androidJavaDir = newDir(androidMainDir.getPath() + "/java/" + gamePackage.getText().replace(".", "/"));
        File androidRes = newDir(androidMainDir + "/res");
        File hdpi = newDir(androidRes.getPath() + "/mipmap-hdpi");
        File mdpi = newDir(androidRes.getPath() + "/mipmap-mdpi");
        File xhdpi = newDir(androidRes.getPath() + "/mipmap-xhdpi");
        File xxhdpi = newDir(androidRes.getPath() + "/mipmap-xxhdpi");
        File xxxhdpi = newDir(androidRes.getPath() + "/mipmap-xxxhdpi");
        File androidValues = newDir(androidRes.getPath() + "/values");

        createFileFromTmp(androidValues, "strings.xml", "template/android/res/values/strings.xml");
        createFileFromTmp(androidValues, "colors.xml", "template/android/res/values/colors.xml");
        createFileFromTmp(androidValues, "styles.xml", "template/android/res/values/styles.xml");
        createFileFromTmp(androidJavaDir, "AndroidLauncher.java", "template/android/AndroidLauncher.java");
        createFileFromTmp(androidDir, "build.gradle", "template/android/build.gradle");
        createFileFromTmp(androidDir, "proguard-rules.pro", "template/android/proguard-rules.pro");
        createFileFromTmp(androidMainDir, "AndroidManifest.xml", "template/android/AndroidManifest.xml");
        copyFile("template/android/res/mipmap-mdpi/ic_launcher_round.png", mdpi.getPath() + "/ic_launcher_round.png");
        copyFile("template/android/res/mipmap-mdpi/ic_launcher.png", mdpi.getPath() + "/ic_launcher.png");
        copyFile("template/android/res/mipmap-mdpi/ic_launcher_round.png", hdpi.getPath() + "/ic_launcher_round.png");
        copyFile("template/android/res/mipmap-mdpi/ic_launcher.png", hdpi.getPath() + "/ic_launcher.png");
        copyFile("template/android/res/mipmap-mdpi/ic_launcher_round.png", xhdpi.getPath() + "/ic_launcher_round.png");
        copyFile("template/android/res/mipmap-mdpi/ic_launcher.png", xhdpi.getPath() + "/ic_launcher.png");
        copyFile("template/android/res/mipmap-mdpi/ic_launcher_round.png", xxhdpi.getPath() + "/ic_launcher_round.png");
        copyFile("template/android/res/mipmap-mdpi/ic_launcher.png", xxhdpi.getPath() + "/ic_launcher.png");
        copyFile("template/android/res/mipmap-mdpi/ic_launcher_round.png", xxxhdpi.getPath() + "/ic_launcher_round.png");
        copyFile("template/android/res/mipmap-mdpi/ic_launcher.png", xxxhdpi.getPath() + "/ic_launcher.png");

        modules = modules + ", 'android'";
    }

    /**
     * called by {@link MainUi#build()} if {@link MainUi#ios} is selected
     */
    private void addIos() {
        File iosDir = newDir(projectDir.getPath() + "/ios");
        File iosJavaDir = newDir(iosDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        createFileFromTmp(iosJavaDir, "IosLauncher.java", "template/ios/IosLauncher.java");
        createFileFromTmp(iosDir, "build.gradle", "template/ios/build.gradle");
        modules = modules + ", 'ios'";
    }

    /**
     * called by {@link MainUi#build()} if {@link MainUi#vr} is selected
     */

    private void addVr() {
        File vrDir = newDir(projectDir.getPath() + "/vr");
        File vrJavaDir = newDir(vrDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        createFileFromTmp(vrJavaDir, "VrLauncher.java", "template/vr/VrLauncher.java");
        createFileFromTmp(vrDir, "build.gradle", "template/vr/build.gradle");

        modules = modules + ", 'vr'";
    }

    /**
     * called by {@link MainUi#build()}
     */

    private void addDependencies() {
        addCoreDependency("${jme3.g}:jme3-core:${jme3.v}");

        if (desktop.isSelected()) {
            addDesktopDependency("${jme3.g}:jme3-desktop:${jme3.v}");

            if (lwjgl3.isSelected()) addDesktopDependency("${jme3.g}:jme3-lwjgl3:${jme3.v}");

            else if (lwjgl.isSelected()) addDesktopDependency("${jme3.g}:jme3-lwjgl:${jme3.v}");
            else if (jogl.isSelected()) addDesktopDependency("${jme3.g}:jme3-jogl:${jme3.v}");
        }

        if (android.isSelected()) {
            addAndroidDependency("com.android.support:appcompat-v7:27.1.0");
            addAndroidDependency("${jme3.g}:jme3-android:${jme3.v}");
            addAndroidDependency("${jme3.g}:jme3-ios:${jme3.v}");
        }

        if (ios.isSelected()) addIosDependency("${jme3.g}:jme3-ios:${jme3.v}");

        if (vr.isSelected()) addVrDependency("${jme3.g}:jme3-vr:${jme3.v}");

        if (bullet.isSelected()) {
            addCoreDependency("${jme3.g}:jme3-bullet{jme3.v}");
            addDesktopDependency("${jme3.g}:jme3-bullet-native:${jme3.v}");
            addAndroidDependency("${jme3.g}:jme3-bullet-native-android:${jme3.v}");
        } else if (jBullet.isSelected())
            addCoreDependency("${jme3.g}:jme3-jbullet:${jme3.v}");

        if (terrain.isSelected()) addCoreDependency("${jme3.g}:jme3-terrain{jme3.v}");

        if (niftyGUI.isSelected()) addCoreDependency("${jme3.g}:jme3-niftygui{jme3.v}");

        if (effects.isSelected()) addCoreDependency("${jme3.g}:jme3-effects{jme3.v}");

        if (plugins.isSelected()) addCoreDependency("${jme3.g}:jme3-plugins{jme3.v}");

        if (blender.isSelected()) addDesktopDependency("${jme3.g}:jme3-blender{jme3.v}");

        if (jogg.isSelected()) addCoreDependency("${jme3.g}:jme3-jogg{jme3.v}");

        if (networking.isSelected()) addCoreDependency("${jme3.g}:jme3-networking{jme3.v}");

        if (examples.isSelected()) addCoreDependency("${jme3.g}:jme3-examples{jme3.v}");
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

    private void printMessage(String text) {
        if (messages.getText().isEmpty()) messages.setText(text);
        else messages.setText(messages.getText() + "\n" + text);
    }

    private void addAndroidDependency(String dependency) {
        androidDependencies
                .append("implementation \"")
                .append(dependency)
                .append("\"\n    ");
    }

    private void addDesktopDependency(String dependency) {
        desktopDependencies
                .append("implementation \"")
                .append(dependency)
                .append("\"\n    ");
    }

    private void addIosDependency(String dependency) {
        iosDependencies
                .append("implementation \"")
                .append(dependency)
                .append("\"\n    ");
    }

    private void addVrDependency(String dependency) {
        vrDependencies
                .append("implementation \"")
                .append(dependency)
                .append("\"\n    ");
    }

    private void addCoreDependency(String dependency) {
        coreDependencies
                .append("implementation \"")
                .append(dependency)
                .append("\"\n    ");
    }

    private void addClasspath(String classpath) {
        classPaths
                .append("classpath \"")
                .append(classpath)
                .append("\"\n        ");
    }

    private void addRepository(String repository) {
        repositories
                .append(repository)
                .append("\n        ");
    }
}