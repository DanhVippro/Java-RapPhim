package UI;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class QuanLyPhimUI extends JPanel {
    public QuanLyPhimUI(String string) {
        JTabbedPane tab = new JTabbedPane();
        tab.add("Danh sách", new DanhSachPhimPanel());
        tab.add("Thêm phim", new ThemPhimPanel());

        Object mode = null;
        if (mode.equals("add")) {
            tab.setSelectedIndex(1);
        }

        add(tab);
    }
}
