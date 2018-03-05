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

    /**
     * Variables  annotated by @FXML are initialized don't listen to your IDE
     */

    static HashMap<String, String> specialWords = new HashMap<>();
    @FXML
    private TextField gameName;
    @FXML
    private TextField gamePackage;
    @FXML
    private TextField gameDirectory;
    @FXML
    private TextField jmeVersion;
    @FXML
    private ComboBox jmeRelease;
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
        fileUtils = new FileUtils(messages);
        update();
    }

    /**
     * called by {@link Controller#buildProject} when clicked
     */

    @FXML
    private void build() {
        messages.setText("Build started" + "\n");
        progressBar.setProgress(10);
        modules = "";
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

        specialWords.put("jmeV", jmeVersion.getText() + "-" + jmeRelease.getValue());
        fileUtils.createFileFromTmp(projectDir, "build.gradle", "template/build.gradle");
        fileUtils.createFileFromContent(projectDir, "settings.gradle", "include 'core','assets'" + modules);
        progressBar.setProgress(80);
        messages.setText(messages.getText() + "Build end");
    }

    /**
     * called by {@link Controller#build()} when the build start
     */

    private void addCore() {
        specialWords.put("package", gamePackage.getText());
        specialWords.put("gameName", gameName.getText());

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

        fileUtils.createFileFromTmp(javaDir, "Main.java", "template/core/Main.java");
        fileUtils.createFileFromTmp(coreDir, "build.gradle", "template/core/build.gradle");

        // add jme3 core dependency to Core module
        coreDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-core:$JMonkey_version\"\n";
        // make gradle wrapper files
        fileUtils.createFileFromTmp(gradleDir, "gradle-wrapper.properties", "template/gradle/wrapper/gradle-wrapper.properties");
        fileUtils.copyFile("template/gradle/wrapper/gradle-wrapper.jar", gradleDir + "/gradle-wrapper.jar");
        progressBar.setProgress(30);

    }

    /**
     * called by {@link Controller#build()} if {@link Controller#desktop} is selected
     */

    private void addDesktop() {
        File desktopDir = fileUtils.newDir(projectDir.getPath() + "/desktop");

        File desktopJavaDir = fileUtils.newDir(desktopDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        fileUtils.createFileFromTmp(desktopJavaDir, "DesktopLauncher.java", "template/desktop/DesktopLauncher.java");
        fileUtils.createFileFromTmp(desktopDir, "build.gradle", "template/desktop/build.gradle");
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
        File androidValues = fileUtils.newDir(androidRes.getPath() + "/values");

        fileUtils.copyDirectory("template/android/res", androidRes.getAbsolutePath());
        fileUtils.createFileFromTmp(androidValues, "strings.xml", "template/android/res/values/strings.xml");
        fileUtils.createFileFromTmp(androidJavaDir, "AndroidLauncher.java", "template/android/AndroidLauncher.java");
        fileUtils.createFileFromTmp(androidDir, "build.gradle", "template/android/build.gradle");
        fileUtils.createFileFromTmp(androidDir, "proguard-rules.pro", "template/android/proguard-rules.pro");
        fileUtils.createFileFromTmp(androidMainDir, "AndroidManifest.xml", "template/android/AndroidManifest.xml");

        // add android necessary dependencies
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
        specialWords.put("androidClasspath", "classpath 'com.android.tools.build:gradle:2.3.0'\n" +
                "        //if you you will use Intellij IDEA instead of Android Studio do not update android gradle plugin to 3.0.0 or more this will cause an issue, at least until an update Intellij IDEA update the android plugin.\n" +
                "        ");
        modules = modules + ", 'android'";
    }

    /**
     * called by {@link Controller#build()} if {@link Controller#ios} is selected
     */
    private void addIos() {
        File iosDir = fileUtils.newDir(projectDir.getPath() + "/ios");
        File iosJavaDir = fileUtils.newDir(iosDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        fileUtils.createFileFromTmp(iosJavaDir, "IosLauncher.java", "template/ios/IosLauncher.java");
        fileUtils.createFileFromTmp(iosDir, "build.gradle", "template/ios/build.gradle");
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
        fileUtils.createFileFromTmp(vrJavaDir, "VrLauncher.java", "template/vr/VrLauncher.java");
        fileUtils.createFileFromTmp(vrDir, "build.gradle", "template/vr/build.gradle");
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
        if (projectDir.exists()) {
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
            blender.setSelected(false);
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

        if (bullet.isSelected()) {
            jBullet.setSelected(false);
        } else if (jBullet.isSelected()) {
            bullet.setSelected(false);
        }

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
            projectDir = new File(gameDirectory.getText());
        }
    }

    /**
     * called by {@link Controller#more} when tapped
     */
    @FXML
    private void more() {
        Main.dependencies.show();
    }
}