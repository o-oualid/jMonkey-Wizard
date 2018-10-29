package com.oualid.jMonkeyWizard;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.HashMap;

import static com.oualid.jMonkeyWizard.FileUtils.*;

public class MainUi {
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
    private String coreDependencies, desktopDependencies = "", androidDependencies = "", iosDependencies = "", vrDependencies = "";
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
            progressBar.setProgress(100);
            return;
        }
        addCore();

        addDependencies();
        specialWords.put("coreDependencies", coreDependencies);
        progressBar.setProgress(20);
        if (desktop.isSelected()) addDesktop();
        else specialWords.put("desktopDependencies", "");


        progressBar.setProgress(30);
        if (android.isSelected()) addAndroid();
        else {
            specialWords.put("androidDependencies", "");
            specialWords.put("androidClasspath", "");
        }
        progressBar.setProgress(40);
        if (ios.isSelected()) addIos();
        else specialWords.put("iosDependencies", "");

        progressBar.setProgress(50);
        if (vr.isSelected()) addVr();
        else specialWords.put("vrDependencies", "");

        progressBar.setProgress(60);
        createFileFromTmp(projectDir, "build.gradle", "template/build.gradle");
        createFileFromContent(projectDir, "settings.gradle", "include 'core','assets'" + modules);
        progressBar.setProgress(80);
        printMessage("Build end");
    }


    @FXML
    private void update() {
        if (projectDir.getAbsoluteFile().exists()) buildProject.setDisable(true);
        else buildProject.setDisable(false);

        if (gameName.getText().isEmpty()) buildProject.setDisable(true);
        else buildProject.setDisable(false);

        if (gamePackage.getText().isEmpty()) buildProject.setDisable(true);
        else buildProject.setDisable(false);

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

        // add jme3 core dependency to Core module
        coreDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-core:$JMonkey_version\"\n";
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
        //add desktop necessary dependencies
        if (lwjgl3.isSelected())
            desktopDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-lwjgl3:$JMonkey_version\"\n";
        else if (lwjgl.isSelected())
            desktopDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-lwjgl:$JMonkey_version\"\n";
        else if (jogl.isSelected())
            desktopDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-jogl:$JMonkey_version\"\n";

        desktopDependencies = "project(\":desktop\") {\n" + "\t\tapply plugin: \"java\"\n" +
                "\t\tapply plugin: \"idea\"\n" + "\t\tidea {\n" + "\t\t\tmodule {\n" +
                "\t\t\t\tscopes.PROVIDED.minus += [configurations.compile]\n" +
                "\t\t\t\tscopes.COMPILE.plus += [configurations.compile]\n" +
                "        }\n" + "    }\n" + "\t\tdependencies {\n" + "\t\t\tcompile project(\":core\")\n" +
                "\t\t\tcompile \"org.junit.platform:junit-platform-launcher:$junitPlatform_version\"\n" +
                "\t\t\tcompile \"org.jmonkeyengine:jme3-desktop:$JMonkey_version\"\n" + desktopDependencies + "\n\t}\n}";

        specialWords.put("desktopDependencies", desktopDependencies);
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
     * called by {@link MainUi#build()} if {@link MainUi#ios} is selected
     */
    private void addIos() {
        File iosDir = newDir(projectDir.getPath() + "/ios");
        File iosJavaDir = newDir(iosDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        createFileFromTmp(iosJavaDir, "IosLauncher.java", "template/ios/IosLauncher.java");
        createFileFromTmp(iosDir, "build.gradle", "template/ios/build.gradle");
        // add ios necessary dependencies
        iosDependencies = "project(\":ios\") {\n" + "\t\tapply plugin: \"java\"\n" + "\t\tdependencies {\n" + "\t\t\tcompile project(\":core\")\n" +
                "\t\t\tcompile \"org.jmonkeyengine:jme3-ios:$JMonkey_version\"\n" + iosDependencies + "\n\t}\n}";
        specialWords.put("iosDependencies", iosDependencies);
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
        //add vr necessary dependencies
        vrDependencies = "project(\":vr\") {\n" + "\t\tapply plugin: \"java\"\n" + "\t\tdependencies {\n" + "\t\t\tcompile project(\":core\")\n" +
                "\t\t\tcompile \"org.jmonkeyengine:jme3-vr:$JMonkey_version\"\n" + vrDependencies + "\n\t}\n}";
        specialWords.put("vrDependencies", vrDependencies);
        modules = modules + ", 'vr'";
    }

    /**
     * called by {@link MainUi#build()}
     */

    private void addDependencies() {
        if (bullet.isSelected()) {
            desktopDependencies = desktopDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-bullet-native:$JMonkey_version\"\n";
            androidDependencies = androidDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-bullet-native-android:$JMonkey_version\"\n";
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-bullet:$JMonkey_version\"\n";
        } else if (jBullet.isSelected())
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-jbullet:$JMonkey_version\"\n";

        if (terrain.isSelected())
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-terrain:$JMonkey_version\"\n";

        if (niftyGUI.isSelected())
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-niftygui:$JMonkey_version\"\n";

        if (effects.isSelected())
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-effects:$JMonkey_version\"\n";

        if (plugins.isSelected())
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-plugins:$JMonkey_version\"\n";

        if (blender.isSelected())
            desktopDependencies = desktopDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-blender:$JMonkey_version\"\n";

        if (jogg.isSelected())
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-jogg:$JMonkey_version\"\n";

        if (networking.isSelected())
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-networking:$JMonkey_version\"\n";

        if (examples.isSelected())
            coreDependencies = coreDependencies + "\t\t\tcompile \"org.jmonkeyengine:jme3-examples:$JMonkey_version\"\n";
    }

    /**
     * called by {@link MainUi#gameName} when modified
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
     * called by {#browse} when tapped
     */
    @FXML
    private void browse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(projectDir.getParentFile());
        File selectedFile = directoryChooser.showDialog(App.primaryStage);
        if (selectedFile != null) {
            gameDirectory.setText(selectedFile.getAbsolutePath() + "\\" + gameName.getText());
            updateDir();
        }
    }

    /**
     * called by {#browseTmp} when tapped
     */
    @FXML
    private void browseTmp() {
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

    /**
     * called by {#more} when tapped
     */
    @FXML
    private void more() {
        App.dependencies.show();
    }

    private void printMessage(String text) {
        if (messages.getText().isEmpty()) messages.setText(text);
        else messages.setText(messages.getText() + "\n" + text);
    }
}