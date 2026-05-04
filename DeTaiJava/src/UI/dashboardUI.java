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
        root.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        root.setBackground(CustomUI.BG_MAIN);

        root.add(buildTopBar(), BorderLayout.NORTH);
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
    // TOP BAR
    // ══════════════════════════════════════════════════════════════════
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
        // Load logo.png từ resources/
        java.net.URL logoUrl = getClass().getResource("/resources/logo.png");
        if (logoUrl != null) {
            ImageIcon logoRaw = new ImageIcon(logoUrl);
            Image logoScaled = logoRaw.getImage().getScaledInstance(-1, 40, Image.SCALE_SMOOTH);
            JLabel logoImg = new JLabel(new ImageIcon(logoScaled));
            logoArea.add(logoImg);
        } else {
            // Fallback nếu chưa có file
            JLabel nm = new JLabel("MEGADE Cinema");
            nm.setFont(CustomUI.bold(15));
            nm.setForeground(CustomUI.PRIMARY);
            logoArea.add(nm);
        }
        bar.add(logoArea, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 11));
        right.setOpaque(false);

        // [Search field removed]

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
                g2.setColor(CustomUI.PRIMARY);
                g2.fillOval(0, 0, 34, 34);
                g2.setFont(CustomUI.bold(13));
                g2.setColor(Color.WHITE);
                g2.drawString(currentUser.isAdmin() ? "AD" : "NV", 8, 23);
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

    // ══════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ══════════════════════════════════════════════════════════════════
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

        // ── JLayeredPane: layer 0 = topbar + tree, layer 1 = menu overlay ──
        JLayeredPane layered = new JLayeredPane() {
            @Override
            public void doLayout() {
                int w = getWidth(), h = getHeight();
                for (Component c : getComponents()) {
                    String name = c.getName();
                    if ("topbar".equals(name))
                        c.setBounds(0, 0, w, 50);
                    else if ("tree".equals(name))
                        c.setBounds(0, 50, w, h - 50);
                    else if ("menu".equals(name))
                        // che toàn bộ phần dưới topbar
                        c.setBounds(0, 50, w, h - 50);
                }
            }
        };
        layered.setOpaque(false);

        // ── Topbar của sidebar (nút ☰ + label) ──
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

        JLabel menuLabel = new JLabel("Menu");
        menuLabel.setFont(CustomUI.plain(12));
        menuLabel.setForeground(new Color(0x90A8BF));

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
                    g2.setColor(new Color(43, 200, 163, 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setFont(CustomUI.plain(18));
                g2.setColor(CustomUI.PRIMARY);
                g2.drawString("☰", 6, 22);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(34, 30);
            }
        };
        topBar.add(btnMenu);
        topBar.add(menuLabel);

        // ── JTree thường trực (layer thấp) ──
        JScrollPane treeScroll = buildNavTree();
        treeScroll.setName("tree");

        // ── Menu overlay (layer cao, ẩn mặc định) ──
        JPanel menuPanel = createMenuPanel();
        menuPanel.setName("menu");
        menuPanel.setVisible(false);

        // Layer 0: tree (bên dưới)
        layered.add(treeScroll, Integer.valueOf(0));
        // Layer 1: menu overlay (bên trên)
        layered.add(menuPanel, Integer.valueOf(1));
        // Layer 2: topbar (luôn trên cùng)
        layered.add(topBar, Integer.valueOf(2));

        btnMenu.addActionListener(e -> {
            boolean show = !menuPanel.isVisible();
            menuPanel.setVisible(show);
            menuLabel.setText(show ? "Đóng" : "Menu");
            menuLabel.setForeground(show ? CustomUI.PRIMARY : new Color(0x90A8BF));
            layered.repaint();
        });

        side.add(layered, BorderLayout.CENTER);
        return side;
    }

    /**
     * Tạo JTree điều hướng với cấu trúc:
     * Trang Chủ
     * Bán Vé
     * ├─ Vé Phim
     * └─ Đồ Ăn
     * Quản Lý Nhân Viên
     * Quản Lý Phim
     * ├─ Danh Sách Phim
     * └─ Thêm Phim
     * Thống Kê
     */
    private JScrollPane buildNavTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        DefaultMutableTreeNode nodeTrangChu = new DefaultMutableTreeNode("🏠  Trang Chủ");
        root.add(nodeTrangChu);

        DefaultMutableTreeNode nodeBanVe = new DefaultMutableTreeNode("🎟️  Bán Vé");
        nodeBanVe.add(new DefaultMutableTreeNode("🎞️  Vé Phim"));
        nodeBanVe.add(new DefaultMutableTreeNode("🍿  Đồ Ăn"));
        root.add(nodeBanVe);

        DefaultMutableTreeNode nodeNhanVien = new DefaultMutableTreeNode("👤  Quản Lý Nhân Viên");
        root.add(nodeNhanVien);

        DefaultMutableTreeNode nodePhim = new DefaultMutableTreeNode("🎬  Quản Lý Phim");
        nodePhim.add(new DefaultMutableTreeNode("📋  Danh Sách Phim"));
        nodePhim.add(new DefaultMutableTreeNode("➕  Thêm Phim"));
        root.add(nodePhim);

        DefaultMutableTreeNode nodeThongKe = new DefaultMutableTreeNode("📊  Thống Kê");
        root.add(nodeThongKe);

        javax.swing.tree.DefaultTreeModel model = new javax.swing.tree.DefaultTreeModel(root);
        JTree tree = new JTree(model);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setBackground(CustomUI.SIDEBAR_BG);
        tree.setForeground(new Color(0x90A8BF));
        tree.setFont(CustomUI.plain(13));
        tree.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));
        tree.setRowHeight(36);

        // Expand tất cả
        for (int i = 0; i < tree.getRowCount(); i++)
            tree.expandRow(i);

        // Custom renderer: active teal, hover, indent style
        tree.setCellRenderer(new javax.swing.tree.DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree t, Object val,
                    boolean sel, boolean exp, boolean leaf, int row, boolean focus) {
                super.getTreeCellRendererComponent(t, val, sel, exp, leaf, row, focus);
                setBackgroundSelectionColor(new Color(43, 200, 163, 50));
                setBorderSelectionColor(new Color(0, 0, 0, 0));
                setBackground(sel ? new Color(43, 200, 163, 50) : CustomUI.SIDEBAR_BG);
                setForeground(sel ? CustomUI.PRIMARY : new Color(0x90A8BF));
                setFont(sel ? CustomUI.bold(13) : CustomUI.plain(13));
                setOpaque(true);
                setLeafIcon(null);
                setOpenIcon(null);
                setClosedIcon(null);
                setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
                return this;
            }
        });

        // Xử lý click
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null || !node.isLeaf() && node.getChildCount() > 0
                    && node.toString().contains("Bán Vé"))
                return;
            if (node == null)
                return;

            String val = node.toString().replaceAll("^[^\\p{L}a-zA-Z]+\\s*", "").trim();
            // Dùng endsWith để tránh lỗi emoji prefix
            if (val.endsWith("Trang Chủ")) {
                if (currentUser.isAdmin())
                    switchContent(buildHomeContent());
            } else if (val.endsWith("Vé Phim") || val.endsWith("Bán Vé")) {
                switchContent(new BanVeUI());
            } else if (val.endsWith("Đồ Ăn")) {
                // switchContent(new DoAnUI()); // mở rộng sau
                JOptionPane.showMessageDialog(dashboardUI.this, "Chức năng Đồ Ăn đang phát triển");
            } else if (val.endsWith("Quản Lý Nhân Viên")) {
                if (currentUser.isAdmin())
                    switchContent(new QuanLyNhanVienUI());
            } else if (val.endsWith("Danh Sách Phim")) {
                if (currentUser.isAdmin())
                    switchContent(new QuanLyPhimUI("List"));
            } else if (val.endsWith("Thêm Phim")) {
                if (currentUser.isAdmin())
                    switchContent(new QuanLyPhimUI("add"));
            } else if (val.endsWith("Thống Kê")) {
                if (currentUser.isAdmin())
                    switchContent(new ThongKeUI());
            }
        });

        JScrollPane sp = new JScrollPane(tree);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setBackground(CustomUI.SIDEBAR_BG);
        sp.getViewport().setOpaque(true);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(10);
        return sp;
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
                                    if (btn instanceof JButton) {
                                        ((JButton) btn).doClick();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    switch (item[1]) {
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
                        case "Nhân Viên":
                            if (currentUser.isAdmin())
                                switchContent(new QuanLyNhanVienUI());
                            break;
                        case "Thống kê":
                            if (currentUser.isAdmin())
                                switchContent(new ThongKeUI()); // ← Kết nối ThongKeUI
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

    private void switchContent(JPanel newContent) {
        if (contentArea != null)
            root.remove(contentArea);
        contentArea = newContent;
        root.add(contentArea, BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }
}