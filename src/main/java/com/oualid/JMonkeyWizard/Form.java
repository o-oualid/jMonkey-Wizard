package com.oualid.JMonkeyWizard;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

/**
 * Created by: ouazrou-oualid on: 26/01/2018 package: com.oualid.JMonkeyWizard project: JMonkey Wizard.
 */

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
    private ClassLoader classLoader = getClass().getClassLoader();
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
    private String coreDependencies, desktopDependencies = "", androidDependencies = "", iosDependencies = "", vrDependencies = "";
    private File projectDir;
    private InputStream is;
    private OutputStream out;
    //the constructor of this class init the listeners

    Form() {
        projectDir = new File(gameName.getText());
        gameDirectory.setText(projectDir.getAbsolutePath());
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
                    try {
                        buildProject();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    progressBar1.setValue(100);
                    JOptionPane.showMessageDialog(null, "The build is done!");
                    progressBar1.setValue(0);
                }
            }

        });

        gameDirectory.addActionListener(e -> projectDir = new File(gameDirectory.getText()));

        gameName.addActionListener(actionEvent -> {
            gameDirectory.setText(projectDir.getParentFile().getAbsolutePath() + "/" + gameName.getText());
            projectDir = new File(gameDirectory.getText());
        });

    }

    private void buildProject() throws IOException {
        progressBar1.setValue(10);
        String modules = "";
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
        File javaDir = new File(coreDir.getPath() +
                "/src/java/" + gamePackage.getText().replace(".", "/"));// core java folder
        javaDir.mkdirs();
        createFileFromTmp(javaDir, "Main.java", "template/core/Main.java");
        createFileFromTmp(coreDir, "build.gradle", "template/core/build.gradle");
        // add the necessary dependency to all modules
        coreDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-core:$JMonkey_version\"\n";
        // make gradle wrapper files
        createFileFromTmp(gradleDir, "gradle-wrapper.properties", "template/gradle/wrapper/gradle-wrapper.properties");
        copyFile("template/gradle/wrapper/gradle-wrapper.jar", gradleDir + "/gradle-wrapper.jar");
        progressBar1.setValue(30);
        addDependencies();// call add addDependencies method to add the selected dependencies

        specialWords.put("coreDependencies", coreDependencies);

        // if the desktop is selected create desktop module files and folders
        if (desktop.isSelected()) {
            File desktopDir = new File(projectDir.getPath() + "/desktop");
            desktopDir.mkdir();
            File desktopJavaDir = new File(desktopDir.getPath()
                    + "/src/java/" + gamePackage.getText().replace(".", "/"));
            desktopJavaDir.mkdirs();
            createFileFromTmp(desktopJavaDir, "DesktopLauncher.java", "template/desktop/DesktopLauncher.java");
            createFileFromTmp(desktopDir, "build.gradle", "template/desktop/build.gradle");
            //add desktop necessary dependencies
            if (lwjgl3.isSelected()) {
                desktopDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-lwjgl3:$JMonkey_version\"\n";
            } else if (lwjgl.isSelected()) {
                desktopDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-lwjgl:$JMonkey_version\"\n";
            } else if (jogl.isSelected()) {
                desktopDependencies = "\t\t\tcompile \"org.jmonkeyengine:jme3-jogl:$JMonkey_version\"\n";
            }
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
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-desktop:$JMonkey_version\"\n" + desktopDependencies + "\n\t}\n}";
            specialWords.put("desktopDependencies", desktopDependencies);
            modules = modules + ", 'desktop'";
        } else {
            specialWords.put("desktopDependencies", "");
        }
        // if the android checkBox is selected create android module files and folders
        if (android.isSelected()) {
            File androidDir = new File(projectDir.getPath() + "/android");
            androidDir.mkdir();
            File androidJavaDir = new File(androidDir.getPath() +
                    "/src/java/" + gamePackage.getText().replace(".", "/"));
            androidJavaDir.mkdirs();
            File androidRes = new File(androidDir.getPath() + "/src/res");
            androidRes.mkdirs();
            copyDirectory(new File(classLoader.getResource("template/android/res").getFile()), androidRes, "template/android/res");
            File androidValues = new File(androidRes.getPath() + "/values");
            androidValues.mkdir();

            createFileFromTmp(androidValues, "strings.xml", "template/android/res/values/strings.xml");
            createFileFromTmp(androidJavaDir, "AndroidLauncher.java", "template/android/AndroidLauncher.java");
            createFileFromTmp(androidDir, "build.gradle", "template/android/build.gradle");
            createFileFromTmp(androidDir, "proguard-rules.pro", "template/android/proguard-rules.pro");
            createFileFromTmp(androidDir, "AndroidManifest.xml", "template/android/src/main/AndroidManifest.xml");

            // add android necessary dependencies
            androidDependencies = "project(\":android\") {\n" +
                    "\t\tapply plugin: \"android\"\n" +
                    "\t\tdependencies {\n" +
                    "\t\t\tcompile project(\":core\")\n" +
                    "\t\t\tcompile fileTree(dir: 'libs', include: ['*.jar'])\n" +
                    "\t\t\ttestCompile 'junit:junit:4.12'\n" +
                    "\t\t\tcompile 'com.android.support:appcompat-v7:27.0.2'\n" +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-android:$JMonkey_version\"\n" +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-android-native:$JMonkey_version\"\n" + androidDependencies + "\n\t}\n}";
            specialWords.put("androidDependencies", androidDependencies);
            modules = modules + ", 'android'";
        } else {
            specialWords.put("androidDependencies", "");
        }

        // if the ios checkBox is selected create ios module files and folders
        if (ios.isSelected()) {
            File iosDir = new File(projectDir.getPath() + "/ios");
            iosDir.mkdir();
            File iosJavaDir = new File(iosDir.getPath() + "/src/java/" +
                    gamePackage.getText().replace(".", "/"));
            iosJavaDir.mkdirs();
            createFileFromTmp(iosJavaDir, "IosLauncher.java", "template/ios/IosLauncher.java");
            createFileFromTmp(iosDir, "build.gradle", "template/ios/build.gradle");
            // add ios necessary dependencies
            iosDependencies = "project(\":ios\") {\n" +
                    "\t\tapply plugin: \"java\"\n" +
                    "\t\tdependencies {\n" +
                    "\t\t\tcompile project(\":core\")\n" +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-ios:$JMonkey_version\"\n" + iosDependencies + "\n\t}\n}";
            specialWords.put("iosDependencies", iosDependencies);
            modules = modules + ", 'ios'";
        } else {
            specialWords.put("iosDependencies", "");
        }
        // if the vr checkBox is selected create vr module files and folders
        if (vr.isSelected()) {
            File vrDir = new File(projectDir.getPath() + "/vr");
            vrDir.mkdir();
            File vrJavaDir = new File(vrDir.getPath() + "/src/java/" +
                    gamePackage.getText().replace(".", "/"));
            vrJavaDir.mkdirs();
            createFileFromTmp(vrJavaDir, "VrLauncher.java", "template/vr/VrLauncher.java");
            createFileFromTmp(vrDir, "build.gradle", "template/vr/build.gradle");
            //add vr necessary dependencies
            vrDependencies = "project(\":vr\") {\n" +
                    "\t\tapply plugin: \"java\"\n" +
                    "\t\tdependencies {\n" +
                    "\t\t\tcompile project(\":core\")\n" +
                    "\t\t\tcompile \"org.jmonkeyengine:jme3-vr:$JMonkey_version\"\n" + vrDependencies + "\n\t}\n}";
            specialWords.put("vrDependencies", vrDependencies);
            modules = modules + ", 'vr'";
        } else {
            specialWords.put("vrDependencies", "");
        }

        progressBar1.setValue(60);

        specialWords.put("jmeV", jmeVersion.getText() + "-" + jmeRelease.getModel().getSelectedItem());
        createFileFromTmp(projectDir, "build.gradle", "template/build.gradle");
        createFileFromContent(projectDir, "settings.gradle", "include 'core','assets'" + modules);
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

    // this method get templates file content and convert them then give them to createFileFromContent method

    private void createFileFromTmp(File path, String name, String tmpPath) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(tmpPath)));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line + "\n");

            }


            for (String key : specialWords.keySet()) {
                out = new StringBuilder(out.toString().replace("${" + key + "}", specialWords.get(key)));
            }
            createFileFromContent(path, name, out.toString());


        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e, "ERROR!", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeFile();
        }
    }

    // this this method create files with content

    private void createFileFromContent(File path, String name, String content) {
        File file = new File(path.getPath() + "/" + name);
        try {
            fw = new FileWriter(file.getAbsolutePath());
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e, "ERROR!", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeFile();
        }
    }

    //this method copy a file from the template folder and put it is the selected path

    private void copyFile(String source, String dir) throws IOException {
        try {
            is = ClassLoader.getSystemResourceAsStream(source);
            out = new FileOutputStream(new File(dir));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e, "ERROR!", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeFile();
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
            if (is != null) {
                is.close();
            }
            if (out != null) {
                out.close();
            }
            is = null;
            out = null;
            bw = null;
            fw = null;
            br = null;
            fr = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //this method copy a directory with all it content to the des directory
    private void copyDirectory(File sourceLocation, File targetLocation, String src) {
        try {
            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists()) {
                    targetLocation.mkdir();
                }
                String[] children = sourceLocation.list();
                for (String aChildren : children) {
                    copyDirectory(new File(sourceLocation, aChildren),
                            new File(targetLocation, aChildren), src);
                }
            } else {

                is = classLoader.getResourceAsStream(src);
                out = new FileOutputStream(targetLocation);

                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                is.close();
                out.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e, "EROOR!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     *if the selected modules are compatible with all the dependencies
     * and one module at least is selected it will return true
     * if not show an alert to the user
     */

    private boolean isSelectedOk() {
        if (projectDir.getAbsoluteFile().exists()) {
            JOptionPane.showMessageDialog(null,
                    "Directory already exist choose an other one!");
            return false;
        } else if (!(desktop.isSelected() || android.isSelected() || ios.isSelected() || vr.isSelected())) {
            JOptionPane.showMessageDialog(null,
                    "You need to select at least one module");
            return false;
        } else if (blender.isSelected() && (android.isSelected() || ios.isSelected() || vr.isSelected())) {
            JOptionPane.showMessageDialog(null,
                    "Blender is compatible only with Desktop");
            return false;
        } else if (bullet.isSelected() && (ios.isSelected() || vr.isSelected())) {
            JOptionPane.showMessageDialog(null,
                    "Native Bullet is not compatible with Ios and VR\n use JBullet instead");
            return false;
        } else {
            return true;
        }
    }
}