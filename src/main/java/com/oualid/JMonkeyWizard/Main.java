package com.oualid.JMonkeyWizard;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Created by: ouazrou-oualid on: 26/01/2018 package: com.oualid.JMonkeyWizard project: JMonkey Wizard.
 */

class Main {
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    private void start() {
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("JMonkey Wizard");
        mainFrame.setContentPane(new Form().mainPanel);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}