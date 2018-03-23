package com.oualid.jMonkeyWizard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Controller extends VBox {
   static HashMap<String, String> specialWords = new HashMap<>();
    @FXML
    TextField gamePackage;
    @FXML
    private TextField gameName;
    @FXML
    private TextField gameDirectory;
    @FXML
    private TextField jmeVersion;
    @FXML
    private TextField gradleVersion;
    @FXML
    private ComboBox jmeRelease;
    @FXML
    private ComboBox javaVersion;
    @FXML
    private ComboBox gradleType;
    @FXML
    private RadioButton jogl;
    @FXML
    private RadioButton lwjgl;
    @FXML
    private RadioButton lwjgl3;
    @FXML
    private CheckBox desktop;
    @FXML
    private CheckBox android;
    @FXML
    private CheckBox ios;
    @FXML
    private CheckBox vr;
    @FXML
    private CheckBox jBullet;
    @FXML
    private CheckBox bullet;
    @FXML
    private CheckBox jogg;
    @FXML
    private CheckBox plugins;
    @FXML
    private CheckBox terrain;
    @FXML
    private CheckBox effects;
    @FXML
    private CheckBox blender;
    @FXML
    private CheckBox niftyGUI;
    @FXML
    private CheckBox examples;
    @FXML
    private CheckBox networking;
    @FXML
    private Button buildProject;
    @FXML
    private Button browse;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextArea messages;
    @FXML
    private Button more;
    @FXML
    private TextField tmpPath;
    @FXML
    private Button browseTmp;
    @FXML
    private CheckBox customTmp;

    private String modules;
    private String coreDependencies, desktopDependencies = "", androidDependencies = "", iosDependencies = "", vrDependencies = "";
    private File projectDir;
    private FileUtils fileUtils;

    /**
     * the constructor of this class
     */
    Controller() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ui.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        projectDir = new File(gameName.getText());
        gameDirectory.setText(projectDir.getAbsolutePath());
        projectDir = new File(projectDir.getAbsolutePath());
        fileUtils = new FileUtils(this);
        jmeRelease.getSelectionModel().select(0);
        javaVersion.getSelectionModel().select(0);
        gradleType.getSelectionModel().select(0);
        update();
    }

    /**
     * called by {@link Controller#buildProject} when clicked
     */

    @FXML
    private void build() {
        if (projectDir.getAbsoluteFile().exists()) {
            newMessage("Directory already exist choose an other one!");
            return;
        }
        if (customTmp.isSelected() && !new File(tmpPath.getText()).exists()) {
            newMessage("Custom template path not found!");
            return;
        }
        newMessage("Build started");

        progressBar.setProgress(10);
        modules = "";
        specialWords.put("package", gamePackage.getText());
        specialWords.put("gameName", gameName.getText());
        specialWords.put("javaVersion", "" + javaVersion.getValue());
        specialWords.put("gradleVersion", gradleVersion.getText() + "-" + gradleType.getValue());
        specialWords.put("jmeV", jmeVersion.getText() + "-" + jmeRelease.getValue());

        if (customTmp.isSelected()) {
            fileUtils.newDir(gameDirectory.getText());
            fileUtils.copyDirectory(tmpPath.getText(), gameDirectory.getText());
            newMessage("Build End");
            progressBar.setProgress(100);
            return;
        }
        addCore();

        addDependencies();
        specialWords.put("coreDependencies", coreDependencies);
        progressBar.setProgress(20);
        if (desktop.isSelected()) {
            addDesktop();
        } else {
            specialWords.put("desktopDependencies", "");

        }
        progressBar.setProgress(30);
        if (android.isSelected()) {
            addAndroid();
        } else {
            specialWords.put("androidDependencies", "");
            specialWords.put("androidClasspath", "");
        }
        progressBar.setProgress(40);
        if (ios.isSelected()) {
            addIos();
        } else {
            specialWords.put("iosDependencies", "");
        }
        progressBar.setProgress(50);
        if (vr.isSelected()) {
            addVr();
        } else {
            specialWords.put("vrDependencies", "");
        }

        progressBar.setProgress(60);


        fileUtils.createFileFromTmp(projectDir, "build.gradle", "template/build.gradle", true);
        fileUtils.createFileFromContent(projectDir, "settings.gradle", "include 'core','assets'" + modules);
        progressBar.setProgress(80);
        newMessage("Build end");
    }


    /**
     * called by {@link Controller#build()} when the build start
     */

    private void addCore() {

        projectDir = fileUtils.newDir(projectDir.getPath());
        File gradleDir = fileUtils.newDir(projectDir.getPath() + "/gradle/wrapper");
        File assetsDir = fileUtils.newDir(projectDir.getPath() + "/assets");

        // sub assets folders
        fileUtils.newDir(assetsDir.getPath() + "/Interface");
        fileUtils.newDir(assetsDir.getPath() + "/MatDefs");
        fileUtils.newDir(assetsDir.getPath() + "/Materials");
        fileUtils.newDir(assetsDir.getPath() + "/Models");
        fileUtils.newDir(assetsDir.getPath() + "/Scenes");
        fileUtils.newDir(assetsDir.getPath() + "/Shaders");
        fileUtils.newDir(assetsDir.getPath() + "/Sounds");
        fileUtils.newDir(assetsDir.getPath() + "/Textures");

        File coreDir = fileUtils.newDir(projectDir.getPath() + "/core"); //core folder
        File javaDir = fileUtils.newDir(coreDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));// core java folder

        fileUtils.createFileFromTmp(javaDir, "Main.java", "template/core/Main.java", true);
        fileUtils.createFileFromTmp(coreDir, "build.gradle", "template/core/build.gradle", true);

        // add jme3 core dependency to Core module
        coreDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-core:$JMonkey_version\"\n";
        // make gradle wrapper files
        fileUtils.createFileFromTmp(gradleDir, "gradle-wrapper.properties", "template/gradle/wrapper/gradle-wrapper.properties", true);
        fileUtils.copyFile("template/gradle/wrapper/gradle-wrapper.jar", gradleDir + "/gradle-wrapper.jar", true);
        progressBar.setProgress(30);

    }

    /**
     * called by {@link Controller#build()} if {@link Controller#desktop} is selected
     */

    private void addDesktop() {
        File desktopDir = fileUtils.newDir(projectDir.getPath() + "/desktop");

        File desktopJavaDir = fileUtils.newDir(desktopDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        fileUtils.createFileFromTmp(desktopJavaDir, "DesktopLauncher.java", "template/desktop/DesktopLauncher.java", true);
        fileUtils.createFileFromTmp(desktopDir, "build.gradle", "template/desktop/build.gradle", true);
        //add desktop necessary dependencies
        if (lwjgl3.isSelected()) {
            desktopDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-lwjgl3:$JMonkey_version\"\n";
        } else if (lwjgl.isSelected()) {
            desktopDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-lwjgl:$JMonkey_version\"\n";
        } else if (jogl.isSelected()) {
            desktopDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-jogl:$JMonkey_version\"\n";
        }
        desktopDependencies = "project(\":desktop\") {\n" + "\t\tapply plugin: \"java\"\n" + "\t\tapply plugin: \"idea\"\n" + "\t\tidea {\n" + "\t\t\tmodule {\n" + "\t\t\t\tscopes.PROVIDED.minus += [configurations.compile]\n" + "\t\t\t\tscopes.COMPILE.plus += [configurations.compile]\n" + "        }\n" + "    }\n" + "\t\tdependencies {\n" + "\t\t\tcompile project(\":core\")\n" + "\t\t\tcompile \"org.junit.platform:junit-platform-launcher:$junitPlatform_version\"\n" + "\t\t\tcompile \"org.jmonkeyengine:jme3-desktop:$JMonkey_version\"\n" + desktopDependencies + "\n\t}\n}";
        specialWords.put("desktopDependencies", desktopDependencies);
        modules = modules + ", 'desktop'";
    }

    /**
     * called by {@link Controller#build()} if {@link Controller#android} is selected
     */
    private void addAndroid() {
        File androidDir = fileUtils.newDir(projectDir.getPath() + "/android");
        File androidMainDir = fileUtils.newDir(androidDir.getPath() + "/src/main");
        File androidJavaDir = fileUtils.newDir(androidMainDir.getPath() + "/java/" + gamePackage.getText().replace(".", "/"));
        File androidRes = fileUtils.newDir(androidMainDir + "/res");
        File hdpi = fileUtils.newDir(androidRes.getPath() + "/mipmap-hdpi");
        File mdpi = fileUtils.newDir(androidRes.getPath() + "/mipmap-mdpi");
        File xhdpi = fileUtils.newDir(androidRes.getPath() + "/mipmap-xhdpi");
        File xxhdpi = fileUtils.newDir(androidRes.getPath() + "/mipmap-xxhdpi");
        File xxxhdpi = fileUtils.newDir(androidRes.getPath() + "/mipmap-xxxhdpi");
        File androidValues = fileUtils.newDir(androidRes.getPath() + "/values");

        fileUtils.createFileFromTmp(androidValues, "strings.xml", "template/android/res/values/strings.xml", true);
        fileUtils.createFileFromTmp(androidValues, "colors.xml", "template/android/res/values/colors.xml", true);
        fileUtils.createFileFromTmp(androidValues, "styles.xml", "template/android/res/values/styles.xml", true);
        fileUtils.createFileFromTmp(androidJavaDir, "AndroidLauncher.java", "template/android/AndroidLauncher.java", true);
        fileUtils.createFileFromTmp(androidDir, "build.gradle", "template/android/build.gradle", true);
        fileUtils.createFileFromTmp(androidDir, "proguard-rules.pro", "template/android/proguard-rules.pro", true);
        fileUtils.createFileFromTmp(androidMainDir, "AndroidManifest.xml", "template/android/AndroidManifest.xml", true);
        fileUtils.copyFile("template/android/res/mipmap-mdpi/ic_launcher_round.png", mdpi.getPath() + "/ic_launcher_round.png", true);
        fileUtils.copyFile("template/android/res/mipmap-mdpi/ic_launcher.png", mdpi.getPath() + "/ic_launcher.png", true);
        fileUtils.copyFile("template/android/res/mipmap-mdpi/ic_launcher_round.png", hdpi.getPath() + "/ic_launcher_round.png", true);
        fileUtils.copyFile("template/android/res/mipmap-mdpi/ic_launcher.png", hdpi.getPath() + "/ic_launcher.png", true);
        fileUtils.copyFile("template/android/res/mipmap-mdpi/ic_launcher_round.png", xhdpi.getPath() + "/ic_launcher_round.png", true);
        fileUtils.copyFile("template/android/res/mipmap-mdpi/ic_launcher.png", xhdpi.getPath() + "/ic_launcher.png", true);
        fileUtils.copyFile("template/android/res/mipmap-mdpi/ic_launcher_round.png", xxhdpi.getPath() + "/ic_launcher_round.png", true);
        fileUtils.copyFile("template/android/res/mipmap-mdpi/ic_launcher.png", xxhdpi.getPath() + "/ic_launcher.png", true);
        fileUtils.copyFile("template/android/res/mipmap-mdpi/ic_launcher_round.png", xxxhdpi.getPath() + "/ic_launcher_round.png", true);
        fileUtils.copyFile("template/android/res/mipmap-mdpi/ic_launcher.png", xxxhdpi.getPath() + "/ic_launcher.png", true);

        androidDependencies = "project(\":android\")" +
                " {\n" + "\t\tapply plugin: \"android\"\n" +
                "\t\tdependencies {\n" + "\t\t\tcompile project(\":core\")\n" +
                "\t\t\tcompile fileTree(dir: 'libs', include: ['*.jar'])\n" +
                "\t\t\ttestCompile 'junit:junit:4.12'\n" +
                "\t\t\tcompile 'com.android.support:appcompat-v7:27.1.0'\n" +
                "\t\t\tcompile \"org.jmonkeyengine:jme3-android:$JMonkey_version\"\n" +
                "\t\t\tcompile \"org.jmonkeyengine:jme3-android-native:$JMonkey_version\"\n" +
                androidDependencies + "\n\t}\n}";
        specialWords.put("androidDependencies", androidDependencies);
        specialWords.put("androidClasspath", "classpath 'com.android.tools.build:gradle:3.0.1'");
        modules = modules + ", 'android'";
    }

    /**
     * called by {@link Controller#build()} if {@link Controller#ios} is selected
     */
    private void addIos() {
        File iosDir = fileUtils.newDir(projectDir.getPath() + "/ios");
        File iosJavaDir = fileUtils.newDir(iosDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        fileUtils.createFileFromTmp(iosJavaDir, "IosLauncher.java", "template/ios/IosLauncher.java", true);
        fileUtils.createFileFromTmp(iosDir, "build.gradle", "template/ios/build.gradle", true);
        // add ios necessary dependencies
        iosDependencies = "project(\":ios\") {\n" + "\t\tapply plugin: \"java\"\n" + "\t\tdependencies {\n" + "\t\t\tcompile project(\":core\")\n" + "\t\t\tcompile \"org.jmonkeyengine:jme3-ios:$JMonkey_version\"\n" + iosDependencies + "\n\t}\n}";
        specialWords.put("iosDependencies", iosDependencies);
        modules = modules + ", 'ios'";

    }

    /**
     * called by {@link Controller#build()} if {@link Controller#vr} is selected
     */

    private void addVr() {
        File vrDir = fileUtils.newDir(projectDir.getPath() + "/vr");
        File vrJavaDir = fileUtils.newDir(vrDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        fileUtils.createFileFromTmp(vrJavaDir, "VrLauncher.java", "template/vr/VrLauncher.java", true);
        fileUtils.createFileFromTmp(vrDir, "build.gradle", "template/vr/build.gradle", true);
        //add vr necessary dependencies
        vrDependencies = "project(\":vr\") {\n" + "\t\tapply plugin: \"java\"\n" + "\t\tdependencies {\n" + "\t\t\tcompile project(\":core\")\n" + "\t\t\tcompile \"org.jmonkeyengine:jme3-vr:$JMonkey_version\"\n" + vrDependencies + "\n\t}\n}";
        specialWords.put("vrDependencies", vrDependencies);
        modules = modules + ", 'vr'";
    }

    /**
     * called by {@link Controller#build()}
     */

    private void addDependencies() {

        if (bullet.isSelected()) {
            desktopDependencies = desktopDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-bullet-native:$JMonkey_version\"\n";
            androidDependencies = androidDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-bullet-native-android:$JMonkey_version\"\n";
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-bullet:$JMonkey_version\"\n";
        } else if (jBullet.isSelected()) {
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-jbullet:$JMonkey_version\"\n";
        }
        if (terrain.isSelected()) {
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-terrain:$JMonkey_version\"\n";
        }
        if (niftyGUI.isSelected()) {
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-niftygui:$JMonkey_version\"\n";
        }
        if (effects.isSelected()) {
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-effects:$JMonkey_version\"\n";
        }
        if (plugins.isSelected()) {
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-plugins:$JMonkey_version\"\n";
        }
        if (blender.isSelected()) {
            desktopDependencies = desktopDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-blender:$JMonkey_version\"\n";
        }
        if (jogg.isSelected()) {
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-jogg:$JMonkey_version\"\n";
        }
        if (networking.isSelected()) {
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-networking:$JMonkey_version\"\n";
        }
        if (examples.isSelected()) {
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-examples:$JMonkey_version\"\n";
        }
    }

    @FXML
    private void update() {
        if (projectDir.getAbsoluteFile().exists()) {
            buildProject.setDisable(true);
        } else {
            buildProject.setDisable(false);
        }
        if (gameName.getText().isEmpty()) {
            buildProject.setDisable(true);
        } else {
            buildProject.setDisable(false);
        }
        if (gamePackage.getText().isEmpty()) {
            buildProject.setDisable(true);
        } else {
            buildProject.setDisable(false);
        }
        if (gameDirectory.getText().isEmpty()) {
            buildProject.setDisable(true);
        } else {
            buildProject.setDisable(false);
        }
        if (!(desktop.isSelected() || android.isSelected() || ios.isSelected() || vr.isSelected())) {
            buildProject.setDisable(true);
        } else {
            buildProject.setDisable(false);
        }

        if (desktop.isSelected()) {
            lwjgl3.setDisable(false);
            jogl.setDisable(false);
            lwjgl.setDisable(false);
        } else {
            lwjgl3.setDisable(true);
            jogl.setDisable(true);
            lwjgl.setDisable(true);
        }

        if (!desktop.isSelected() || android.isSelected() || ios.isSelected() || vr.isSelected()) {
            blender.setDisable(true);


        } else {

            blender.setDisable(false);
        }
        if (ios.isSelected() || vr.isSelected()) {
            bullet.setDisable(true);
            if (bullet.isSelected()) {
                bullet.setSelected(false);
                jBullet.setSelected(true);
            }
        } else {
            bullet.setDisable(false);
        }

    }

    /**
     * called by {@link Controller#bullet} when modified
     */
    @FXML
    private void updateBullet() {
        jBullet.setSelected(false);
    }

    /**
     * called by {@link Controller#jBullet} when modified
     */
    @FXML
    private void updateJBullet() {
        bullet.setSelected(false);
    }

    /**
     * called by {@link Controller#gameName} when modified
     */
    @FXML
    private void updateGameName() {
        if (!gameName.getText().isEmpty()) {
            gameDirectory.setText(projectDir.getParent() + "\\" + gameName.getText());
            projectDir = new File(gameDirectory.getText());
            gameDirectory.setText(
                    projectDir.getParentFile().getAbsolutePath() + "\\" +
                            gameName.getText()
                                    .replace("\\", "")
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
            gamePackage.setText(p.toString());
        }
    }

    /**
     * called by {@link Controller#browse} when tapped
     */
    @FXML
    private void browse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(projectDir.getParentFile());
        File selectedFile = directoryChooser.showDialog(Main.primaryStage);
        if (selectedFile != null) {
            gameDirectory.setText(selectedFile.getAbsolutePath() + "\\" + gameName.getText());
            updateDir();
        }
    }

    /**
     * called by {@link Controller#browseTmp} when tapped
     */
    @FXML
    private void browseTmp() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(projectDir.getParentFile());
        File selectedFile = directoryChooser.showDialog(Main.primaryStage);
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

    /**
     * called by {@link Controller#more} when tapped
     */
    @FXML
    private void more() {
        newMessage("check again in an other release");
    }

    void newMessage(String text) {
        if (messages.getText().isEmpty()) {
            messages.setText(text);
        } else {
            messages.setText(messages.getText() + "\n" + text);
        }
    }
}