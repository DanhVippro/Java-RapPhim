package UI;

import DAO.PhimDAO;
import customUI.CustomUI;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

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
        root.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        root.setBackground(CustomUI.BG_MAIN);

        // Thêm sidebar trực tiếp, không có topbar
        root.add(buildSidebar(), BorderLayout.WEST);

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
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(CustomUI.BG_MAIN);
        page.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ── Header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Xin chào, " + currentUser.getHoTen() + " 👋");
        title.setFont(CustomUI.bold(26));
        title.setForeground(CustomUI.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel("Phim đang chiếu hôm nay");
        sub.setFont(CustomUI.plain(13));
        sub.setForeground(new Color(0x6B8099));
        sub.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        header.add(sub, BorderLayout.SOUTH);
        page.add(header, BorderLayout.NORTH);

        // ── Lưới phim (2 cột) ──
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 4));

        // Load dữ liệu phim
        PhimDAO dao = new PhimDAO();
        List<Object[]> phimList = dao.getPhimDangChieu();

        if (phimList.isEmpty()) {
            JLabel empty = new JLabel("Không có phim đang chiếu.", SwingConstants.CENTER);
            empty.setFont(CustomUI.plain(14));
            empty.setForeground(new Color(0x6B8099));
            grid.add(empty);
        } else {
            for (Object[] row : phimList) {
                grid.add(buildPhimCard(row));
            }
            // Đảm bảo grid luôn chẵn (thêm ô trống nếu lẻ)
            if (phimList.size() % 2 != 0) {
                grid.add(new JLabel());
            }
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    /**
     * Tạo card cho 1 phim
     * row = [maPhim, tenPhim, theLoai, thoiLuong, ngayKhoiChieu, moTa, trangThai,
     * poster_path]
     */
    private JPanel buildPhimCard(Object[] row) {
        String tenPhim = (String) row[1];
        String theLoai = row[2] != null ? (String) row[2] : "Chưa phân loại";
        int thoiLuong = row[3] != null ? (int) row[3] : 0;
        Object ngayKC = row[4];
        String moTa = row[5] != null ? (String) row[5] : "";
        String posterPath = row[7] != null ? (String) row[7] : "";

        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3, 14, 14));
                // Card bg
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ── Poster ──
        JPanel posterPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xE8EDF2));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        posterPanel.setOpaque(false);
        posterPanel.setPreferredSize(new Dimension(110, 155));

        ImageIcon posterIcon = loadPoster(posterPath);
        if (posterIcon != null) {
            Image scaled = posterIcon.getImage().getScaledInstance(110, 155, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaled));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            posterPanel.add(imgLabel, BorderLayout.CENTER);
        } else {
            JLabel ph = new JLabel("🎬", SwingConstants.CENTER);
            ph.setFont(CustomUI.plain(36));
            posterPanel.add(ph, BorderLayout.CENTER);
        }
        card.add(posterPanel, BorderLayout.WEST);

        // ── Thông tin phim ──
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblTen = new JLabel(tenPhim);
        lblTen.setFont(CustomUI.bold(15));
        lblTen.setForeground(new Color(0x1E3A5F));
        lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(lblTen);
        info.add(Box.createVerticalStrut(6));

        // Badge thể loại
        JLabel lblTheLoai = new JLabel(theLoai) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(43, 200, 163, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblTheLoai.setFont(CustomUI.plain(11));
        lblTheLoai.setForeground(CustomUI.PRIMARY);
        lblTheLoai.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        lblTheLoai.setOpaque(false);
        lblTheLoai.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(lblTheLoai);
        info.add(Box.createVerticalStrut(8));

        // Thời lượng
        JLabel lblThoiLuong = new JLabel("⏱ " + thoiLuong + " phút");
        lblThoiLuong.setFont(CustomUI.plain(12));
        lblThoiLuong.setForeground(new Color(0x6B8099));
        lblThoiLuong.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(lblThoiLuong);
        info.add(Box.createVerticalStrut(4));

        // Ngày khởi chiếu
        if (ngayKC != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String ngayStr = sdf.format(ngayKC);
            JLabel lblNgay = new JLabel("📅 Khởi chiếu: " + ngayStr);
            lblNgay.setFont(CustomUI.plain(12));
            lblNgay.setForeground(new Color(0x6B8099));
            lblNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
            info.add(lblNgay);
            info.add(Box.createVerticalStrut(8));
        }

        // Mô tả (giới hạn 2 dòng)
        if (!moTa.isEmpty()) {
            String shortDesc = moTa.length() > 120 ? moTa.substring(0, 120) + "…" : moTa;
            JTextArea desc = new JTextArea(shortDesc);
            desc.setFont(CustomUI.plain(12));
            desc.setForeground(new Color(0x8A9BB0));
            desc.setWrapStyleWord(true);
            desc.setLineWrap(true);
            desc.setEditable(false);
            desc.setOpaque(false);
            desc.setAlignmentX(Component.LEFT_ALIGNMENT);
            desc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
            info.add(desc);
            info.add(Box.createVerticalStrut(10));
        }

        // Nút Đặt vé
        JButton btnDatVe = new JButton("🎟 Đặt Vé") {
            boolean hov = false;
            {
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setFocusPainted(false);
                setFont(CustomUI.bold(12));
                setForeground(Color.WHITE);
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
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? new Color(0x22A88A) : CustomUI.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(110, 32);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(120, 32);
            }
        };
        btnDatVe.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDatVe.addActionListener(e -> switchContent(new BanVeUI()));
        info.add(btnDatVe);

        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private ImageIcon loadPoster(String posterPath) {
        if (posterPath == null || posterPath.isBlank())
            return null;

        try {
            if (posterPath.startsWith("http")) {
                return new ImageIcon(new URL(posterPath));
            } else {
                // load từ resources
                URL url = getClass().getResource("/resources/" + posterPath);
                if (url != null) {
                    return new ImageIcon(url);
                } else {
                    System.out.println("Không tìm thấy ảnh: " + posterPath);
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // SIDEBAR - Layout: LOGO -> MENU -> ACCOUNT (Account nằm ngang)
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        final int W = 260;
        JPanel side = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(CustomUI.SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(W, 0));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // ── PHẦN 1: LOGO (đầu trang) ──
        JPanel logoArea = new JPanel();
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setOpaque(false);
        logoArea.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));

        JPanel logoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoWrapper.setOpaque(false);
        java.net.URL logoUrl = getClass().getResource("/resources/logo.png");
        if (logoUrl != null) {
            ImageIcon logoRaw = new ImageIcon(logoUrl);
            Image logoScaled = logoRaw.getImage().getScaledInstance(120, -1, Image.SCALE_SMOOTH);
            JLabel logoImg = new JLabel(new ImageIcon(logoScaled));
            logoWrapper.add(logoImg);
        } else {
            JLabel nm = new JLabel("MEGADE Cinema");
            nm.setFont(CustomUI.bold(18));
            nm.setForeground(CustomUI.PRIMARY);
            logoWrapper.add(nm);
        }
        logoArea.add(logoWrapper);

        mainPanel.add(logoArea, BorderLayout.NORTH);

        // ── PHẦN 2: MENU CHÍNH (ở giữa) ──
        JPanel navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setOpaque(false);
        navContainer.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        // Title menu
        JLabel menuTitle = new JLabel("MENU CHÍNH");
        menuTitle.setFont(CustomUI.bold(11));
        menuTitle.setForeground(new Color(0x5A6E85));
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        navContainer.add(menuTitle);
        navContainer.add(Box.createVerticalStrut(12));

        String[][] navItems;
        if (currentUser.isAdmin()) {
            navItems = new String[][] {
                    { "🏠", "Trang Chủ" },
                    { "🎟️", "Bán Vé" },
                    { "🎬", "Quản Lý Phim" },
                    { "👤", "Quản Lý Nhân Viên" },
                    { "📊", "Thống Kê" },
                    { "⚙️", "Cài Đặt" },
            };
        } else {
            navItems = new String[][] {
                    { "🎟️", "Bán Vé" },
            };
        }

        for (String[] item : navItems) {
            boolean isActive = item[1].equals(activeNav);
            JPanel navItem = createNavItem(item[0], item[1], isActive);
            navItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            final String itemName = item[1];
            navItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    activeNav = itemName;
                    handleNavigation(itemName);
                    updateNavActiveState(navContainer, itemName);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isActive) {
                        navItem.setBackground(new Color(43, 200, 163, 20));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isActive) {
                        navItem.setBackground(new Color(0, 0, 0, 0));
                    }
                }
            });
            navContainer.add(navItem);
            navContainer.add(Box.createVerticalStrut(4));
        }

        // Nút đăng xuất nằm trong menu
        JPanel logoutItem = createNavItem("🚪", "Đăng xuất", false);
        logoutItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        logoutItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(dashboardUI.this,
                        "Bạn có chắc muốn đăng xuất?", "Xác nhận",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new dangNhapUI().setVisible(true);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                logoutItem.setBackground(new Color(43, 200, 163, 20));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutItem.setBackground(new Color(0, 0, 0, 0));
            }
        });
        navContainer.add(logoutItem);

        JScrollPane navScroll = new JScrollPane(navContainer);
        navScroll.setBorder(null);
        navScroll.setOpaque(false);
        navScroll.getViewport().setOpaque(false);
        navScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        navScroll.getVerticalScrollBar().setUnitIncrement(10);
        navScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        mainPanel.add(navScroll, BorderLayout.CENTER);

        // ── PHẦN 3: ACCOUNT (nằm ngang, ở cuối sidebar) ──
        JPanel accountArea = new JPanel();
        accountArea.setLayout(new BoxLayout(accountArea, BoxLayout.Y_AXIS));
        accountArea.setOpaque(false);
        accountArea.setBorder(BorderFactory.createEmptyBorder(16, 16, 24, 16));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x2D4055));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        accountArea.add(sep);
        accountArea.add(Box.createVerticalStrut(16));

        // Account panel - nằm ngang (avatar + info bên cạnh nhau)
        JPanel accountPanel = new JPanel(new BorderLayout(12, 0));
        accountPanel.setOpaque(false);
        accountPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Avatar nhỏ hơn (40x40)
        JPanel avatarPanel = new JPanel() {
            {
                setPreferredSize(new Dimension(40, 40));
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CustomUI.PRIMARY);
                g2.fillOval(0, 0, 40, 40);
                g2.setFont(CustomUI.bold(14));
                g2.setColor(Color.WHITE);
                String text = currentUser.isAdmin() ? "AD"
                        : (currentUser.getHoTen().length() >= 2 ? currentUser.getHoTen().substring(0, 2).toUpperCase()
                                : currentUser.getHoTen().substring(0, 1).toUpperCase());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2.drawString(text, (40 - textWidth) / 2, (40 + textHeight) / 2 - 5);
                g2.dispose();
            }
        };

        // Thông tin user
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);

        JLabel userName = new JLabel(currentUser.getHoTen());
        userName.setFont(CustomUI.bold(13));
        userName.setForeground(Color.WHITE);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userRole = new JLabel(currentUser.isAdmin() ? "Quản trị viên" : "Nhân viên");
        userRole.setFont(CustomUI.plain(10));
        userRole.setForeground(new Color(0x90A8BF));
        userRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        userInfoPanel.add(userName);
        userInfoPanel.add(Box.createVerticalStrut(4));
        userInfoPanel.add(userRole);

        accountPanel.add(avatarPanel, BorderLayout.WEST);
        accountPanel.add(userInfoPanel, BorderLayout.CENTER);

        // Thêm icon settings nhỏ bên phải (tùy chọn)
        JLabel settingsIcon = new JLabel("⚙️");
        settingsIcon.setFont(CustomUI.plain(14));
        settingsIcon.setForeground(new Color(0x90A8BF));
        settingsIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        settingsIcon.setToolTipText("Cài đặt tài khoản");
        accountPanel.add(settingsIcon, BorderLayout.EAST);

        accountArea.add(accountPanel);

        // Version text nhỏ
        JLabel versionLabel = new JLabel("MEGADE Cinema v1.0");
        versionLabel.setFont(CustomUI.plain(9));
        versionLabel.setForeground(new Color(0x5A6E85));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        accountArea.add(versionLabel);

        mainPanel.add(accountArea, BorderLayout.SOUTH);

        side.add(mainPanel, BorderLayout.CENTER);
        return side;
    }

    private JPanel createNavItem(String icon, String text, boolean isActive) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);

        if (isActive) {
            item.setBackground(new Color(43, 200, 163, 30));
            item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, CustomUI.PRIMARY),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        } else {
            item.setBackground(new Color(0, 0, 0, 0));
            item.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(CustomUI.plain(18));
        iconLabel.setForeground(isActive ? CustomUI.PRIMARY : new Color(0x90A8BF));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(isActive ? CustomUI.bold(13) : CustomUI.plain(13));
        textLabel.setForeground(isActive ? CustomUI.PRIMARY : new Color(0x90A8BF));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel);
        leftPanel.add(textLabel);

        item.add(leftPanel, BorderLayout.WEST);

        return item;
    }

    private void updateNavActiveState(JPanel navContainer, String activeItemName) {
        Component[] comps = navContainer.getComponents();
        for (Component comp : comps) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                // Tìm text label trong panel
                for (Component inner : panel.getComponents()) {
                    if (inner instanceof JPanel) {
                        JPanel leftPanel = (JPanel) inner;
                        for (Component labelComp : leftPanel.getComponents()) {
                            if (labelComp instanceof JLabel) {
                                JLabel lbl = (JLabel) labelComp;
                                // Nếu là text label (không phải icon)
                                if (!lbl.getText().matches("[🏠🎟️🎬👤📊⚙️🚪]") &&
                                        !lbl.getText().matches(".*[🏠🎟️🎬👤📊⚙️🚪].*")) {
                                    if (lbl.getText().equals(activeItemName)) {
                                        // Active state
                                        panel.setBackground(new Color(43, 200, 163, 30));
                                        panel.setBorder(BorderFactory.createCompoundBorder(
                                                BorderFactory.createMatteBorder(0, 3, 0, 0, CustomUI.PRIMARY),
                                                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
                                        lbl.setForeground(CustomUI.PRIMARY);
                                        lbl.setFont(CustomUI.bold(13));
                                    } else {
                                        // Normal state
                                        panel.setBackground(new Color(0, 0, 0, 0));
                                        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                                        lbl.setForeground(new Color(0x90A8BF));
                                        lbl.setFont(CustomUI.plain(13));
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleNavigation(String itemName) {
        switch (itemName) {
            case "Trang Chủ":
                if (currentUser.isAdmin())
                    switchContent(buildHomeContent());
                break;
            case "Bán Vé":
                switchContent(new BanVeUI());
                break;
            case "Quản Lý Phim":
                if (currentUser.isAdmin())
                    switchContent(new QuanLyPhimUI("List"));
                break;
            case "Quản Lý Nhân Viên":
                if (currentUser.isAdmin())
                    switchContent(new QuanLyNhanVienUI());
                break;
            case "Thống Kê":
                if (currentUser.isAdmin())
                    switchContent(new ThongKeUI());
                break;
            case "Cài Đặt":
                JOptionPane.showMessageDialog(dashboardUI.this, "Chức năng đang phát triển");
                break;
        }
    }

    private void switchContent(JPanel newContent) {
        if (contentArea != null)
            root.remove(contentArea);
        contentArea = newContent;
        root.add(contentArea, BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }
}