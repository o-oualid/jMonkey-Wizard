package com.oualid.jmonkeyWizrad;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;


/**
 * Created by: ouazrou-oualid on: 26/01/2018 package: com.oualid.jmonkeyWizrad project: JMonkey Wizard.
 */

/*
 * ${gameName} for the game name
 * ${package} for the game package
 * ${coreDependencies}
 * ${desktopDependencies}
 * ${androidDependencies}
 * ${iosDependencies}
 * ${vrDependencies}
 */

class Form {
    public JPanel mainPanel;
    private JRadioButton bullet;
    private JRadioButton jBullet;
    private JRadioButton jogl;
    private JRadioButton lwjgl;
    private JRadioButton lwjgl3;
    private JCheckBox jogg;
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
    private BufferedWriter bw = null;
    private BufferedReader br = null;
    private FileWriter fw = null;
    private FileReader fr = null;
    private HashMap<String, String> specialWords = new HashMap<>();
    private String coreDependencies, desktopDependencies, androidDependencies, iosDependencies, vrDependencies;

    //the constructor of this class init the listeners

    Form() {
        File projectDir = FileSystemView.getFileSystemView().getDefaultDirectory();
        gameDirectory.setText(projectDir.getAbsolutePath() + "\\" + gameName.getText());
        browseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setVisible(true);
                jFileChooser.setDialogTitle("Choose your project gameDirectory");
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = jFileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    if (jFileChooser.getSelectedFile().isDirectory()) {
                        File projectDir = jFileChooser.getSelectedFile();
                        gameDirectory.setText(projectDir.getAbsolutePath() + "\\" + gameName.getText());
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
                    try {
                        buildProject();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    progressBar1.setValue(100);
                    JOptionPane.showMessageDialog(null, "the build is done");
                }
            }

        });
        gameName.addActionListener(actionEvent -> gameDirectory.setText(projectDir.getAbsolutePath() + "\\" + gameName.getText()));

    }
    
    private void buildProject() throws IOException {
        progressBar1.setValue(10);
        String modules = "";
        specialWords.put("package", gamePackage.getText());
        specialWords.put("gameName", gameName.getText());
        File projectDir = new File(gameDirectory.getText());
        projectDir.mkdir(); //the project folder
        File gradleDir = new File(projectDir.getPath() + "\\gradle\\wrapper");
        gradleDir.mkdirs();
        File assetsDir = new File(projectDir.getPath() + "\\assets");
        assetsDir.mkdir(); //assets folder
        // sub assets folders
        new File(assetsDir.getPath() + "\\Interface").mkdir();
        new File(assetsDir.getPath() + "\\MatDefs").mkdir();
        new File(assetsDir.getPath() + "\\Materials").mkdir();
        new File(assetsDir.getPath() + "\\Models").mkdir();
        new File(assetsDir.getPath() + "\\Scenes").mkdir();
        new File(assetsDir.getPath() + "\\Shaders").mkdir();
        new File(assetsDir.getPath() + "\\Sounds").mkdir();
        new File(assetsDir.getPath() + "\\Textures").mkdir();

        File coreDir = new File(projectDir.getPath() + "\\core"); //core folder
        coreDir.mkdir();
        File javaDir = new File(coreDir.getPath() + "\\src\\java\\" + gamePackage.getText().replace(".", "\\"));// core java folder
        javaDir.mkdirs();
        createFile(javaDir, "Main.java", "template/core/Main.java");
        createFile(coreDir, "build.gradle", "template/desktop/build.gradle");
        // add the necessary dependency to all modules
        coreDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-core:$JMonkey_version\"\n";
        // make gradle warrper files
        createFile(gradleDir, "gradle-wrapper.properties", "template/gradle/wrapper/gradle-wrapper.properties");
        copyFile("template/gradle/wrapper/gradle-wrapper.jar", gradleDir + "/gradle-wrapper.jar");

        // if the desktop is selected create desktop module files and folders
        if (desktop.isSelected()) {
            File desktopDir = new File(projectDir.getPath() + "\\desktop");
            desktopDir.mkdir();
            File desktopJavaDir = new File(desktopDir.getPath() + "\\src\\java\\" + gamePackage.getText().replace(".", "\\"));
            desktopJavaDir.mkdirs();
            createFile(desktopJavaDir, "DesktopLauncher.java", "template/desktop/DesktopLauncher.java");
            createFile(desktopDir, "build.gradle", "template/desktop/build.gradle");
            //add desktop necessary dependencies
            desktopDependencies = "project(\":desktop\") {\n" +
                    "\t\tapply plugin: \"java\"\n" +
                    "\t\tapply plugin: \"idea\"\n" +
                    "\t\tidea {\n" +
                    "\t\t\tmodule {\n" +
                    "\t\t\t\tscopes.PROVIDED.minus += [configurations.compile]\n" +
                    "\t\t\t\tscopes.COMPILE.plus += [configurations.compile]\n" +
                    "        }\n" +
                    "    }\n" +
                    "\t\tdependencies {\n" +
                    "\t\t\tcompile project(\":core\")\n" +
                    "\t\t\tcompile \"org.junit.platform:junit-platform-launcher:$junitPlatform_version\"\n" +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-desktop:$JMonkey_version\"\n";
            if (lwjgl3.isSelected()) {
                desktopDependencies = desktopDependencies +
                        "\t\t\tcompile \"org.jmonkeyengine:jme3-lwjgl3:$JMonkey_version\"\n";
            } else if (lwjgl.isSelected()) {
                desktopDependencies = desktopDependencies +
                        "\t\t\tcompile \"org.jmonkeyengine:jme3-lwjgl:$JMonkey_version\"\n";
            } else if (jogl.isSelected()) {
                desktopDependencies = desktopDependencies +
                        "\t\t\tcompile \"org.jmonkeyengine:jme3-jogl:$JMonkey_version\"\n";
            }


        }
        // if the android checkBox is selected create android module files and folders
        if (android.isSelected()) {
            File androidDir = new File(projectDir.getPath() + "\\android");
            androidDir.mkdir();
            File androidJavaDir = new File(androidDir.getPath() + "\\src\\java\\" + gamePackage.getText().replace(".", "\\"));
            androidJavaDir.mkdirs();
            File androidRes = new File(androidDir.getPath() + "/src/res");
            androidRes.mkdirs();

            new File(androidRes.getPath() + "/mipmap-xxxhdpi").mkdir();
            new File(androidRes.getPath() + "/mipmap-xxhdpi").mkdir();
            new File(androidRes.getPath() + "/mipmap-xhdpi").mkdir();
            new File(androidRes.getPath() + "/mipmap-hdpi").mkdir();
            new File(androidRes.getPath() + "/mipmap-mdpi").mkdir();
            new File(androidRes.getPath() + "/mipmap-anydpi-v26").mkdir();
            new File(androidRes.getPath() + "/drawable").mkdir();
            new File(androidRes.getPath() + "/drawable-v24").mkdir();

            File androidValues = new File(androidDir.getPath() + "/src/values");
            androidValues.mkdir();

            createFile(androidValues, "colors.xml", "template/android/src/main/res/values/colors.xml");
            createFile(androidValues, "strings.xml", "template/android/src/main/res/values/strings.xml");
            createFile(androidValues, "styles.xml", "template/android/src/main/res/values/styles.xml");

            createFile(androidJavaDir, "AndroidLauncher.java", "template/android/AndroidLauncher.java");
            createFile(androidDir, "build.gradle", "template/android/build.gradle");
            createFile(androidDir, "proguard-rules.pro", "template/android/proguard-rules.pro");
            // add android necessary dependencies
            androidDependencies = "project(\":android\") {\n" +
                    "\t\tapply plugin: \"android\"\n" +
                    "\t\tdependencies {\n" +
                    "\t\t\tcompile project(\":core\")\n" +
                    "\t\t\tcompile fileTree(dir: 'libs', include: ['*.jar'])\n" +
                    "\t\t\ttestCompile 'junit:junit:4.12'\n" +
                    "\t\t\tcompile 'com.android.support:appcompat-v7:27.0.2'\n" +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-android:$JMonkey_version\"\n" +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-android-native:$JMonkey_version\"\n";
        }
        // if the ios checkBox is selected create ios module files and folders
        if (ios.isSelected()) {
            File iosDir = new File(projectDir.getPath() + "\\ios");
            iosDir.mkdir();
            File iosJavaDir = new File(iosDir.getPath() + "\\src\\java\\" + gamePackage.getText().replace(".", "\\"));
            iosJavaDir.mkdirs();
            createFile(iosJavaDir, "IosLauncher.java", "template/ios/IosLauncher.java");
            createFile(iosDir, "build.gradle", "template/ios/build.gradle");
            // add ios necessary dependencies
            iosDependencies = "project(\":ios\") {\n" +
                    "\t\tapply plugin: \"java\"\n" +
                    "\t\tdependencies {\n" +
                    "\t\t\tcompile project(\":core\")\n" +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-ios:$JMonkey_version\"\n";

        }
        // if the vr checkBox is selected create vr module files and folders
        if (vr.isSelected()) {
            File vrDir = new File(projectDir.getPath() + "\\vr");
            vrDir.mkdir();
            File vrJavaDir = new File(vrDir.getPath() + "\\src\\java\\" + gamePackage.getText().replace(".", "\\"));
            vrJavaDir.mkdirs();
            createFile(vrJavaDir, "VrLauncher.java", "template/vr/VrLauncher.java");
            createFile(vrDir, "build.gradle", "template/vr/build.gradle");
            //add vr necessary dependencies
            vrDependencies = "project(\":vr\") {\n" +
                    "\t\tapply plugin: \"java\"\n" +
                    "\t\tdependencies {\n" +
                    "\t\t\tcompile project(\":core\")\n" +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-vr:$JMonkey_version\"\n";

        }
        progressBar1.setValue(50);
        addDependencies();// call add addDependencies method to add the selected dependencies
        progressBar1.setValue(70);
        specialWords.put("coreDependencies", coreDependencies);
        // if module is selected add his dependencies to gradle build file
        if (android.isSelected()) {
            specialWords.put("androidDependencies", androidDependencies + "\n\t}\n}");
            modules = modules + ", 'android'";
        } else {
            specialWords.put("androidDependencies", "");
        }
        if (desktop.isSelected()) {
            specialWords.put("desktopDependencies", desktopDependencies + "\n\t}\n}");
            modules = modules + ", 'desktop'";
        } else {
            specialWords.put("desktopDependencies", "");
        }
        if (ios.isSelected()) {
            specialWords.put("iosDependencies", iosDependencies + "\n\t}\n}");
            modules = modules + ", 'ios'";
        } else {
            specialWords.put("iosDependencies", "");
        }
        if (vr.isSelected()) {
            specialWords.put("vrDependencies", vrDependencies + "\n\t}\n}");
            modules = modules + ", 'vr'";
        } else {
            specialWords.put("vrDependencies", "");
        }

        specialWords.put("jmeV", jmeVersion.getText() + "-" + jmeRelease.getModel().getSelectedItem());
        createFile(projectDir, "build.gradle", "template/build.gradle");
        newFile(projectDir, "settings.gradle",
                "include 'core','assets'" + modules);
        progressBar1.setValue(90);
    }

    //this method create files using String

    private void addDependencies() {

        if (bullet.isSelected()) {
            desktopDependencies = desktopDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-bullet-native:$JMonkey_version\"\n";
            androidDependencies = androidDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-bullet-native-android:$JMonkey_version\"\n";
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-bullet:$JMonkey_version\"\n";
        } else if (jBullet.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-jbullet:$JMonkey_version\"\n";
        }
        if (terrain.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-terrain:$JMonkey_version\"\n";
        }
        if (niftyGUI.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-niftygui:$JMonkey_version\"\n";

        }
        if (effects.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-effects:$JMonkey_version\"\n";

        }
        if (plugins.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-plugins:$JMonkey_version\"\n";

        }
        if (blender.isSelected()) {
            desktopDependencies = desktopDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-blender:$JMonkey_version\"\n";

        }
        if (jogg.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-jogg:$JMonkey_version\"\n";
        }
        if (networking.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-networking:$JMonkey_version\"\n";
        }
        if (examples.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-examples:$JMonkey_version\"\n";
        }
    }

    // this method get templates file content and convert them then give them to newFile method

    private void createFile(File path, String name, String tmpPath) {
        String tempContent = "";
        try {
            fr = new FileReader(tmpPath);
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                if (tempContent.isEmpty()) {
                    tempContent = sCurrentLine;
                } else {
                    tempContent = tempContent + "\n" + sCurrentLine;
                }
            }
            for (String key : specialWords.keySet()) {
                tempContent = tempContent.replace("${" + key + "}", specialWords.get(key));
            }
            if (!newFile(path, name, tempContent)) {
                JOptionPane.showMessageDialog(null,
                        "Failed t0 create file: " + path + "/" + name + "\n this generally happen when the file is already exist!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFile();
        }
    }

    // this this method create files with content

    private boolean newFile(File path, String name, String content) {
        File mainClass = new File(path.getPath() + "\\" + name);
        try {
            if (!mainClass.createNewFile()) {
                return false;
            }
            fw = new FileWriter(mainClass.getAbsolutePath());
            bw = new BufferedWriter(fw);
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeFile();
        }
    }

    //this method copy a file from the template folder and put it in the selected path

    private void copyFile(String source, String dir) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File(source));
            os = new FileOutputStream(new File(dir));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    // close the opened file

    private void closeFile() {
        try {
            if (bw != null)
                bw.close();

            if (fw != null)
                fw.close();

            if (br != null) {
                br.close();
            }
            if (fr != null) {
                fr.close();
            }
            bw = null;
            fw = null;
            br = null;
            fr = null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*
     *if the selected modules are compatible withe all the dependencies it will return true
     * if not show an alert to the user
     */

    private boolean isSelectedOk() {
        if (blender.isSelected() && (android.isSelected() || ios.isSelected() || vr.isSelected())) {
            JOptionPane.showMessageDialog(null, "Blender is compatible only with Desktop");
            return false;
        } else if (bullet.isSelected() && (ios.isSelected() || vr.isSelected())) {
            JOptionPane.showMessageDialog(null, "Native Bullet is not compatible with Ios and VR\n use JBullet instead");
            return false;
        } else {
            return true;
        }
    }


}