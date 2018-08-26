package com.oualid.jMonkeyWizard;

import java.io.*;

class FileUtils {
    private InputStream is;
    private OutputStream out;
    private BufferedWriter bw = null;
    private BufferedReader br = null;
    private FileWriter fw = null;
    private FileReader fr = null;
    private Controller controller;

    FileUtils(Controller controller) {
        this.controller = controller;
    }

    /**
     * this method copy file from template then give it to {@link FileUtils#createFileFromContent}
     *
     * @param path     path to where you want to create the file
     * @param name     the file name
     * @param tmpPath  the template file directory in String
     * @param internal true if the emp file is inside the jar
     */

    void createFileFromTmp(File path, String name, String tmpPath, boolean internal) {
        try {
            BufferedReader reader;
            if (internal) {
                reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(tmpPath)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(tmpPath)));
            }
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
            controller.newMessage("\u26A0 Failed to create file from tmp: " + path + "/" + name);
            e.printStackTrace();
        } finally {
            closeFile();
        }
    }

    /**
     * this method create file and put String content on it.
     *
     * @param path    path to where you want to create the file
     * @param name    the file name
     * @param content the content that will be in the file.
     */

    void createFileFromContent(File path, String name, String content) {
        File file = new File(path.getAbsolutePath() + "/" + name);
        controller.newMessage(file.getAbsolutePath());

        try {
            fw = new FileWriter(file.getAbsolutePath());
            bw = new BufferedWriter(fw);
            bw.write(content);
            controller.newMessage("File successfully created: " + path + "/" + name);
        } catch (IOException e) {
            controller.newMessage("\u26A0 Failed to create file from content: " + path + "/" + name);
            e.printStackTrace();
        } finally {
            closeFile();
        }
    }

    /**
     * this method copy a file from the template folder and put it is the selected path
     *
     * @param source   source file need to be File not directory
     * @param dir      the file destination
     * @param internal true if the source file is inside the jar
     */


    void copyFile(String source, String dir, boolean internal) {
        try {
            if (internal) {
                is = ClassLoader.getSystemResourceAsStream(source);
            } else {
                is = new FileInputStream(source);
            }
            out = new FileOutputStream(dir);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            controller.newMessage("File copied from: " + source + " to: " + dir);
        } catch (IOException e) {
            controller.newMessage("\u26A0 Failed to copy file from: " + source + " to: " + dir);
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
                    File dir;
                    if (child.getName().startsWith("$package")) {
                        dir = newDir(targetLocation + "/" + controller.gamePackage.getText().replace(".", "/"));
                    } else {
                        dir = newDir(targetLocation + "/" + child.getName());
                    }

                    copyDirectory(sourceLocation + "/" + child.getName(), dir.getAbsolutePath());
                } else if (child.isFile()) {
                    if (child.getName().startsWith("$")) {
                        createFileFromTmp(new File(targetLocation), child.getName().replace("$", ""), child.getAbsolutePath(), false);
                    } else {
                        copyFile(sourceLocation + "/" + child.getName(), targetLocation + "/" + child.getName(), false);
                    }
                }
            }
        } else {
            controller.newMessage("\u26A0 The template Directory: " + sourceFile.getAbsolutePath() + " is empty!");
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
            controller.newMessage("Folder successfully created: " + file.getPath());
        } else {
            controller.newMessage("\u26A0 Failed to create folder: " + file.getPath());
        }
        return file;

    }

}
