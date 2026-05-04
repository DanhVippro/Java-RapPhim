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
        title.setForeground(CustomUI.PRIMARY_DARK);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // Custom Styled JTabbedPane
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(CustomUI.bold(14));
        tab.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
                    boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    g2.setColor(CustomUI.PRIMARY);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(x + 2, y + 2, w - 4, h + 10, 12, 12);
                g2.dispose();
            }

            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex,
                    String title, Rectangle textRect, boolean isSelected) {
                g.setFont(font);
                if (isSelected) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(CustomUI.TEXT_MID);
                }
                int titleWidth = metrics.stringWidth(title);
                int titleHeight = metrics.getAscent();
                g.drawString(title, textRect.x + (textRect.width - titleWidth) / 2,
                        textRect.y + titleHeight + (textRect.height - titleHeight) / 2 - 1);
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
                    boolean isSelected) {
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
                    Rectangle iconRect, Rectangle textRect, boolean isSelected) {
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                g.setColor(CustomUI.PRIMARY);
                int y = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                g.fillRect(0, y, tabPane.getWidth(), 3);
            }

            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 30;
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 12;
            }
        });
        tab.setOpaque(false);
        tab.addTab("Danh Sách Phim", new DanhSachPhimPanel());
        tab.addTab("Thêm Phim Mới", new ThemPhimPanel());

        tab.addChangeListener(e -> {
            int selected = tab.getSelectedIndex();
            String targetNodeName = selected == 0 ? "Danh Sách Phim" : "Thêm Phim";
            Window window = SwingUtilities.getWindowAncestor(QuanLyPhimUI.this);
            if (window instanceof dashboardUI) {
                ((dashboardUI) window).setSelectedSidebarNode(targetNodeName);
            }
        });

        if ("add".equals(mode)) {
            tab.setSelectedIndex(1);
        } else {
            tab.setSelectedIndex(0);
        }

        add(tab, BorderLayout.CENTER);
    }
}
