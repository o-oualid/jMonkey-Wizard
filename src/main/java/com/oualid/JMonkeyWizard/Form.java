package com.oualid.JMonkeyWizard;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;

//Created by: ouazrou-oualid on: 26/01/2018 package: com.oualid.JMonkeyWizard project: JMonkey Wizard.

/*
 * template special words:
 * ${gameName}
 * ${package}
 * ${coreDependencies}
 * ${desktopDependencies}
 * ${androidDependencies}
 * ${iosDependencies}
 * ${vrDependencies}
 */

class Form {
    public JPanel mainPanel;
    private JRadioButton jogl;
    private JRadioButton lwjgl;
    private JRadioButton lwjgl3;
    private JCheckBox jogg;
    private JCheckBox jBullet;
    private JCheckBox bullet;
    private JCheckBox plugins;
    private JCheckBox android;
    private JCheckBox desktop;
    private JCheckBox ios;
    private JCheckBox vr;
    private JCheckBox terrain;
    private JCheckBox effects;
    private JCheckBox blender;
    private JCheckBox niftyGUI;
    private JCheckBox examples;
    private JCheckBox networking;
    private JTextField gameName;
    private JTextField jmeVersion;
    private JTextField gameDirectory;
    private JTextField gamePackage;
    private JButton buildProjectButton;
    private JButton browseButton;
    private JComboBox jmeRelease;
    private JProgressBar progressBar1;
    private String modules;
    static HashMap<String, String> specialWords = new HashMap<>();
    private String coreDependencies, desktopDependencies = "", androidDependencies = "", iosDependencies = "", vrDependencies = "";
    private File projectDir;
    private FileUtils fileUtils = new FileUtils();
    //the constructor of this class init the listeners

    Form() {
        projectDir = new File(gameName.getText());
        gameDirectory.setText(projectDir.getAbsolutePath());
        projectDir = new File(projectDir.getAbsolutePath());

        browseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser jFileChooser = new JFileChooser(projectDir.getAbsolutePath());
                jFileChooser.setVisible(true);
                jFileChooser.setDialogTitle("Choose your project game Directory");
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = jFileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    if (jFileChooser.getSelectedFile().isDirectory()) {
                        gameDirectory.setText(jFileChooser.getSelectedFile() + "/" + gameName.getText());
                        projectDir = new File(gameDirectory.getText());
                    }
                }
            }
        });
        buildProjectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (isSelectedOk()) {
                    progressBar1.setValue(0);
                    buildProject();
                    progressBar1.setValue(100);
                    JOptionPane.showMessageDialog(null, "The build is done!");
                    progressBar1.setValue(0);
                }
            }

        });

        gameDirectory.addActionListener(e -> projectDir = new File(gameDirectory.getText()));
        gameName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!gameName.getText().isEmpty()) {
                    gameDirectory.setText(projectDir.getParentFile().getAbsolutePath() + "/" + gameName.getText());
                    projectDir = new File(gameDirectory.getText());
                    String[] packages = (gamePackage.getText()).split("\\.");
                    packages[packages.length - 1] = gameName.getText()
                            .replace(" ", "")
                            .replace(".", "")
                            .toLowerCase();
                    String p = "";
                    for (String string : packages) {
                        if (!p.isEmpty()) p += ".";
                        p += string;
                    }
                    gamePackage.setText(p);
                }
            }
        });
    }

    private void buildProject() {
        progressBar1.setValue(10);
        modules = "";
        addCore();
        addDependencies();
        specialWords.put("coreDependencies", coreDependencies);
        progressBar1.setValue(20);
        if (desktop.isSelected()) {
            addDesktop();
        } else {
            specialWords.put("desktopDependencies", "");

        }
        progressBar1.setValue(30);
        if (android.isSelected()) {
            addAndroid();
        } else {
            specialWords.put("androidDependencies", "");
            specialWords.put("androidClasspath", "");
        }
        progressBar1.setValue(40);
        if (ios.isSelected()) {
            addIos();
        } else {
            specialWords.put("iosDependencies", "");
        }
        progressBar1.setValue(50);
        if (vr.isSelected()) {
            addVr();
        } else {
            specialWords.put("vrDependencies", "");
        }

        progressBar1.setValue(60);

        specialWords.put("jmeV", jmeVersion.getText() + "-" + jmeRelease.getModel().getSelectedItem());
        fileUtils.createFileFromTmp(projectDir, "build.gradle", "template/build.gradle");
        fileUtils.createFileFromContent(projectDir, "settings.gradle", "include 'core','assets'" + modules);
        progressBar1.setValue(80);
    }

    private void addCore() {
        specialWords.put("package", gamePackage.getText());
        specialWords.put("gameName", gameName.getText());
        projectDir.mkdir(); //the project folder
        File gradleDir = new File(projectDir.getPath() + "/gradle/wrapper");
        gradleDir.mkdirs();
        File assetsDir = new File(projectDir.getPath() + "/assets");
        assetsDir.mkdir(); //assets folder
        // sub assets folders
        new File(assetsDir.getPath() + "/Interface").mkdir();
        new File(assetsDir.getPath() + "/MatDefs").mkdir();
        new File(assetsDir.getPath() + "/Materials").mkdir();
        new File(assetsDir.getPath() + "/Models").mkdir();
        new File(assetsDir.getPath() + "/Scenes").mkdir();
        new File(assetsDir.getPath() + "/Shaders").mkdir();
        new File(assetsDir.getPath() + "/Sounds").mkdir();
        new File(assetsDir.getPath() + "/Textures").mkdir();

        File coreDir = new File(projectDir.getPath() + "/core"); //core folder
        coreDir.mkdir();
        File javaDir = new File(coreDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));// core java folder
        javaDir.mkdirs();
        fileUtils.createFileFromTmp(javaDir, "Main.java", "template/core/Main.java");
        fileUtils.createFileFromTmp(coreDir, "build.gradle", "template/core/build.gradle");
        // add jme3 core dependency to Core module
        coreDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-core:$JMonkey_version\"\n";
        // make gradle wrapper files
        fileUtils.createFileFromTmp(gradleDir, "gradle-wrapper.properties", "template/gradle/wrapper/gradle-wrapper.properties");
        fileUtils.copyFile("template/gradle/wrapper/gradle-wrapper.jar", gradleDir + "/gradle-wrapper.jar");
        progressBar1.setValue(30);

    }

    private void addDesktop() {
        File desktopDir = new File(projectDir.getPath() + "/desktop");
        desktopDir.mkdir();
        File desktopJavaDir = new File(desktopDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        desktopJavaDir.mkdirs();
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

    private void addAndroid() {
        File androidDir = new File(projectDir.getPath() + "/android");
        androidDir.mkdir();
        File androidMainDir = new File(androidDir.getPath() + "/src/main");
        androidMainDir.mkdirs();
        File androidJavaDir = new File(androidMainDir.getPath() + "/java/" + gamePackage.getText().replace(".", "/"));
        androidJavaDir.mkdirs();
        File androidRes = new File(androidMainDir + "/res");
        androidRes.mkdirs();
        File androidValues = new File(androidRes.getPath() + "/values");
        androidValues.mkdir();
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

    private void addIos() {
        File iosDir = new File(projectDir.getPath() + "/ios");
        iosDir.mkdir();
        File iosJavaDir = new File(iosDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        iosJavaDir.mkdirs();
        fileUtils.createFileFromTmp(iosJavaDir, "IosLauncher.java", "template/ios/IosLauncher.java");
        fileUtils.createFileFromTmp(iosDir, "build.gradle", "template/ios/build.gradle");
        // add ios necessary dependencies
        iosDependencies = "project(\":ios\") {\n" + "\t\tapply plugin: \"java\"\n" + "\t\tdependencies {\n" + "\t\t\tcompile project(\":core\")\n" + "\t\t\tcompile \"org.jmonkeyengine:jme3-ios:$JMonkey_version\"\n" + iosDependencies + "\n\t}\n}";
        specialWords.put("iosDependencies", iosDependencies);
        modules = modules + ", 'ios'";

    }

    private void addVr() {
        File vrDir = new File(projectDir.getPath() + "/vr");
        vrDir.mkdir();
        File vrJavaDir = new File(vrDir.getPath() + "/src/main/java/" + gamePackage.getText().replace(".", "/"));
        vrJavaDir.mkdirs();
        fileUtils.createFileFromTmp(vrJavaDir, "VrLauncher.java", "template/vr/VrLauncher.java");
        fileUtils.createFileFromTmp(vrDir, "build.gradle", "template/vr/build.gradle");
        //add vr necessary dependencies
        vrDependencies = "project(\":vr\") {\n" + "\t\tapply plugin: \"java\"\n" + "\t\tdependencies {\n" + "\t\t\tcompile project(\":core\")\n" + "\t\t\tcompile \"org.jmonkeyengine:jme3-vr:$JMonkey_version\"\n" + vrDependencies + "\n\t}\n}";
        specialWords.put("vrDependencies", vrDependencies);
        modules = modules + ", 'vr'";
    }

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

    /*
     *if the selected modules are compatible with all the dependencies
     * and one module at least is selected it will return true
     * if not show an alert to the user
     */

    private boolean isSelectedOk() {
        if (gameName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Game name can't be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (gamePackage.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Package can't be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;

        } else if (gameDirectory.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Directory can't be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (projectDir.getAbsoluteFile().exists()) {
            JOptionPane.showMessageDialog(null, "Directory already exist choose an other one!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (jBullet.isSelected() && bullet.isSelected()) {
            JOptionPane.showMessageDialog(null, "You can't choose JBullet an Bullet at the same time!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (!(desktop.isSelected() || android.isSelected() || ios.isSelected() || vr.isSelected())) {
            JOptionPane.showMessageDialog(null, "You need to select at least one module!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (blender.isSelected() && (android.isSelected() || ios.isSelected() || vr.isSelected())) {
            JOptionPane.showMessageDialog(null, "Blender is compatible only with Desktop!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (bullet.isSelected() && (ios.isSelected() || vr.isSelected())) {
            JOptionPane.showMessageDialog(null, "Native Bullet is not compatible with Ios and VR\n use JBullet instead!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            return true;
        }

    }

}