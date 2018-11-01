package com.oualid.jmonkeywizard;

import java.io.*;

class FileUtils {


    /**
     * this method copy file from template then give it to {@link FileUtils#createFileFromContent}
     *
     * @param path    path to where you want to create the file
     * @param name    the file name
     * @param tmpPath the template file directory in String
     */

    static void createFileFromTmp(File path, String name, String tmpPath) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(tmpPath)))) {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) out.append(line).append("\n");

            for (String key : MainUi.specialWords.keySet())
                out = new StringBuilder(out.toString().replace("${" + key + "}", MainUi.specialWords.get(key)));

            createFileFromContent(path, name, out.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method create file and put String content on it.
     *
     * @param path    path to where you want to create the file
     * @param name    the file name
     * @param content the content that will be in the file.
     */

    static void createFileFromContent(File path, String name, String content) {
        File file = new File(path.getAbsolutePath() + "/" + name);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method copy a file from the template folder and put it is the selected path
     *
     * @param source source file need to be File not directory
     * @param dir    the file destination
     */

    static void copyFile(String source, String dir) {
        try {
            try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(source)) {
                try (FileOutputStream outputStream = new FileOutputStream(dir)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, length);
                }
            }
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

    static void copyDirectory(String sourceLocation, String targetLocation, String gamePackage) {
        File sourceFile = new File(sourceLocation);
        File[] children = sourceFile.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    File dir;
                    if (child.getName().startsWith("$package"))
                        dir = newDir(targetLocation + "/" + gamePackage.replace(".", "/"));
                    else dir = newDir(targetLocation + "/" + child.getName());
                    copyDirectory(sourceLocation + "/" + child.getName(), dir.getAbsolutePath(), gamePackage);
                } else if (child.isFile()) {
                    if (child.getName().startsWith("$")) {
                        createFileFromTmp(new File(targetLocation), child.getName().replace("$", ""), child.getAbsolutePath());
                    } else {
                        copyFile(sourceLocation + "/" + child.getName(), targetLocation + "/" + child.getName());
                    }
                }
            }
        }
    }

    /**
     * this method create new directory then return it.
     *
     * @param directory the directory path in String
     * @return file type File
     */

    static File newDir(String directory) {
        File file = new File(directory);
        if (file.mkdirs()) return file;
        else return file;
    }
}
