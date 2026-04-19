package UI;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class BanVeUI extends JPanel {
    public BanVeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(new JLabel("Màn hình Bán Vé"), BorderLayout.CENTER);
    }
}
