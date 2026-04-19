package UI;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class QuanLyPhimUI extends JPanel {
    public QuanLyPhimUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(new JLabel("Màn hình PHIM"), BorderLayout.CENTER);
    }
}
