package com.oualid.JMonkeyWizard;

import javax.swing.*;
import java.io.*;


public class FileUtils {
    private ClassLoader classLoader = getClass().getClassLoader();
    private InputStream is;
    private OutputStream out;
    private BufferedWriter bw = null;
    private BufferedReader br = null;
    private FileWriter fw = null;
    private FileReader fr = null;

    void createFileFromTmp(File path, String name, String tmpPath) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(tmpPath)));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line + "\n");

            }


            for (String key : Form.specialWords.keySet()) {
                out = new StringBuilder(out.toString().replace("${" + key + "}", Form.specialWords.get(key)));
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

    void createFileFromContent(File path, String name, String content) {
        File file = new File(path.getPath() + "\\" + name);
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

    void copyFile(String source, String dir) {
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
    void copyDirectory(File sourceLocation, File targetLocation, String src) {
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
}
