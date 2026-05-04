package UI;

import DAO.PhimDAO;
import customUI.CustomUI;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

public class dashboardUI extends JFrame {
    private final TaiKhoan currentUser;
    private String activeNav = "Trang Chủ";
    private JPanel contentArea;
    private JPanel root;
    private ChatAssistantUI assistant;
    private JPanel menuOverlay;
    private JLayeredPane layeredSidebar;
    private JButton menuToggleBtn;
    private JScrollPane treeScroll;

    public dashboardUI(TaiKhoan user) {
        this.currentUser = user;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setBackground(CustomUI.BG_MAIN);

        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        root.setBackground(CustomUI.BG_MAIN);

        root.add(buildSidebar(), BorderLayout.WEST);

        // KHỞI TẠO NỘI DUNG THEO QUYỀN
        if (user.isAdmin()) {
            activeNav = "Trang Chủ";
            contentArea = buildHomeContent();
        } else {
            // NHÂN VIÊN: MẶC ĐỊNH LÀ BÁN VÉ
            activeNav = "Bán Vé";
            contentArea = new BanVeUI();
        }
        root.add(contentArea, BorderLayout.CENTER);

        // AI Assistant
        assistant = new ChatAssistantUI();

        JLayeredPane layers = new JLayeredPane() {
            @Override
            public void doLayout() {
                for (Component c : getComponents()) {
                    if (c == root) {
                        c.setBounds(0, 0, getWidth(), getHeight());
                    } else if (c == assistant) {
                        Dimension d = c.getPreferredSize();
                        c.setBounds(getWidth() - d.width - 60, getHeight() - d.height - 30, d.width, d.height);
                    }
                }
            }
        };
        layers.add(root, JLayeredPane.DEFAULT_LAYER);
        layers.add(assistant, JLayeredPane.PALETTE_LAYER);

        setContentPane(layers);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildHomeContent() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(CustomUI.BG_MAIN);
        page.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("Xin chào, " + currentUser.getHoTen());
        title.setFont(CustomUI.bold(26));
        title.setForeground(CustomUI.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel("Phim đang chiếu hôm nay");
        sub.setFont(CustomUI.plain(13));
        sub.setForeground(new Color(0x6B8099));
        sub.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        header.add(sub, BorderLayout.SOUTH);
        page.add(header, BorderLayout.NORTH);

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 4));

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
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3, 14, 14));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

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
        posterPanel.setPreferredSize(new Dimension(130, 185));

        ImageIcon posterIcon = loadPoster(posterPath);
        if (posterIcon != null) {
            Image scaled = posterIcon.getImage().getScaledInstance(130, 185, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaled));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            posterPanel.add(imgLabel, BorderLayout.CENTER);
        } else {
            JLabel ph = new JLabel("🎬", SwingConstants.CENTER);
            ph.setFont(CustomUI.plain(36));
            posterPanel.add(ph, BorderLayout.CENTER);
        }
        card.add(posterPanel, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblTen = new JLabel(tenPhim);
        lblTen.setFont(CustomUI.bold(15));
        lblTen.setForeground(new Color(0x1E3A5F));
        lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(lblTen);
        info.add(Box.createVerticalStrut(6));

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

        JLabel lblThoiLuong = new JLabel("Thời lượng: " + thoiLuong + " phút");
        lblThoiLuong.setFont(CustomUI.plain(12));
        lblThoiLuong.setForeground(new Color(0x6B8099));
        lblThoiLuong.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(lblThoiLuong);
        info.add(Box.createVerticalStrut(4));

        if (ngayKC != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String ngayStr = sdf.format(ngayKC);
            JLabel lblNgay = new JLabel("Khởi chiếu: " + ngayStr);
            lblNgay.setFont(CustomUI.plain(12));
            lblNgay.setForeground(new Color(0x6B8099));
            lblNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
            info.add(lblNgay);
            info.add(Box.createVerticalStrut(8));
        }

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

        JButton btnDatVe = new JButton("Đặt Vé") {
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
                URL url = getClass().getResource("/resources/" + posterPath);
                if (url != null) {
                    return new ImageIcon(url);
                }
            }

            String[] candidates = {
                    posterPath,
                    "/resources/list_film/" + posterPath,
                    "/resources/" + posterPath
            };
            for (String candidate : candidates) {
                URL url = getClass().getResource(candidate);
                if (url != null)
                    return new ImageIcon(url);
            }
            System.out.println("[poster] Không tìm thấy: " + posterPath);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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

        layeredSidebar = new JLayeredPane() {
            @Override
            public void doLayout() {
                int w = getWidth(), h = getHeight();
                for (Component c : getComponents()) {
                    if ("logo".equals(c.getName()))
                        c.setBounds(0, 0, w, 90);
                    else if ("menuBtn".equals(c.getName()))
                        c.setBounds(0, 90, w, 50);
                    else if ("tree".equals(c.getName()))
                        c.setBounds(0, 140, w, h - 240);
                    else if ("account".equals(c.getName()))
                        c.setBounds(0, h - 100, w, 100);
                    else if ("menuOverlay".equals(c.getName()))
                        c.setBounds(0, 140, w, h - 240);
                }
            }
        };
        layeredSidebar.setOpaque(false);
        layeredSidebar.setBackground(CustomUI.SIDEBAR_BG);

        // LOGO
        JPanel logoArea = new JPanel(new BorderLayout());
        logoArea.setOpaque(false);
        logoArea.setName("logo");
        logoArea.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        URL logoUrl = getClass().getResource("/resources/logo.png");
        if (logoUrl != null) {
            ImageIcon logoRaw = new ImageIcon(logoUrl);
            Image logoScaled = logoRaw.getImage().getScaledInstance(100, -1, Image.SCALE_SMOOTH);
            JLabel logoImg = new JLabel(new ImageIcon(logoScaled));
            logoImg.setHorizontalAlignment(SwingConstants.CENTER);
            logoArea.add(logoImg, BorderLayout.CENTER);
        } else {
            JLabel nm = new JLabel("MEGADE Cinema");
            nm.setFont(CustomUI.bold(16));
            nm.setForeground(CustomUI.PRIMARY);
            nm.setHorizontalAlignment(SwingConstants.CENTER);
            logoArea.add(nm, BorderLayout.CENTER);
        }
        layeredSidebar.add(logoArea, Integer.valueOf(1));
        logoArea.setName("logo");

        // NÚT MENU (HIỂN THỊ CHO CẢ ADMIN VÀ NHÂN VIÊN)
        JPanel menuBtnPanel = new JPanel(new BorderLayout());
        menuBtnPanel.setOpaque(false);
        menuBtnPanel.setName("menuBtn");
        menuBtnPanel.setBorder(BorderFactory.createEmptyBorder(5, 16, 5, 16));

        menuToggleBtn = new JButton("MENU") {
            boolean hover = false;
            {
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setFocusPainted(false);
                setFont(CustomUI.bold(13));
                setForeground(Color.WHITE);
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? CustomUI.PRIMARY : new Color(43, 200, 163, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(Color.WHITE);
                int startX = (getWidth() - 60) / 2;
                int startY = getHeight() / 2 - 5;
                g2.fillRect(startX, startY, 14, 2);
                g2.fillRect(startX, startY + 5, 14, 2);
                g2.fillRect(startX, startY + 10, 14, 2);

                g2.setFont(CustomUI.bold(13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("MENU", startX + 22, (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 1);
                g2.dispose();
            }
        };
        menuToggleBtn.setPreferredSize(new Dimension(0, 38));
        menuBtnPanel.add(menuToggleBtn, BorderLayout.CENTER);
        layeredSidebar.add(menuBtnPanel, Integer.valueOf(1));
        menuBtnPanel.setName("menuBtn");

        // TREE NAVIGATION (HIỂN THỊ CHO CẢ HAI, NHƯNG NỘI DUNG KHÁC NHAU)
        treeScroll = buildNavTree();
        treeScroll.setName("tree");
        layeredSidebar.add(treeScroll, Integer.valueOf(0));
        treeScroll.setName("tree");

        // MENU OVERLAY (HIỂN THỊ CHO CẢ HAI, NHƯNG NỘI DUNG KHÁC NHAU)
        menuOverlay = createMenuOverlay();
        menuOverlay.setName("menuOverlay");
        menuOverlay.setVisible(false);
        layeredSidebar.add(menuOverlay, Integer.valueOf(1));
        menuOverlay.setName("menuOverlay");

        // SỰ KIỆN BẬT/TẮT MENU (CHO CẢ HAI)
        menuToggleBtn.addActionListener(e -> {
            boolean showMenu = !menuOverlay.isVisible();
            menuOverlay.setVisible(showMenu);
            treeScroll.setVisible(!showMenu);
            menuToggleBtn.setText(showMenu ? "✖ ĐÓNG" : "☰ MENU");
            layeredSidebar.revalidate();
            layeredSidebar.repaint();
        });

        // ACCOUNT
        JPanel accountArea = new JPanel();
        accountArea.setLayout(new BoxLayout(accountArea, BoxLayout.Y_AXIS));
        accountArea.setOpaque(false);
        accountArea.setName("account");
        accountArea.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x2D4055));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        accountArea.add(sep);
        accountArea.add(Box.createVerticalStrut(12));

        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        accountPanel.setOpaque(false);

        JPanel avatarPanel = new JPanel() {
            {
                setPreferredSize(new Dimension(38, 38));
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CustomUI.PRIMARY);
                g2.fillOval(0, 0, 38, 38);
                g2.setFont(CustomUI.bold(14));
                g2.setColor(Color.WHITE);
                String text = currentUser.isAdmin() ? "AD"
                        : (currentUser.getHoTen().length() >= 2 ? currentUser.getHoTen().substring(0, 2).toUpperCase()
                                : currentUser.getHoTen().substring(0, 1).toUpperCase());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2.drawString(text, (38 - textWidth) / 2, (38 + textHeight) / 2 - 4);
                g2.dispose();
            }
        };

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel userName = new JLabel(currentUser.getHoTen());
        userName.setFont(CustomUI.bold(13));
        userName.setForeground(Color.WHITE);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userRole = new JLabel(currentUser.isAdmin() ? "Quản trị viên" : "Nhân viên");
        userRole.setFont(CustomUI.plain(10));
        userRole.setForeground(new Color(0x90A8BF));
        userRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(userName);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(userRole);

        accountPanel.add(avatarPanel);
        accountPanel.add(infoPanel);
        accountArea.add(accountPanel);

        layeredSidebar.add(accountArea, Integer.valueOf(1));
        accountArea.setName("account");

        side.add(layeredSidebar, BorderLayout.CENTER);
        return side;
    }

    private JScrollPane buildNavTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        // THÊM NODE TRANG CHỦ CHO CẢ HAI
        DefaultMutableTreeNode nodeTrangChu = new DefaultMutableTreeNode("Trang Chủ");
        root.add(nodeTrangChu);

        // THÊM NODE BÁN VÉ CHO CẢ HAI
        DefaultMutableTreeNode nodeBanVe = new DefaultMutableTreeNode("Bán Vé");
        nodeBanVe.add(new DefaultMutableTreeNode("Vé Phim"));
        nodeBanVe.add(new DefaultMutableTreeNode("Đồ Ăn"));
        root.add(nodeBanVe);

        // CHỈ THÊM MENU QUẢN LÝ NẾU LÀ ADMIN
        if (currentUser.isAdmin()) {
            DefaultMutableTreeNode nodeNhanVien = new DefaultMutableTreeNode("Quản Lý Nhân Viên");
            root.add(nodeNhanVien);

            DefaultMutableTreeNode nodePhim = new DefaultMutableTreeNode("Quản Lý Phim");
            nodePhim.add(new DefaultMutableTreeNode("Danh Sách Phim"));
            nodePhim.add(new DefaultMutableTreeNode("Thêm Phim"));
            root.add(nodePhim);

            DefaultMutableTreeNode nodeThongKe = new DefaultMutableTreeNode("Thống Kê");
            root.add(nodeThongKe);
        }

        java.util.Map<String, String> iconFileMap = new java.util.HashMap<>();
        iconFileMap.put("Trang Chủ", "/resources/icons/house.png");
        iconFileMap.put("Bán Vé", "/resources/icons/film.png");
        iconFileMap.put("Vé Phim", "/resources/icons/clapboard.png");
        iconFileMap.put("Đồ Ăn", "/resources/icons/burger.png");

        // CHỈ THÊM ICON CHO MENU QUẢN LÝ NẾU LÀ ADMIN
        if (currentUser.isAdmin()) {
            iconFileMap.put("Quản Lý Nhân Viên", "/resources/icons/staff.png");
            iconFileMap.put("Quản Lý Phim", "/resources/icons/movies.png");
            iconFileMap.put("Danh Sách Phim", "/resources/icons/list_film.png");
            iconFileMap.put("Thêm Phim", "/resources/icons/add-movie.png");
            iconFileMap.put("Thống Kê", "/resources/icons/report.png");
        }

        DefaultTreeModel model = new DefaultTreeModel(root);
        JTree tree = new JTree(model);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(false);
        tree.putClientProperty("JTree.lineStyle", "None");
        tree.setBackground(CustomUI.SIDEBAR_BG);
        tree.setFont(CustomUI.plain(13));
        tree.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        tree.setRowHeight(46);

        for (int i = 0; i < tree.getRowCount(); i++)
            tree.expandRow(i);

        java.util.Map<String, ImageIcon> iconCache = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, String> e : iconFileMap.entrySet()) {
            URL url = getClass().getResource(e.getValue());
            if (url != null) {
                ImageIcon raw = new ImageIcon(url);
                Image scaled = raw.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                iconCache.put(e.getKey(), new ImageIcon(scaled));
            }
        }

        tree.setCellRenderer(new javax.swing.tree.TreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree t, Object val,
                    boolean sel, boolean exp, boolean leaf, int row, boolean focus) {

                String label = val.toString();
                int depth = t.getPathForRow(row) != null
                        ? t.getPathForRow(row).getPathCount() - 2
                        : 0;
                boolean isChild = depth > 0;

                JPanel item = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setColor(sel ? new Color(0x1E1B4B) : CustomUI.SIDEBAR_BG);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        if (sel) {
                            g2.setColor(new Color(0x6C63FF));
                            g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
                        }
                        g2.dispose();
                    }
                };
                item.setOpaque(false);
                int leftPad = isChild ? (24 + (depth - 1) * 14) : 10;
                item.setBorder(BorderFactory.createEmptyBorder(0, leftPad, 0, 8));

                JLabel lblIcon;
                ImageIcon ico = iconCache.get(label);
                if (ico != null) {
                    lblIcon = new JLabel(ico);
                } else {
                    lblIcon = new JLabel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(sel ? new Color(0x6C63FF) : new Color(0x5A6A7E));
                            g2.fillOval(4, 5, 8, 8);
                            g2.dispose();
                        }
                    };
                }
                lblIcon.setPreferredSize(new Dimension(26, 26));
                lblIcon.setHorizontalAlignment(JLabel.CENTER);

                JLabel lblText = new JLabel(label);
                lblText.setFont(isChild ? CustomUI.plain(12) : CustomUI.bold(13));
                lblText.setForeground(sel ? Color.WHITE
                        : (isChild ? new Color(0x7A95B0) : new Color(0xC5D5E8)));
                lblText.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

                JPanel content = new JPanel(new BorderLayout());
                content.setOpaque(false);
                content.add(lblIcon, BorderLayout.WEST);
                content.add(lblText, BorderLayout.CENTER);

                item.add(content, BorderLayout.CENTER);
                return item;
            }
        });

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null)
                return;

            String val = node.toString().trim();

            // XỬ LÝ ĐIỀU HƯỚNG
            if (val.equals("Trang Chủ")) {
                // NHÂN VIÊN CŨNG CÓ THỂ XEM TRANG CHỦ
                if (currentUser.isAdmin())
                    switchContent(buildHomeContent());
                else
                    switchContent(buildHomeContent()); // NHÂN VIÊN VẪN XEM ĐƯỢC TRANG CHỦ
            } else if (val.equals("Vé Phim") || val.equals("Bán Vé")) {
                switchContent(new BanVeUI());
            } else if (val.equals("Đồ Ăn")) {
                switchContent(new DoAnUI());
            } else if (val.equals("Quản Lý Nhân Viên")) {
                if (currentUser.isAdmin())
                    switchContent(new QuanLyNhanVienUI());
            } else if (val.equals("Danh Sách Phim")) {
                if (currentUser.isAdmin())
                    switchContent(new QuanLyPhimUI("List"));
            } else if (val.equals("Thêm Phim")) {
                if (currentUser.isAdmin())
                    switchContent(new QuanLyPhimUI("add"));
            } else if (val.equals("Thống Kê")) {
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

    private JPanel createMenuOverlay() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CustomUI.SIDEBAR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        java.util.ArrayList<String> items = new java.util.ArrayList<>();
        items.add("Trang Chủ");
        items.add("Bán Vé");

        if (currentUser.isAdmin()) {
            items.add("Quản Lý Nhân Viên");
            items.add("Quản Lý Phim");
            items.add("Thống Kê");
        }
        items.add("Đăng xuất");

        for (String name : items) {
            JPanel item = CustomUI.createNavItem("", name, false);
            item.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleNavigationFromMenu(name);
                }
            });

            panel.add(item);
            panel.add(Box.createVerticalStrut(6));
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void handleNavigationFromMenu(String itemName) {
        switch (itemName) {
            case "Trang Chủ":
                switchContent(buildHomeContent()); // CẢ ADMIN VÀ NHÂN VIÊN ĐỀU XEM ĐƯỢC
                break;
            case "Bán Vé":
                switchContent(new BanVeUI());
                break;
            case "Quản Lý Nhân Viên":
                if (currentUser.isAdmin())
                    switchContent(new QuanLyNhanVienUI());
                break;
            case "Quản Lý Phim":
                if (currentUser.isAdmin())
                    switchContent(new QuanLyPhimUI("List"));
                break;
            case "Thống Kê":
                if (currentUser.isAdmin())
                    switchContent(new ThongKeUI());
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

    private void switchContent(JPanel newContent) {
        if (contentArea != null)
            root.remove(contentArea);
        contentArea = newContent;
        root.add(contentArea, BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }
}