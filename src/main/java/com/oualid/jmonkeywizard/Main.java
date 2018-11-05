package com.oualid.jmonkeywizard;

public class Main {

    public static void main(String[] args) {
        /*if (args.length == 0) {
            try {
                System.out.println("no argumonts");
                Runtime.getRuntime().exec(new String[]{"java",
                        "--module-path", "/javafx",
                        "--add-modules", "javafx.controls",
                        "--add-modules", "javafx.graphics",
                        "--add-exports", "javafx.graphics/com.sun.javafx.util=ALL-UNNAMED",
                        "--add-exports", "javafx.base/com.sun.javafx.reflect=ALL-UNNAMED",
                        "--add-exports", "javafx.base/com.sun.javafx.beans=ALL-UNNAMED",
                        "--add-exports", "javafx.graphics/com.sun.glass.utils=ALL-UNNAMED",
                        "--add-exports", "javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED",
                        "-jar", "jmonkey-wizard.jar", "arg"});
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            System.exit(0);
        }*/
        App.main(args);
    }
}