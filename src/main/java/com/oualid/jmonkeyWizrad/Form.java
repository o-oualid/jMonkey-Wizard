package com.oualid.jmonkeyWizrad;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;


/**
 * Created by: ouazrou-oualid on: 26/01/2018 package: com.oualid.jmonkeyWizrad project: JMonkey Wizard.
 */

class Form {
    private JCheckBox bulletCheckBox;
    private JCheckBox JBulletCheckBox;
    private JCheckBox JOGGCheckBox;
    private JCheckBox pluginsCheckBox;
    public JPanel mainPanel;
    private JCheckBox androidCheckBox;
    private JCheckBox desktopCheckBox;
    private JCheckBox iosCheckBox;
    private JCheckBox VRCheckBox;
    private JTextField myGameTextField;
    private JButton buildProjectButton;
    private JProgressBar progressBar1;
    private JCheckBox niftyGUICheckBox;
    private JButton browseButton;
    private JCheckBox Terrain;
    private JCheckBox effects;
    private JCheckBox blender;
    private JCheckBox jogl;
    private JCheckBox examples;
    private JTextField jmeVersion;
    private JComboBox jmeRelease;
    private JTextField directory;
    private JTextField gamePackage;
    private BufferedWriter bw = null;
    private BufferedReader br = null;
    private FileWriter fw = null;
    private FileReader fr = null;
    private HashMap<String, String> specialWords = new HashMap();
    private String coreDependencies, desktopDependencies, androidDependencies, iosDependencies, vrDependencies;

    Form() {
        File projectDir = FileSystemView.getFileSystemView().getDefaultDirectory();
        directory.setText(projectDir.getAbsolutePath());
        browseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setVisible(true);
                jFileChooser.setDialogTitle("Choose your project directory");
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = jFileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    if (jFileChooser.getSelectedFile().isDirectory()) {
                        File projectDir = jFileChooser.getSelectedFile();
                        directory.setText(projectDir.getAbsolutePath());
                    }
                }
            }
        });
        buildProjectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                buildProject();
            }
        });
    }

    private void buildProject() {

        specialWords.put("package", gamePackage.getText());
        File projectDir = new File(directory.getText() + "\\" + myGameTextField.getText());
        projectDir.mkdir(); //the project folder
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
        // add the necessary dependency to all modules
        coreDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-core:$JMonkey_version\"\n";

        // if the desktopCheckBox is selected create desktop module files and folders
        if (desktopCheckBox.isSelected()) {
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
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-desktop:$JMonkey_version\"\n" +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-lwjgl3:$JMonkey_version\"\n";

        }
        // if the android checkBox is selected create android module files and folders
        if (androidCheckBox.isSelected()) {
            File androidDir = new File(projectDir.getPath() + "\\android");
            androidDir.mkdir();
            File androidJavaDir = new File(androidDir.getPath() + "\\src\\java\\" + gamePackage.getText().replace(".", "\\"));
            androidJavaDir.mkdirs();
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
        if (iosCheckBox.isSelected()) {
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
        if (VRCheckBox.isSelected()) {
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
        addDependencies();// call add addDependencies method to add the selected dependencies

        specialWords.put("coreDependencies", coreDependencies);
        // if module is selected add his dependencies to gradle build file
        if (androidCheckBox.isSelected()) {
            specialWords.put("androidDependencies", androidDependencies + "\n\t}\n}");
        } else {
            specialWords.put("androidDependencies", "");
        }
        if (desktopCheckBox.isSelected()) {
            specialWords.put("desktopDependencies", desktopDependencies + "\n\t}\n}");
        } else {
            specialWords.put("desktopDependencies", "");
        }
        if (iosCheckBox.isSelected()) {
            specialWords.put("iosDependencies", iosDependencies + "\n\t}\n}");
        } else {
            specialWords.put("iosDependencies", "");
        }
        if (VRCheckBox.isSelected()) {
            specialWords.put("vrDependencies", vrDependencies + "\n\t}\n}");
        } else {
            specialWords.put("vrDependencies", "");
        }
        specialWords.put("jmeV", jmeVersion.getText() + "-" + jmeRelease.getModel().getSelectedItem());
        createFile(projectDir, "build.gradle", "template/build.gradle");
    }

    private void addDependencies() {

        if (bulletCheckBox.isSelected()) {
            androidDependencies = androidDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-bullet-native-android:$JMonkey_version\"";
        }
        if (JBulletCheckBox.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-jbullet:$JMonkey_version\"\n";

        }
        if (Terrain.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-terrain:$JMonkey_version\"\n";

        }
        if (niftyGUICheckBox.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-niftygui:$JMonkey_version\"\n";

        }
        if (effects.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-effects:$JMonkey_version\"\n";

        }
        if (pluginsCheckBox.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-plugins:$JMonkey_version\"\n";

        }
        if (blender.isSelected()) {
            desktopDependencies = desktopDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-blender:$JMonkey_version\"\n";

        }
        if (JOGGCheckBox.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-jogg:$JMonkey_version\"\n";
        }
        if (jogl.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-jogl:$JMonkey_version\"\n";

        }
        if (examples.isSelected()) {
            coreDependencies = coreDependencies +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-examples:$JMonkey_version\"\n";
        }
    }
// this method create files using templates
    private void createFile(File path, String name, String tmp) {
        File mainClass = new File(path.getPath() + "\\" + name);
        String mainClassTmp = "";
        try {
            mainClass.createNewFile();
            fr = new FileReader(tmp);
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                if (mainClassTmp.isEmpty()) {
                    mainClassTmp = sCurrentLine;
                } else {
                    mainClassTmp = mainClassTmp + "\n" + sCurrentLine;
                }
            }
            for (String key : specialWords.keySet()) {
                mainClassTmp = mainClassTmp.replace("${" + key + "}", specialWords.get(key));
            }
            fw = new FileWriter(mainClass.getAbsolutePath());
            bw = new BufferedWriter(fw);
            bw.write(mainClassTmp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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

    }

}