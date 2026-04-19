package UI;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.DefaultMutableTreeNode;

import customerUI.CustomerUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class dashboardUI extends JFrame {

    private String activeNav = "Trang Chủ";
    private JPanel contentArea;
    private JPanel root;

    public dashboardUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setBackground(CustomerUI.BG_MAIN);

        root = new JPanel(new BorderLayout());
        root.setBackground(CustomerUI.BG_MAIN);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        // content mặc định
        contentArea = buildContent();
        root.add(contentArea, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ─── Top Bar ──────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(CustomerUI.SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(CustomerUI.BORDER);
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Logo bên trái (dùng sidebar style)
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoArea.setOpaque(false);
        JLabel ico = new JLabel("🎬");// LOGO
        ico.setFont(CustomerUI.plain(20));
        JLabel nm = new JLabel("MEGADE Cinema");
        nm.setFont(CustomerUI.bold(15));
        nm.setForeground(CustomerUI.TEAL);
        logoArea.add(ico);
        logoArea.add(nm);
        bar.add(logoArea, BorderLayout.WEST);

        // Bên phải: search + notification + user
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 11));
        right.setOpaque(false);

        JTextField search = CustomerUI.createTextField("Tìm kiếm...");
        search.setPreferredSize(new Dimension(220, 36));
        right.add(search);

        // Bell icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/res/icon/bell-white.png"));
        Image img = icon.getImage().getScaledInstance(35, 40, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        JLabel bell = new JLabel(icon);
        bell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        right.add(bell);

        // User block
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
                g2.setColor(CustomerUI.TEAL);
                g2.fillOval(0, 0, 34, 34);
                g2.setFont(CustomerUI.bold(13));
                g2.setColor(Color.WHITE);
                g2.drawString("QT", 8, 23);
                g2.dispose();
            }
        };
        JLabel uname = new JLabel("Quản Trị Viên");
        uname.setFont(CustomerUI.plain(13));
        uname.setForeground(CustomerUI.TEXT_LIGHT);
        user.add(av);
        user.add(uname);
        right.add(user);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ─── Sidebar─────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(220, 0));
        side.setBackground(CustomerUI.BG_WHITE);

        // ===== TẠO NODE =====
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Menu");

        DefaultMutableTreeNode trangChu = new DefaultMutableTreeNode("Trang chủ");

        DefaultMutableTreeNode quanLy = new DefaultMutableTreeNode("Quản lí");
        DefaultMutableTreeNode nhanVien = new DefaultMutableTreeNode("Nhân viên");
        DefaultMutableTreeNode ve = new DefaultMutableTreeNode("Vé");

        DefaultMutableTreeNode phim = new DefaultMutableTreeNode("Phim");
        DefaultMutableTreeNode dsPhim = new DefaultMutableTreeNode("Danh sách");
        DefaultMutableTreeNode themPhim = new DefaultMutableTreeNode("Thêm phim");

        DefaultMutableTreeNode thongKe = new DefaultMutableTreeNode("Thống kê");

        // ===== GHÉP CÂY =====
        phim.add(dsPhim);
        phim.add(themPhim);

        quanLy.add(nhanVien);
        quanLy.add(ve);
        quanLy.add(phim);

        root.add(trangChu);
        root.add(quanLy);
        root.add(thongKe);

        // ===== TẠO TREE =====
        JTree tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setBackground(CustomerUI.BG_WHITE);
        tree.setForeground(Color.RED);
        tree.setRowHeight(30);

        // Expand mặc định
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        // ===== XỬ LÝ CLICK =====
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

            if (selectedNode == null)
                return;

            String value = selectedNode.toString();

            switch (value) {
                case "Trang chủ":
                    switchContent(buildContent());
                    break;

                case "Nhân viên":
                    switchContent(new QuanLyNhanVienUI());
                    break;

                case "Vé":
                    switchContent(new BanVeUI());
                    break;

                case "Danh sách":
                    switchContent(new QuanLyPhimUI("list"));
                    break;

                case "Thêm phim":
                    switchContent(new QuanLyPhimUI("add"));
                    break;

                case "Thống kê":
                    JOptionPane.showMessageDialog(this, "Chưa làm thống kê");
                    break;
            }
        });

        // ===== SCROLL =====
        JScrollPane scroll = new JScrollPane(tree);
        scroll.setBorder(null);

        side.add(scroll, BorderLayout.CENTER);

        return side;
    }

    // ─── Nội dung chính ──────────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(CustomerUI.BG_MAIN);

        // Header trang
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        hdr.setBorder(BorderFactory.createEmptyBorder(22, 24, 12, 24));
        JLabel ttl = new JLabel("Bảng Điều Khiển");
        ttl.setFont(CustomerUI.bold(24));
        ttl.setForeground(CustomerUI.TEXT_DARK);
        JLabel sub = new JLabel("Chào mừng trở lại, Quản Trị Viên  •  " + java.time.LocalDate.now());
        sub.setFont(CustomerUI.plain(13));
        sub.setForeground(CustomerUI.TEXT_MID);
        hdr.add(ttl, BorderLayout.NORTH);
        hdr.add(sub, BorderLayout.SOUTH);
        wrap.add(hdr, BorderLayout.NORTH);

        // Body cuộn
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(CustomerUI.BG_MAIN);
        body.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));

        // ── Stat cards (3 card teal như ảnh) ──────────────────────────────────
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 14, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));
        statsRow.add(CustomerUI.createStatCard("Tổng Vé Đã Bán", "1,250", "K&H hôm nay +42", CustomerUI.CARD_1));
        statsRow.add(CustomerUI.createStatCard("Doanh Thu Hôm Nay", "30,250,000", "VNĐ  ▲ 12%", CustomerUI.CARD_2));
        statsRow.add(CustomerUI.createStatCard("Phim Đang Chiếu", "8", "Khách hàng — 3 sắp ra", CustomerUI.CARD_3));
        body.add(statsRow);
        body.add(Box.createVerticalStrut(18));

        // ── Giữa: biểu đồ + top phim ──────────────────────────────────────────
        JPanel midRow = new JPanel(new GridLayout(1, 2, 14, 0));
        midRow.setOpaque(false);
        midRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 255));

        // Biểu đồ doanh thu
        JPanel chartCard = CustomerUI.createCard();
        chartCard.setLayout(new BorderLayout(0, 10));
        JPanel chHdr = new JPanel(new BorderLayout());
        chHdr.setOpaque(false);
        chHdr.add(CustomerUI.createSectionTitle("Doanh Thu Theo Ngày"), BorderLayout.WEST);
        JLabel chOpt = new JLabel("Chọn Tiếp ▼");
        chOpt.setFont(CustomerUI.plain(12));
        chOpt.setForeground(CustomerUI.TEXT_LIGHT);
        chHdr.add(chOpt, BorderLayout.EAST);
        chartCard.add(chHdr, BorderLayout.NORTH);
        int[] vals = { 12, 22, 17, 38, 28, 42, 31 };
        String[] days = { "T2", "T3", "T4", "T5", "T6", "T7", "CN" };
        chartCard.add(CustomerUI.createMiniBarChart(vals, days, CustomerUI.TEAL), BorderLayout.CENTER);
        // legend
        JPanel leg = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leg.setOpaque(false);
        addLegend(leg, "Tổng tuần", "188M VNĐ", CustomerUI.TEAL);
        addLegend(leg, "Cao nhất", "42M", CustomerUI.INFO);
        chartCard.add(leg, BorderLayout.SOUTH);
        midRow.add(chartCard);

        // Top phim
        JPanel movCard = CustomerUI.createCard();
        movCard.setLayout(new BorderLayout(0, 10));
        movCard.add(CustomerUI.createSectionTitle("Doanh Thu Theo Ngày"), BorderLayout.NORTH);
        JPanel movList = new JPanel();
        movList.setLayout(new BoxLayout(movList, BoxLayout.Y_AXIS));
        movList.setOpaque(false);
        String[][] movies = {
                { "Avatar: Đường Nước", "Bán 21.1 tỷ, 3sbn", "#2BC8A3" },
                { "Fast X: Siêu Tốc Độ", "Bán 10.3 tỷ, 2sbn", "#3B82F6" },
                { "Đen Góa Phụ", "Bán 4.1 tỷ, 1sbn", "#F59E0B" },
                { "Biệt Đội Siêu Anh Hùng", "Bán 2.5 tỷ", "#EF4444" },
        };
        for (String[] m : movies) {
            movList.add(buildMovieRow(m[0], m[1], Color.decode(m[2])));
            movList.add(Box.createVerticalStrut(8));
        }
        movCard.add(movList, BorderLayout.CENTER);
        midRow.add(movCard);
        body.add(midRow);
        body.add(Box.createVerticalStrut(18));

        // ── Bảng lịch chiếu ────────────────────────────────────────────────────
        JPanel tblCard = CustomerUI.createCard();
        tblCard.setLayout(new BorderLayout(0, 12));
        JPanel tblHdr = new JPanel(new BorderLayout());
        tblHdr.setOpaque(false);
        tblHdr.add(CustomerUI.createSectionTitle("Lịch Chiếu Hôm Nay"), BorderLayout.WEST);
        JPanel tblBtns = CustomerUI.row(
                CustomerUI.createSecondaryButton("Lọc"),
                CustomerUI.createPrimaryButton("+ Thêm Suất"));
        tblHdr.add(tblBtns, BorderLayout.EAST);
        tblCard.add(tblHdr, BorderLayout.NORTH);
        tblCard.add(buildScheduleTable(), BorderLayout.CENTER);
        body.add(tblCard);
        body.add(Box.createVerticalStrut(18));

        // ── Hàng dưới cùng ─────────────────────────────────────────────────────
        JPanel botRow = new JPanel(new GridLayout(1, 2, 14, 0));
        botRow.setOpaque(false);
        botRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel actCard = CustomerUI.createCard();
        actCard.setLayout(new BorderLayout(0, 10));
        actCard.add(CustomerUI.createSectionTitle("Hoạt Động Gần Đây"), BorderLayout.NORTH);
        actCard.add(buildActivityLog(), BorderLayout.CENTER);
        botRow.add(actCard);

        JPanel qCard = CustomerUI.createCard();
        qCard.setLayout(new BorderLayout(0, 10));
        qCard.add(CustomerUI.createSectionTitle("Thao Tác Nhanh"), BorderLayout.NORTH);
        qCard.add(buildQuickActions(), BorderLayout.CENTER);
        botRow.add(qCard);
        body.add(botRow);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBackground(CustomerUI.BG_MAIN);
        scroll.getViewport().setBackground(CustomerUI.BG_MAIN);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    // ─── Movie row ────────────────────────────────────────────────────────────
    private JPanel buildMovieRow(String name, String meta, Color accent) {
        JPanel p = new JPanel(new BorderLayout(10, 0)) {
            boolean hov = false;
            {
                setOpaque(false);
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
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (hov) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0, 0, 0, 5));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        p.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Thumb màu
        JPanel thumb = new JPanel() {
            {
                setPreferredSize(new Dimension(32, 44));
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, accent, 0, getHeight(), accent.darker());
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 2, getWidth(), getHeight() - 4, 6, 6));
                g2.dispose();
            }
        };

        JPanel info = new JPanel(new BorderLayout());
        info.setOpaque(false);
        JLabel nm = new JLabel(name);
        nm.setFont(CustomerUI.bold(13));
        nm.setForeground(CustomerUI.TEXT_DARK);
        JLabel mt = new JLabel(meta);
        mt.setFont(CustomerUI.plain(11));
        mt.setForeground(CustomerUI.TEXT_LIGHT);
        info.add(nm, BorderLayout.NORTH);
        info.add(mt, BorderLayout.SOUTH);

        p.add(thumb, BorderLayout.WEST);
        p.add(info, BorderLayout.CENTER);
        return p;
    }

    // ─── Bảng lịch chiếu ─────────────────────────────────────────────────────
    private JScrollPane buildScheduleTable() {
        String[] cols = { "Mã Suất", "Phim", "Phòng", "Giờ Chiếu", "Ghế Trống", "Giá Vé", "Trạng Thái" };
        Object[][] data = {
                { "SC001", "Avatar: Đường Nước", "P1", "09:00", "80 / 120", "120,000 đ", "Đang chiếu" },
                { "SC002", "Fast X: Siêu Tốc Độ", "P2", "11:30", "45 / 100", "110,000 đ", "Sắp chiếu" },
                { "SC003", "Đen Góa Phụ", "P3", "14:00", "12 / 80", "100,000 đ", "Đang chiếu" },
                { "SC004", "Biệt Đội Siêu Anh Hùng", "P1", "16:30", "60 / 120", "115,000 đ", "Sắp chiếu" },
                { "SC005", "Avatar: Đường Nước", "P2", "19:00", "0 / 100", "130,000 đ", "Hết vé" },
                { "SC006", "Fast X: Siêu Tốc Độ", "P3", "21:30", "95 / 80", "110,000 đ", "Đang chiếu" },
        };
        DefaultTableModel mdl = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tbl = new JTable(mdl) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                // xen kẽ hàng
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? CustomerUI.BG_WHITE : CustomerUI.BG_ROW_ALT);
                else
                    c.setBackground(CustomerUI.TEAL_LIGHT);
                c.setForeground(CustomerUI.TEXT_DARK);
                if (c instanceof JLabel)
                    ((JLabel) c).setBorder(
                            BorderFactory.createEmptyBorder(8, 12, 8, 12));
                // màu trạng thái
                if (col == 6) {
                    String v = (String) getValueAt(row, col);
                    if (v.contains("Hết"))
                        c.setForeground(CustomerUI.DANGER);
                    else if (v.contains("Sắp"))
                        c.setForeground(CustomerUI.WARNING);
                    else
                        c.setForeground(CustomerUI.SUCCESS);
                }
                return c;
            }
        };
        tbl.setBackground(CustomerUI.BG_WHITE);
        tbl.setForeground(CustomerUI.TEXT_DARK);
        tbl.setFont(CustomerUI.plain(13));
        tbl.setRowHeight(40);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 1));
        tbl.getTableHeader().setFont(CustomerUI.bold(12));
        tbl.getTableHeader().setBackground(new Color(0xF1F5F9));
        tbl.getTableHeader().setForeground(CustomerUI.TEXT_MID);
        tbl.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, CustomerUI.BORDER));
        int[] ws = { 70, 200, 60, 90, 100, 110, 110 };
        for (int i = 0; i < ws.length; i++)
            tbl.getColumnModel().getColumn(i).setPreferredWidth(ws[i]);
        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setBackground(CustomerUI.BG_WHITE);
        sp.getViewport().setBackground(CustomerUI.BG_WHITE);
        sp.setPreferredSize(new Dimension(0, 215));
        return sp;
    }

    // ─── Activity log ─────────────────────────────────────────────────────────
    private JPanel buildActivityLog() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        String[][] acts = {
                { "🎟️", "Vé SC001-B5 đã được bán", "2 phút trước", "#2BC8A3" },
                { "👤", "Nhân viên Minh Liên đăng nhập", "15 phút trước", "#3B82F6" },
                { "🎬", "Phim 'Oppenheimer' được thêm mới", "1 giờ trước", "#F59E0B" },
                { "💰", "Báo cáo doanh thu ngày được tạo", "2 giờ trước", "#22C55E" },
                { "🔔", "Hết vé suất Avatar 19:00", "3 giờ trước", "#EF4444" },
        };
        for (String[] a : acts) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            JLabel ic = new JLabel(a[0]);
            ic.setFont(CustomerUI.plain(14));
            ic.setPreferredSize(new Dimension(24, 20));
            JPanel inf = new JPanel(new BorderLayout());
            inf.setOpaque(false);
            JLabel msg = new JLabel(a[1]);
            msg.setFont(CustomerUI.plain(12));
            msg.setForeground(CustomerUI.TEXT_DARK);
            JLabel tm = new JLabel(a[2]);
            tm.setFont(CustomerUI.plain(11));
            tm.setForeground(CustomerUI.TEXT_LIGHT);
            inf.add(msg, BorderLayout.NORTH);
            inf.add(tm, BorderLayout.SOUTH);
            row.add(ic, BorderLayout.WEST);
            row.add(inf, BorderLayout.CENTER);
            p.add(row);
        }
        return p;
    }

    // ─── Quick actions ────────────────────────────────────────────────────────
    private JPanel buildQuickActions() {
        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        p.setOpaque(false);
        String[][] acts = {
                { "🎟️", "Bán Vé Nhanh" }, { "➕", "Thêm Phim Mới" },
                { "📋", "Xem Báo Cáo" }, { "👤", "Quản Lý NV" },
        };
        for (String[] a : acts) {
            JButton btn = new JButton() {
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
                    g2.setColor(hov ? CustomerUI.TEAL_LIGHT : CustomerUI.BG_ROW_ALT);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                    g2.setColor(hov ? CustomerUI.TEAL : CustomerUI.BORDER2);
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
                    g2.setFont(CustomerUI.plain(22));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(a[0], (getWidth() - fm.stringWidth(a[0])) / 2, getHeight() / 2 - 2);
                    g2.setFont(CustomerUI.plain(12));
                    g2.setColor(CustomerUI.TEXT_MID);
                    fm = g2.getFontMetrics();
                    g2.drawString(a[1], (getWidth() - fm.stringWidth(a[1])) / 2, getHeight() / 2 + 18);
                    g2.dispose();
                }
            };
            btn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Mở: " + a[1]));
            p.add(btn);
        }
        return p;
    }

    // ─── Legend helper ────────────────────────────────────────────────────────
    private void addLegend(JPanel p, String label, String value, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setOpaque(false);
        JPanel dot = new JPanel() {
            {
                setPreferredSize(new Dimension(9, 9));
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 1, 9, 9);
                g2.dispose();
            }
        };
        JLabel lb = new JLabel(label + ": ");
        lb.setFont(CustomerUI.plain(11));
        lb.setForeground(CustomerUI.TEXT_LIGHT);
        JLabel vl = new JLabel(value);
        vl.setFont(CustomerUI.bold(12));
        vl.setForeground(color);
        item.add(dot);
        item.add(lb);
        item.add(vl);
        p.add(item);
    }

    private void switchContent(JPanel newContent) {
        root.remove(contentArea);
        contentArea = newContent;
        root.add(contentArea, BorderLayout.CENTER);

        root.revalidate();
        root.repaint();
    }
}