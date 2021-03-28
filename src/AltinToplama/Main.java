package AltinToplama;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setUndecorated(true);
        f.setSize(1200, 720);
        f.setBackground(new Color(25,84,150));
        f.setLocationRelativeTo(null);
        f.add(new HomePanel(f));
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
