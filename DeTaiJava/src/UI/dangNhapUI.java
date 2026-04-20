package UI;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class dangNhapUI extends JPanel {
    public dangNhapUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(new JLabel("Màn hình Dang nhap"), BorderLayout.CENTER);
    }
}
