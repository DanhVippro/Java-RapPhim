package UI;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class QuanLyPhimUI extends JPanel {
    public QuanLyPhimUI(String mode) {
        setLayout(new BorderLayout()); // thêm dòng này cho chắc layout

        JTabbedPane tab = new JTabbedPane();
        tab.add("Danh sách", new DanhSachPhimPanel());
        tab.add("Thêm phim", new ThemPhimPanel());

        // dùng đúng tham số truyền vào
        if ("add".equals(mode)) {
            tab.setSelectedIndex(1);
        } else {
            tab.setSelectedIndex(0);
        }

        add(tab, BorderLayout.CENTER);
    }
}
