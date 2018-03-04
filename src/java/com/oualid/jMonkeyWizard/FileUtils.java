package com.oualid.jMonkeyWizard;

import javafx.scene.control.TextArea;
import java.io.*;

public class FileUtils {
    private InputStream is;
    private OutputStream out;
    private BufferedWriter bw = null;
    private BufferedReader br = null;
    private FileWriter fw = null;
    private FileReader fr = null;
    private TextArea messages;

    FileUtils(TextArea messages) {
        this.messages = messages;
    }

    void createFileFromTmp(File path, String name, String tmpPath) {

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(tmpPath)));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line).append("\n");

            }


            for (String key : Controller.specialWords.keySet()) {
                out = new StringBuilder(out.toString().replace("${" + key + "}", Controller.specialWords.get(key)));
            }
            createFileFromContent(path, name, out.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFile();
        }
    }

    // this this method create files with content

    void createFileFromContent(File path, String name, String content) {
        File file = new File(path.getPath() + "/" + name);
        try {
            fw = new FileWriter(file.getAbsolutePath());
            bw = new BufferedWriter(fw);
            bw.write(content);
            messages.setText(messages.getText() + "File successfully created: " + path + "/" + name + "\n");
        } catch (IOException e) {
            messages.setText(messages.getText() + "Failed to create file: " + path + "/" + name + "\n");
        } finally {
            closeFile();
        }
    }

    //this method copy a file from the template folder and put it is the selected path

    void copyFile(String source, String dir) {
        try {
            is = ClassLoader.getSystemResourceAsStream(source);
            out = new FileOutputStream(new File(dir));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            messages.setText(messages.getText() + "File copied from: " + source + " to: " + dir + "\n");
        } catch (IOException e) {
            messages.setText(messages.getText() + "Failed to copy file from: " + source + " to: " + dir + "\n");
        } finally {
            closeFile();
        }
    }

    // close the opened file

    private void closeFile() {
        try {
            if (bw != null) bw.close();

            if (fw != null) fw.close();

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
    void copyDirectory(String sourceLocation, String targetLocation) {
        File sourceFile = new File(sourceLocation);
        File[] children = sourceFile.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    File dir = newDir(targetLocation + "/" + child.getName());
                    copyDirectory(sourceLocation + "/" + child.getName(), dir.getAbsolutePath());
                } else if (child.isFile()) {
                    copyFile(sourceLocation + "/" + child.getName(), targetLocation + "/" + child.getName());
                }
            }
        } else {
            messages.setText(messages.getText() + "The template Directory: " + sourceFile.getAbsolutePath() + "is empty!\n");
        }
    }

    File newDir(String path) {
        File file = new File(path);
        if (file.mkdirs()) {
            messages.setText(messages.getText() + "Folder successfully created: " + file.getPath() + "\n");
        } else {
            messages.setText(messages.getText() + "Failed to create folder: " + file.getPath() + "\n");
        }
        return file;

    }
}
