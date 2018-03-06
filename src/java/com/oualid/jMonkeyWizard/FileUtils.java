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

    /**
     * this method copy file from template then give it to {@link FileUtils#createFileFromContent}
     *
     * @param path    path to where you want to create the file
     * @param name    the file name
     * @param tmpPath the template file directory in String
     */

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
            messages.setText(messages.getText() + "\u26A0 Failed to create file: " + path + "/" + name + "\n");
            e.printStackTrace();
        } finally {
            closeFile();
        }
    }

    /**
     * this method create file and put Sting content on it.
     *
     * @param path    path to where you want to create the file
     * @param name    the file name
     * @param content the content that will be in the file.
     */

    void createFileFromContent(File path, String name, String content) {
        File file = new File(path.getPath() + "/" + name);
        try {
            fw = new FileWriter(file.getAbsolutePath());
            bw = new BufferedWriter(fw);
            bw.write(content);
            messages.setText(messages.getText() + "File successfully created: " + path + "/" + name + "\n");
        } catch (IOException e) {
            messages.setText(messages.getText() + "\u26A0 Failed to create file: " + path + "/" + name + "\n");
        } finally {
            closeFile();
        }
    }

    /**
     * this method copy a file from the template folder and put it is the selected path
     *
     * @param source source file need to be File not directory
     * @param dir    the file destination
     */


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
            messages.setText(messages.getText() + "\u26A0 Failed to copy file from: " + source + " to: " + dir + "\n");
        } finally {
            closeFile();
        }
    }

    /**
     * close the opened file
     * called after using bw,fw...
     */

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

    /**
     * this method copy a directory with all it content to the des directory
     * Note:this method will not work in jar file directory in jar ar not actually directory.
     *
     * @param sourceLocation the source directory
     * @param targetLocation the targeted directory
     */
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
            messages.setText(messages.getText() + "\u26A0 The template Directory: " + sourceFile.getAbsolutePath() + " is empty!\n");
        }
    }

    /**
     * this method create new directory then return it.
     *
     * @param directory the directory path in String
     * @return file type File
     */
    File newDir(String directory) {
        File file = new File(directory);
        if (file.mkdirs()) {
            messages.setText(messages.getText() + "Folder successfully created: " + file.getPath() + "\n");
        } else {
            messages.setText(messages.getText() + "\u26A0 Failed to create folder: " + file.getPath() + "\n");
        }
        return file;

    }
}
