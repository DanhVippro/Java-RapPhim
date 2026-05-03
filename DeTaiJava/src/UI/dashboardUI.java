package UI;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import customUI.CustomUI;
import entity.TaiKhoan;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class dashboardUI extends JFrame {
    private final TaiKhoan currentUser;
    private String activeNav = "Trang Chủ";
    private JPanel contentArea;
    private JPanel root;

    public dashboardUI(TaiKhoan user) {
        this.currentUser = user;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setBackground(CustomUI.BG_MAIN);

        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        root.setBackground(CustomUI.BG_MAIN);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        // content mặc định
        if (user.isAdmin()) {
            activeNav = "Trang Chủ";
            contentArea = buildHomeContent();
        } else {
            activeNav = "Bán Vé";
            contentArea = new BanVeUI();
        }
        root.add(contentArea, BorderLayout.CENTER);

        setContentPane(root);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildHomeContent() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(CustomUI.BG_MAIN);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Xin chào, " + currentUser.getHoTen() + " 👋");
        title.setFont(CustomUI.bold(26));
        title.setForeground(CustomUI.TEXT_DARK);
        p.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        cards.add(CustomUI.createStatCard("TỔNG VÉ HÔM NAY", "128", "↑ 12% so với hôm qua", CustomUI.CARD_1));
        cards.add(CustomUI.createStatCard("DOANH THU", "11.5M", "Tháng này", CustomUI.CARD_2));
        cards.add(CustomUI.createStatCard("PHIM ĐANG CHIẾU", "3", "Cập nhật mới nhất", CustomUI.CARD_3));
        p.add(cards, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(CustomUI.SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(CustomUI.BORDER);
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoArea.setOpaque(false);
        JLabel ico = new JLabel("🎬");
        ico.setFont(CustomUI.plain(20));
        JLabel nm = new JLabel("MEGADE Cinema");
        nm.setFont(CustomUI.bold(15));
        nm.setForeground(CustomUI.TEAL);
        logoArea.add(ico);
        logoArea.add(nm);
        bar.add(logoArea, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 11));
        right.setOpaque(false);

        JTextField search = CustomUI.createTextField("Tìm kiếm...");
        search.setPreferredSize(new Dimension(220, 36));
        right.add(search);

        JPanel user = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        user.setOpaque(false);
        JPanel av = new JPanel() {
            {
                setPreferredSize(new Dimension(34, 34));
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CustomUI.TEAL);
                g2.fillOval(0, 0, 34, 34);
                g2.setFont(CustomUI.bold(13));
                g2.setColor(Color.WHITE);
                String initials = currentUser.isAdmin() ? "AD" : "NV";
                g2.drawString(initials, 8, 23);
                g2.dispose();
            }
        };
        JLabel uname = new JLabel(currentUser.isAdmin() ? "Quản Trị Viên" : currentUser.getHoTen());
        uname.setFont(CustomUI.plain(13));
        uname.setForeground(CustomUI.TEXT_LIGHT);
        user.add(av);
        user.add(uname);
        right.add(user);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSidebar() {
        final int W = 220;
        JPanel side = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(CustomUI.SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(W, 0));

        JLayeredPane layered = new JLayeredPane() {
            @Override
            public void doLayout() {
                int w = getWidth(), h = getHeight();
                for (Component c : getComponents()) {
                    String name = c.getName();
                    if ("scroll".equals(name))
                        c.setBounds(0, 50, w, h - 50);
                    else if ("menu".equals(name))
                        c.setBounds(0, 50, w, Math.min(h - 50, 340));
                    else if ("topbar".equals(name))
                        c.setBounds(0, 0, w, 50);
                }
            }
        };
        layered.setOpaque(false);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(CustomUI.SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(0x2D4055));
                g.drawLine(0, 49, getWidth(), 49);
            }
        };
        topBar.setOpaque(false);
        topBar.setName("topbar");

        JButton btnMenu = new JButton("☰") {
            boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hov = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        hov = false;
                        repaint();
                    }
                });
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hov) {
                    g2.setColor(new Color(255, 255, 255, 18));
                    g2.fill(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 8, 8));
                }
                g2.setFont(CustomUI.plain(18));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("☰", (getWidth() - fm.stringWidth("☰")) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btnMenu.setPreferredSize(new Dimension(36, 34));
        btnMenu.setToolTipText("Menu");

        JLabel menuLabel = new JLabel("Menu");
        menuLabel.setFont(CustomUI.bold(13));
        menuLabel.setForeground(new Color(0x90A8BF));

        topBar.add(btnMenu);
        topBar.add(menuLabel);

        // CHỈ HIỂN THỊ TREE CHO ADMIN
        if (currentUser.isAdmin()) {
            JTree tree = createTree();
            tree.setBackground(CustomUI.SIDEBAR_BG);
            tree.setForeground(new Color(0x90A8BF));
            tree.setOpaque(true);
            tree.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            JScrollPane scroll = new JScrollPane(tree);
            scroll.setName("scroll");
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.getVerticalScrollBar().setOpaque(false);
            layered.add(scroll, Integer.valueOf(0));
        }

        JPanel menuPanel = createMenuPanel();
        menuPanel.setName("menu");
        menuPanel.setVisible(false);
        layered.add(menuPanel, Integer.valueOf(1));
        layered.add(topBar, Integer.valueOf(2));

        btnMenu.addActionListener(e -> {
            boolean show = !menuPanel.isVisible();
            menuPanel.setVisible(show);
            menuLabel.setText(show ? "Đóng" : "Menu");
            menuLabel.setForeground(show ? CustomUI.TEAL : new Color(0x90A8BF));
            layered.repaint();
        });

        side.add(layered, BorderLayout.CENTER);
        return side;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CustomUI.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                GradientPaint shadow = new GradientPaint(
                        0, getHeight() - 14, new Color(0, 0, 0, 0),
                        0, getHeight(), new Color(0, 0, 0, 60));
                g2.setPaint(shadow);
                g2.fillRect(0, getHeight() - 14, getWidth(), 14);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JSeparator sep = CustomUI.createDivider();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(4));

        // MENU THEO ROLE
        String[][] navItems;
        if (currentUser.isAdmin()) {
            navItems = new String[][] {
                    { "🏠", "Trang Chủ" },
                    { "🎟️", "Bán Vé" },
                    { "🎬", "Quản Lý Phim" },
                    { "👤", "Nhân Viên" },
                    { "📊", "Thống kê" },
                    { "🚪", "Đăng xuất" },
            };
        } else {
            navItems = new String[][] {
                    { "🎟️", "Bán Vé" },
                    { "🚪", "Đăng xuất" },
            };
        }

        for (String[] item : navItems) {
            boolean isActive = item[1].equals(activeNav);
            JPanel nav = CustomUI.createNavItem(item[0], item[1], isActive);
            nav.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            nav.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    activeNav = item[1];

                    // Đóng menu sau khi chọn
                    Container parent = panel.getParent();
                    if (parent instanceof JLayeredPane) {
                        for (Component c : parent.getComponents()) {
                            if ("topbar".equals(c.getName()) && c instanceof JPanel) {
                                for (Component btn : ((JPanel) c).getComponents()) {
                                    if (btn instanceof JButton && "☰".equals(((JButton) btn).getText())) {
                                        ((JButton) btn).doClick();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    switch (item[1]) {
                        case "Trang Chủ":
                            if (currentUser.isAdmin()) {
                                switchContent(buildHomeContent());
                            }
                            break;
                        case "Bán Vé":
                            switchContent(new BanVeUI());
                            break;
                        case "Quản Lý Phim":
                            if (currentUser.isAdmin()) {
                                switchContent(new QuanLyPhimUI("List"));
                            }
                            break;
                        case "Nhân Viên":
                            if (currentUser.isAdmin()) {
                                switchContent(new QuanLyNhanVienUI());
                            }
                            break;
                        case "Thống kê":
                            if (currentUser.isAdmin()) {
                                JOptionPane.showMessageDialog(dashboardUI.this, "Chưa làm báo cáo");
                            }
                            break;
                        case "Đăng xuất":
                            int confirm = JOptionPane.showConfirmDialog(dashboardUI.this,
                                    "Bạn có chắc muốn đăng xuất?", "Xác nhận",
                                    JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                dispose();
                                new dangNhapUI().setVisible(true);
                            }
                            break;
                    }
                }
            });
            panel.add(nav);
        }

        panel.add(Box.createVerticalStrut(4));
        JSeparator sep2 = CustomUI.createDivider();
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep2);

        return panel;
    }

    private JTree createTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Menu");
        DefaultMutableTreeNode ql = new DefaultMutableTreeNode("📁 Quản lý");
        ql.add(new DefaultMutableTreeNode("👤 Nhân viên"));
        ql.add(new DefaultMutableTreeNode("🎟️ Vé"));
        DefaultMutableTreeNode qlPhim = new DefaultMutableTreeNode("🎬 Phim");
        qlPhim.add(new DefaultMutableTreeNode("Thêm phim"));
        qlPhim.add(new DefaultMutableTreeNode("Danh Sách"));
        ql.add(qlPhim);
        ql.add(new DefaultMutableTreeNode("📊 Thống kê"));
        root.add(ql);

        JTree tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setBackground(CustomUI.SIDEBAR_BG);
        tree.setForeground(new Color(0x90A8BF));
        tree.setFont(CustomUI.plain(13));
        tree.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));

        tree.setCellRenderer(new javax.swing.tree.DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree t, Object val,
                    boolean sel, boolean exp, boolean leaf, int row, boolean focus) {
                super.getTreeCellRendererComponent(t, val, sel, exp, leaf, row, focus);
                setBackground(sel ? new Color(43, 200, 163, 40) : CustomUI.SIDEBAR_BG);
                setForeground(sel ? CustomUI.TEAL : new Color(0x90A8BF));
                setFont(CustomUI.plain(13));
                setBorderSelectionColor(new Color(0, 0, 0, 0));
                setBackgroundSelectionColor(new Color(43, 200, 163, 40));
                setOpaque(true);
                return this;
            }
        });

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null)
                return;
            String value = node.toString();
            switch (value) {
                case "👤 Nhân viên":
                    switchContent(new QuanLyNhanVienUI());
                    break;
                case "🎟️ Vé":
                    switchContent(new BanVeUI());
                    break;
                case "Danh Sách":
                    switchContent(new QuanLyPhimUI("list"));
                    break;
                case "Thêm phim":
                    switchContent(new QuanLyPhimUI("add"));
                    break;
                case "📊 Thống kê":
                    JOptionPane.showMessageDialog(this, "Chưa làm thống kê");
                    break;
            }
        });

        for (int i = 0; i < tree.getRowCount(); i++)
            tree.expandRow(i);
        return tree;
    }

    private void switchContent(JPanel newContent) {
        if (contentArea != null) {
            root.remove(contentArea);
        }
        contentArea = newContent;
        root.add(contentArea, BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }
}