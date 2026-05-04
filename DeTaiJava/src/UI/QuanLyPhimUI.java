package UI;

import java.awt.*;
import javax.swing.*;
import customUI.CustomUI;

public class QuanLyPhimUI extends JPanel {
    public QuanLyPhimUI(String mode) {
        setOpaque(false);
        setLayout(new BorderLayout());

        // Header Area
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Hệ Thống Quản Lý Phim");
        title.setFont(CustomUI.bold(26));
        title.setForeground(CustomUI.TEXT_LIGHT);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // Custom Styled JTabbedPane
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(CustomUI.bold(14));
        tab.setBackground(BanVeHelper.BG_MAIN);
        tab.setForeground(Color.RED);

        tab.addTab("Danh Sách Phim", new DanhSachPhimPanel());
        tab.addTab("Thêm Phim Mới", new ThemPhimPanel());

        if ("add".equals(mode)) {
            tab.setSelectedIndex(1);
        } else {
            tab.setSelectedIndex(0);
        }

        add(tab, BorderLayout.CENTER);
    }
}
