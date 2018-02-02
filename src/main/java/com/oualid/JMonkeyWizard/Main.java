package com.oualid.JMonkeyWizard;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Created by: ouazrou-oualid on: 26/01/2018 package: com.oualid.JMonkeyWizard project: JMonkey Wizard.
 */

class Main extends JFrame {
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    private void start() {
        setTitle("JMonkey Wizard");
        setContentPane(new Form().mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setVisible(true);
    }
}