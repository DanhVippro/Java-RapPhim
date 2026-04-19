package UI;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class QuanLyNhanVienUI extends JPanel {
    public QuanLyNhanVienUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(new JLabel("Màn hình Nhân viên"), BorderLayout.CENTER);
    }
}
