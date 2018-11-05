package com.oualid.jmonkeywizard;

import java.io.File;
import java.util.HashMap;

import static com.oualid.jmonkeywizard.FileUtils.*;

class ProjectBuilder {
    static HashMap<String, String> specialWords = new HashMap<>();

    private MainUiController mainController;
    private DependenciesController dependenciesController;

    ProjectBuilder(MainUiController mainController, DependenciesController dependenciesController) {
        this.mainController = mainController;
        this.dependenciesController = dependenciesController;
    }

    private String modules;
    private StringBuilder coreDependencies;
    private StringBuilder desktopDependencies;
    private StringBuilder androidDependencies;
    private StringBuilder iosDependencies;
    private StringBuilder vrDependencies;

    private StringBuilder corePlugins;
    private StringBuilder desktopPlugins;
    private StringBuilder androidPlugins;
    private StringBuilder iosPlugins;
    private StringBuilder vrPlugins;

    private StringBuilder extVars;
    private StringBuilder classPaths;
    private StringBuilder repositories;
    private StringBuilder subRepositories;

    private File projectDir;

    void build() {
        coreDependencies = new StringBuilder();
        desktopDependencies = new StringBuilder();
        androidDependencies = new StringBuilder();
        iosDependencies = new StringBuilder();
        vrDependencies = new StringBuilder();

        corePlugins = new StringBuilder();
        desktopPlugins = new StringBuilder();
        androidPlugins = new StringBuilder();
        iosPlugins = new StringBuilder();
        vrPlugins = new StringBuilder();

        extVars = new StringBuilder();
        classPaths = new StringBuilder();
        repositories = new StringBuilder();
        subRepositories = new StringBuilder();

        printMessage("Build started");

        modules = "";
        specialWords.put("package", mainController.gamePackage.getText());
        specialWords.put("gameName", mainController.gameName.getText());
        specialWords.put("javaVersion", "" + mainController.javaVersion.getValue());
        specialWords.put("gradleVersion", mainController.gradleVersion.getText() + "-" + mainController.gradleType.getValue());
        specialWords.put("jmeV", mainController.jmeVersion.getText() + "-" + mainController.jmeRelease.getValue());

        if (mainController.customTmp.isSelected()) {
            newDir(mainController.gameDirectory.getText());
            copyDirectory(mainController.tmpPath.getText(), mainController.gameDirectory.getText(), mainController.gamePackage.getText());
            printMessage("Build End");
            mainController.progressBar.setProgress(0);
            return;
        }

        addCorePlugin("java");
        addDesktopPlugin("java");
        addIosPlugin("java");
        addVrPlugin("java");
        if (mainController.useKotlin.isSelected()) {
            addCorePlugin("kotlin");
            addDesktopPlugin("kotlin");
            addIosPlugin("kotlin");
            addVrPlugin("kotlin");
            addAndroidPlugin("kotlin-android");
            addClasspath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version");
            addExtVar("kotlin_version = '" + mainController.kotlinVersion.getValue() + "'");
        }
        addDependencies();
        addCustomDependencies();
        specialWords.put("coreDependencies", coreDependencies.toString());
        specialWords.put("desktopDependencies", desktopDependencies.toString());
        specialWords.put("androidDependencies", androidDependencies.toString());
        specialWords.put("iosDependencies", iosDependencies.toString());
        specialWords.put("vrDependencies", vrDependencies.toString());
        if (mainController.android.isSelected()) {
            addClasspath("com.android.tools.build:gradle:3.2.1");
            addRepository("google()");
            addSubRepository("google()");
        }
        specialWords.put("classPaths", classPaths.toString());
        specialWords.put("repositories", repositories.toString());


        specialWords.put("corePlugins", corePlugins.toString());
        specialWords.put("desktopPlugins", desktopPlugins.toString());
        specialWords.put("androidPlugins", androidPlugins.toString());
        specialWords.put("iosPlugins", iosPlugins.toString());
        specialWords.put("vrPlugins", vrPlugins.toString());

        specialWords.put("extVars", extVars.toString());
        specialWords.put("subRepositories", subRepositories.toString());

        addCore();
        mainController.progressBar.setProgress(10);
        if (mainController.desktop.isSelected()) addDesktop();


        mainController.progressBar.setProgress(20);
        if (mainController.android.isSelected()) addAndroid();

        mainController.progressBar.setProgress(40);
        if (mainController.ios.isSelected()) addIos();

        mainController.progressBar.setProgress(60);
        if (mainController.vr.isSelected()) addVr();

        mainController.progressBar.setProgress(80);
        createFileFromTmp(projectDir, "build.gradle", "template/build.gradle");
        createFileFromContent(projectDir, "settings.gradle", "include 'core'" + modules);
        mainController.progressBar.setProgress(100);
        printMessage("Build end");
        mainController.progressBar.setProgress(0);
    }


    private void addCore() {
        projectDir = newDir(mainController.projectDir.getPath());
        File gradleDir = newDir(projectDir.getPath() + "/gradle/wrapper");
        File assetsDir = newDir(projectDir.getPath() + "/assets");

        newDir(assetsDir.getPath() + "/Interface");
        newDir(assetsDir.getPath() + "/MatDefs");
        newDir(assetsDir.getPath() + "/Materials");
        newDir(assetsDir.getPath() + "/Models");
        newDir(assetsDir.getPath() + "/Scenes");
        newDir(assetsDir.getPath() + "/Shaders");
        newDir(assetsDir.getPath() + "/Sounds");
        newDir(assetsDir.getPath() + "/Textures");

        File coreDir = newDir(projectDir.getPath() + "/core");

        createFileFromTmp(coreDir, "build.gradle", "template/core/build.gradle");
        File javaDir = newDir(coreDir.getPath() + "/src/main/java/" + mainController.gamePackage.getText().replace(".", "/"));

        if (mainController.useKotlin.isSelected()) {
            File kotlinDir = newDir(coreDir.getPath() + "/src/main/kotlin/" + mainController.gamePackage.getText().replace(".", "/"));
            createFileFromTmp(kotlinDir, "Main.kt", "template/core/Main.kt");
        } else
            createFileFromTmp(javaDir, "Main.java", "template/core/Main.java");


        createFileFromTmp(gradleDir, "gradle-wrapper.properties", "template/gradle/wrapper/gradle-wrapper.properties");
        copyFile("template/gradle/wrapper/gradle-wrapper.jar", gradleDir + "/gradle-wrapper.jar");

    }


    private void addDesktop() {
        File desktopDir = newDir(projectDir.getPath() + "/desktop");
        File javaDir = newDir(desktopDir.getPath() + "/src/main/java/" + mainController.gamePackage.getText().replace(".", "/"));
        if (mainController.useKotlin.isSelected()) {
            File kotlinDir = newDir(desktopDir.getPath() + "/src/main/kotlin/" + mainController.gamePackage.getText().replace(".", "/"));
            createFileFromTmp(kotlinDir, "DesktopLauncher.kt", "template/desktop/DesktopLauncher.kt");
        } else
            createFileFromTmp(javaDir, "DesktopLauncher.java", "template/desktop/DesktopLauncher.java");

        createFileFromTmp(desktopDir, "build.gradle", "template/desktop/build.gradle");
        modules = modules + ", 'desktop'";
    }

    private void addAndroid() {
        File androidDir = newDir(projectDir.getPath() + "/android");
        File androidMainDir = newDir(androidDir.getPath() + "/src/main");
        File javaDir = newDir(androidMainDir.getPath() + "/java/" + mainController.gamePackage.getText().replace(".", "/"));
        File androidRes = newDir(androidMainDir + "/res");
        File hdpi = newDir(androidRes.getPath() + "/mipmap-hdpi");
        File mdpi = newDir(androidRes.getPath() + "/mipmap-mdpi");
        File xhdpi = newDir(androidRes.getPath() + "/mipmap-xhdpi");
        File xxhdpi = newDir(androidRes.getPath() + "/mipmap-xxhdpi");
        File xxxhdpi = newDir(androidRes.getPath() + "/mipmap-xxxhdpi");
        File androidValues = newDir(androidRes.getPath() + "/values");

        if (mainController.useKotlin.isSelected()) {
            File kotlinDir = newDir(androidMainDir.getPath() + "/kotlin/" + mainController.gamePackage.getText().replace(".", "/"));
            createFileFromTmp(kotlinDir, "AndroidLauncher.kt", "template/android/AndroidLauncher.kt");

        } else
            createFileFromTmp(javaDir, "AndroidLauncher.java", "template/android/AndroidLauncher.java");

        createFileFromTmp(androidValues, "strings.xml", "template/android/res/values/strings.xml");
        createFileFromTmp(androidValues, "colors.xml", "template/android/res/values/colors.xml");
        createFileFromTmp(androidValues, "styles.xml", "template/android/res/values/styles.xml");
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

    private void addIos() {
        File iosDir = newDir(projectDir.getPath() + "/ios");
        File javaDir = newDir(iosDir.getPath() + "/src/main/java/" + mainController.gamePackage.getText().replace(".", "/"));
        if (mainController.useKotlin.isSelected()) {
            File kotlinDir = newDir(iosDir.getPath() + "/src/main/kotlin/" + mainController.gamePackage.getText().replace(".", "/"));
            createFileFromTmp(kotlinDir, "IosLauncher.kt", "template/ios/IosLauncher.kt");
        } else
            createFileFromTmp(javaDir, "IosLauncher.java", "template/ios/IosLauncher.java");
        createFileFromTmp(iosDir, "build.gradle", "template/ios/build.gradle");
        modules = modules + ", 'ios'";
    }

    /**
     * called by {@link MainUiController#build()} if {@link MainUiController#vr} is selected
     */

    private void addVr() {
        File vrDir = newDir(projectDir.getPath() + "/vr");
        File javaDir = newDir(vrDir.getPath() + "/src/main/java/" + mainController.gamePackage.getText().replace(".", "/"));

        if (mainController.useKotlin.isSelected()) {
            File kotlinDir = newDir(vrDir.getPath() + "/src/main/kotlin/" + mainController.gamePackage.getText().replace(".", "/"));
            createFileFromTmp(kotlinDir, "VrLauncher.kt", "template/vr/VrLauncher.kt");
        } else createFileFromTmp(javaDir, "VrLauncher.java", "template/vr/VrLauncher.java");
        createFileFromTmp(vrDir, "build.gradle", "template/vr/build.gradle");

        modules = modules + ", 'vr'";
    }

    private void addDependencies() {
        addCoreDependency("${jme3.g}:jme3-core:${jme3.v}");

        if (mainController.desktop.isSelected()) {
            addDesktopDependency("${jme3.g}:jme3-desktop:${jme3.v}");

            if (mainController.lwjgl3.isSelected()) addDesktopDependency("${jme3.g}:jme3-lwjgl3:${jme3.v}");

            else if (mainController.lwjgl.isSelected()) addDesktopDependency("${jme3.g}:jme3-lwjgl:${jme3.v}");
            else if (mainController.jogl.isSelected()) addDesktopDependency("${jme3.g}:jme3-jogl:${jme3.v}");
        }

        if (mainController.android.isSelected()) {
            addAndroidDependency("com.android.support:appcompat-v7:28.0.0");
            addAndroidDependency("${jme3.g}:jme3-android:${jme3.v}");
            addAndroidDependency("${jme3.g}:jme3-android-native:${jme3.v}");
        }

        if (mainController.ios.isSelected()) addIosDependency("${jme3.g}:jme3-ios:${jme3.v}");

        if (mainController.vr.isSelected()) addVrDependency("${jme3.g}:jme3-vr:${jme3.v}");

        if (mainController.bullet.isSelected()) {
            addCoreDependency("${jme3.g}:jme3-bullet:${jme3.v}");
            addDesktopDependency("${jme3.g}:jme3-bullet-native:${jme3.v}");
            addAndroidDependency("${jme3.g}:jme3-bullet-native-android:${jme3.v}");
        } else if (mainController.jBullet.isSelected())
            addCoreDependency("${jme3.g}:jme3-jbullet:${jme3.v}");

        if (mainController.terrain.isSelected()) addCoreDependency("${jme3.g}:jme3-terrain:${jme3.v}");

        if (mainController.niftyGUI.isSelected()) addCoreDependency("${jme3.g}:jme3-niftygui:${jme3.v}");

        if (mainController.effects.isSelected()) addCoreDependency("${jme3.g}:jme3-effects:${jme3.v}");

        if (mainController.plugins.isSelected()) addCoreDependency("${jme3.g}:jme3-plugins:${jme3.v}");

        if (mainController.blender.isSelected()) addDesktopDependency("${jme3.g}:jme3-blender:${jme3.v}");

        if (mainController.jogg.isSelected()) addCoreDependency("${jme3.g}:jme3-jogg:${jme3.v}");

        if (mainController.networking.isSelected()) addCoreDependency("${jme3.g}:jme3-networking:${jme3.v}");

        if (mainController.examples.isSelected()) addCoreDependency("${jme3.g}:jme3-examples:${jme3.v}");
    }


    private void printMessage(String message) {
        mainController.printMessage(message);
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

    private void addCorePlugin(String plugin) {
        corePlugins
                .append("id \"")
                .append(plugin)
                .append("\"\n    ");
    }

    private void addDesktopPlugin(String plugin) {
        desktopPlugins
                .append("id \"")
                .append(plugin)
                .append("\"\n    ");
    }

    private void addAndroidPlugin(String plugin) {
        androidPlugins
                .append("id \"")
                .append(plugin)
                .append("\"\n    ");
    }

    private void addIosPlugin(String plugin) {
        iosPlugins
                .append("id \"")
                .append(plugin)
                .append("\"    ");
    }

    private void addVrPlugin(String plugin) {
        vrPlugins
                .append("id \"")
                .append(plugin)
                .append("\"\n    ");
    }

    private void addExtVar(String var) {
        extVars
                .append(var)
                .append("\n        ");
    }

    private void addSubRepository(String repository) {
        subRepositories
                .append(repository)
                .append("\n        ");

    }

    private void addCustomDependencies() {
        for (Dependency dependency : App.dependenciesController.getSelectedDependencies()) {
            if (!dependency.repository.isEmpty() && !subRepositories.toString().contains(dependency.repository))
                addSubRepository(dependency.repository);

            switch (dependency.platform) {
                case ALL:
                    addCoreDependency(dependency.group + ":" + dependency.name + ":" + dependency.version);
                    break;
                case DESKTOP:
                    addDesktopDependency(dependency.group + ":" + dependency.name + ":" + dependency.version);
                    break;
                case ANDROID:
                    addAndroidDependency(dependency.group + ":" + dependency.name + ":" + dependency.version);
                    break;
                case IOS:
                    addIosDependency(dependency.group + ":" + dependency.name + ":" + dependency.version);
                    break;
                case VR:
                    addVrDependency(dependency.group + ":" + dependency.name + ":" + dependency.version);
                    break;
            }
        }
    }


}
