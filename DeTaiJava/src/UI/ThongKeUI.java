package UI;

import DAO.PhimDAO;
import DAO.ThongKeDAO;
import customUI.CustomUI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ThongKeUI extends JPanel {
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();
    private final PhimDAO phimDAO = new PhimDAO();
    private final DecimalFormat dfMoney = new DecimalFormat("#,###");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // Components cần refresh
    private JPanel statCardsPanel;
    private JPanel doanhThuChartPanel;
    private JPanel theLoaiChartPanel;
    private JTable topPhimTable;
    private JTable hoatDongTable;
    private DefaultTableModel topPhimModel;
    private DefaultTableModel hoatDongModel;

    public ThongKeUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(CustomUI.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ── Tiêu đề trang với nút Refresh ──
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("📊 Thống Kê & Báo Cáo");
        title.setFont(CustomUI.bold(24));
        title.setForeground(CustomUI.TEXT_DARK);
        headerPanel.add(title, BorderLayout.WEST);

        // Nút Refresh
        JButton refreshBtn = createRefreshButton();
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ── Nội dung chính (scroll) ──
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setOpaque(false);

        // Hàng 1: 3 stat cards
        statCardsPanel = buildStatCards();
        main.add(statCardsPanel);
        main.add(Box.createVerticalStrut(16));

        // Hàng 2: Biểu đồ doanh thu 7 ngày + Bảng top phim
        JPanel row2 = new JPanel(new GridLayout(1, 2, 16, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        doanhThuChartPanel = buildDoanhThuChart();
        row2.add(doanhThuChartPanel);
        row2.add(buildTopPhimTable());
        main.add(row2);
        main.add(Box.createVerticalStrut(16));

        // Hàng 3: Biểu đồ doanh thu theo thể loại + Bảng hoạt động gần đây
        JPanel row3 = new JPanel(new GridLayout(1, 2, 16, 0));
        row3.setOpaque(false);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        theLoaiChartPanel = buildTheLoaiChart();
        row3.add(theLoaiChartPanel);
        row3.add(buildHoatDongTable());
        main.add(row3);
        main.add(Box.createVerticalStrut(16));

        JScrollPane scroll = new JScrollPane(main);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    private JButton createRefreshButton() {
        JButton btn = new JButton("Làm mới");
        btn.setFont(CustomUI.plain(12));
        btn.setForeground(CustomUI.PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE8EDF2), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> refreshAllData());
        return btn;
    }

    private void refreshAllData() {
        // Refresh stat cards
        refreshStatCards();

        // Refresh biểu đồ
        refreshDoanhThuChart();
        refreshTheLoaiChart();

        // Refresh bảng
        refreshTopPhimTable();
        refreshHoatDongTable();
    }

    private void refreshStatCards() {
        Container parent = statCardsPanel.getParent();
        int index = -1;
        if (parent != null) {
            for (int i = 0; i < parent.getComponentCount(); i++) {
                if (parent.getComponent(i) == statCardsPanel) {
                    index = i;
                    break;
                }
            }
        }

        statCardsPanel = buildStatCards();
        if (parent != null && index != -1) {
            parent.remove(index);
            parent.add(statCardsPanel, index);
            parent.revalidate();
            parent.repaint();
        }
    }

    private void refreshDoanhThuChart() {
        Container parent = doanhThuChartPanel.getParent();
        if (parent != null) {
            int index = -1;
            for (int i = 0; i < parent.getComponentCount(); i++) {
                if (parent.getComponent(i) == doanhThuChartPanel) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                parent.remove(index);
                doanhThuChartPanel = buildDoanhThuChart();
                parent.add(doanhThuChartPanel, index);
                parent.revalidate();
                parent.repaint();
            }
        }
    }

    private void refreshTheLoaiChart() {
        Container parent = theLoaiChartPanel.getParent();
        if (parent != null) {
            int index = -1;
            for (int i = 0; i < parent.getComponentCount(); i++) {
                if (parent.getComponent(i) == theLoaiChartPanel) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                parent.remove(index);
                theLoaiChartPanel = buildTheLoaiChart();
                parent.add(theLoaiChartPanel, index);
                parent.revalidate();
                parent.repaint();
            }
        }
    }

    private void refreshTopPhimTable() {
        if (topPhimModel != null) {
            topPhimModel.setRowCount(0);
            List<Object[]> rows = phimDAO.getTopPhimBanChay(5);
            for (Object[] row : rows) {
                topPhimModel.addRow(new Object[] { row[0], row[1] });
            }
        }
    }

    private void refreshHoatDongTable() {
        if (hoatDongModel != null) {
            hoatDongModel.setRowCount(0);
            List<Object[]> rows = thongKeDAO.getHoatDongGanDay(8);
            for (Object[] row : rows) {
                String thoiGian = row[3] != null ? sdf.format(row[3]) : "";
                hoatDongModel.addRow(new Object[] {
                        row[0], row[1], dfMoney.format((Double) row[2]), thoiGian
                });
            }
        }
    }

    // ──────────────────────────────────────────
    // STAT CARDS (Vé hôm nay, Doanh thu tháng, Phim đang chiếu)
    // ──────────────────────────────────────────
    private JPanel buildStatCards() {
        int veHomNay = thongKeDAO.getTongVeHomNay();
        int veHomQua = thongKeDAO.getTongVeHomQua();
        double dtThang = thongKeDAO.getDoanhThuThangNay();
        int soPhim = phimDAO.getSoPhimDangChieu();

        String pctVe;
        if (veHomQua == 0) {
            pctVe = "—";
        } else {
            double pct = (veHomNay - veHomQua) * 100.0 / veHomQua;
            String arrow = pct >= 0 ? "▲" : "▼";
            pctVe = String.format("%s %.1f%% so với hôm qua", arrow, Math.abs(pct));
        }

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        cards.add(CustomUI.createStatCard("TỔNG VÉ HÔM NAY",
                String.format("%,d", veHomNay), pctVe, CustomUI.CARD_1));
        cards.add(CustomUI.createStatCard("DOANH THU THÁNG NÀY",
                dfMoney.format(dtThang / 1_000_000) + "M",
                "≈ " + dfMoney.format(dtThang) + " VNĐ", CustomUI.CARD_2));
        cards.add(CustomUI.createStatCard("PHIM ĐANG CHIẾU",
                String.valueOf(soPhim),
                "Đang hoạt động", CustomUI.CARD_3));
        return cards;
    }

    // ──────────────────────────────────────────
    // BIỂU ĐỒ CỘT: Doanh thu 7 ngày
    // ──────────────────────────────────────────
    private JPanel buildDoanhThuChart() {
        List<Double> doanhThu = thongKeDAO.getDoanhThu7Ngay();
        List<String> ngay = thongKeDAO.getNgayTrongTuan();

        JPanel card = createCard("Doanh Thu 7 Ngày Gần Nhất");

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int padL = 55, padR = 16, padT = 20, padB = 45;
                int chartW = w - padL - padR;
                int chartH = h - padT - padB;

                if (doanhThu == null || doanhThu.isEmpty() || doanhThu.stream().allMatch(d -> d == 0)) {
                    g2.setColor(new Color(0xA0B0C0));
                    g2.setFont(CustomUI.plain(13));
                    g2.drawString("Chưa có dữ liệu", w / 2 - 50, h / 2);
                    g2.dispose();
                    return;
                }

                double max = doanhThu.stream().mapToDouble(Double::doubleValue).max().orElse(1);
                if (max == 0)
                    max = 1;

                int n = Math.min(doanhThu.size(), 7);
                int barW = chartW / n;

                // Vẽ đường lưới ngang
                g2.setColor(new Color(0xE8EDF2));
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                        0, new float[] { 4, 4 }, 0));
                for (int i = 1; i <= 4; i++) {
                    int y = padT + chartH - (int) (chartH * i / 4.0);
                    g2.drawLine(padL, y, w - padR, y);
                    g2.setColor(new Color(0xA0B0C0));
                    g2.setFont(CustomUI.plain(9));
                    long val = (long) (max * i / 4);
                    if (val >= 1_000_000) {
                        g2.drawString((val / 1_000_000) + "tr", 2, y + 4);
                    } else if (val >= 1_000) {
                        g2.drawString((val / 1_000) + "k", 2, y + 4);
                    } else {
                        g2.drawString(String.valueOf(val), 2, y + 4);
                    }
                    g2.setColor(new Color(0xE8EDF2));
                }
                g2.setStroke(new BasicStroke(1));

                // Vẽ thanh cột
                for (int i = 0; i < n; i++) {
                    double v = doanhThu.get(i);
                    int barH = (int) (chartH * v / max);
                    if (barH < 2 && v > 0)
                        barH = 2;
                    int x = padL + i * barW + barW / 5;
                    int bw = barW * 3 / 5;
                    int y = padT + chartH - barH;

                    // Gradient fill
                    GradientPaint gp = new GradientPaint(x, y, CustomUI.PRIMARY,
                            x, padT + chartH, new Color(43, 200, 163, 80));
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(x, y, bw, barH, 6, 6));

                    // Nhãn ngày
                    g2.setColor(new Color(0x6B8099));
                    g2.setFont(CustomUI.plain(9));
                    String label = (i < ngay.size()) ? ngay.get(i) : "";
                    if (label.length() > 5)
                        label = label.substring(0, 5);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(label, x + bw / 2 - fm.stringWidth(label) / 2,
                            padT + chartH + 16);

                    // Giá trị trên cột
                    if (barH > 15) {
                        g2.setColor(CustomUI.PRIMARY);
                        g2.setFont(CustomUI.bold(9));
                        String valStr;
                        if (v >= 1_000_000) {
                            valStr = (v / 1_000_000) + "tr";
                        } else if (v >= 1_000) {
                            valStr = (v / 1_000) + "k";
                        } else {
                            valStr = String.valueOf((long) v);
                        }
                        fm = g2.getFontMetrics();
                        g2.drawString(valStr, x + bw / 2 - fm.stringWidth(valStr) / 2, y - 3);
                    }
                }
                g2.dispose();
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(0, 220));
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    // ──────────────────────────────────────────
    // BIỂU ĐỒ TRÒN: Doanh thu theo thể loại
    // ──────────────────────────────────────────
    private JPanel buildTheLoaiChart() {
        List<Object[]> data = thongKeDAO.getDoanhThuTheoTheLoai();

        Color[] palette = {
                new Color(0x2BC8A3), new Color(0x3B82F6), new Color(0xF59E0B),
                new Color(0xEF4444), new Color(0x8B5CF6), new Color(0xEC4899),
                new Color(0x10B981), new Color(0x6366F1)
        };

        JPanel card = createCard("Doanh Thu Theo Thể Loại (Tháng Này)");

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                if (data == null || data.isEmpty()) {
                    g2.setColor(new Color(0xA0B0C0));
                    g2.setFont(CustomUI.plain(13));
                    g2.drawString("Chưa có dữ liệu", w / 2 - 50, h / 2);
                    g2.dispose();
                    return;
                }

                double total = data.stream().mapToDouble(r -> (Double) r[1]).sum();
                if (total == 0) {
                    g2.setColor(new Color(0xA0B0C0));
                    g2.setFont(CustomUI.plain(13));
                    g2.drawString("Chưa có dữ liệu", w / 2 - 50, h / 2);
                    g2.dispose();
                    return;
                }

                int size = Math.min(h - 60, w / 2 - 30);
                int cx = size / 2 + 25, cy = h / 2;
                double startAngle = 90;

                for (int i = 0; i < data.size(); i++) {
                    double val = (Double) data.get(i)[1];
                    double angle = (val / total) * 360.0;
                    if (angle > 0) {
                        g2.setColor(palette[i % palette.length]);
                        g2.fill(new Arc2D.Double(cx - size / 2, cy - size / 2, size, size,
                                startAngle, -angle, Arc2D.PIE));
                    }
                    startAngle -= angle;
                }

                // Vòng trắng giữa (donut)
                g2.setColor(Color.WHITE);
                int inner = size / 3;
                g2.fillOval(cx - inner, cy - inner, inner * 2, inner * 2);

                // Legend
                int legX = cx + size / 2 + 12;
                int legY = cy - (Math.min(data.size(), 6) * 22) / 2;
                g2.setFont(CustomUI.plain(11));
                for (int i = 0; i < Math.min(data.size(), 6); i++) {
                    g2.setColor(palette[i % palette.length]);
                    g2.fillRoundRect(legX, legY + i * 22, 12, 12, 4, 4);
                    g2.setColor(new Color(0x3D5166));
                    String lbl = (String) data.get(i)[0];
                    if (lbl.length() > 15)
                        lbl = lbl.substring(0, 12) + "...";
                    double pct = (Double) data.get(i)[1] / total * 100;
                    g2.drawString(String.format("%s %.1f%%", lbl, pct), legX + 18, legY + i * 22 + 11);
                }
                g2.dispose();
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(0, 220));
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    // ──────────────────────────────────────────
    // BẢNG: Top phim bán chạy
    // ──────────────────────────────────────────
    private JPanel buildTopPhimTable() {
        List<Object[]> rows = phimDAO.getTopPhimBanChay(5);
        JPanel card = createCard("🏆 Top 5 Phim Bán Chạy");

        String[] cols = { "Tên Phim", "Số Vé" };
        Object[][] data = new Object[rows.size()][2];
        for (int i = 0; i < rows.size(); i++) {
            data[i][0] = rows.get(i)[0];
            data[i][1] = rows.get(i)[1];
        }

        topPhimModel = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(topPhimModel);
        styleTable(table);

        // Set column widths
        TableColumnModel colModel = table.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(200);
        colModel.getColumn(1).setPreferredWidth(80);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // ──────────────────────────────────────────
    // BẢNG: Hoạt động gần đây
    // ──────────────────────────────────────────
    private JPanel buildHoatDongTable() {
        List<Object[]> rows = thongKeDAO.getHoatDongGanDay(8);
        JPanel card = createCard("🕐 Hoạt Động Gần Đây");

        String[] cols = { "Khách Hàng", "Phim", "Tiền", "Thời Gian" };
        Object[][] data = new Object[rows.size()][4];
        for (int i = 0; i < rows.size(); i++) {
            data[i][0] = rows.get(i)[0];
            data[i][1] = rows.get(i)[1];
            data[i][2] = dfMoney.format((Double) rows.get(i)[2]);
            data[i][3] = rows.get(i)[3] != null ? sdf.format(rows.get(i)[3]) : "";
        }

        hoatDongModel = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(hoatDongModel);
        styleTable(table);

        // Set column widths
        TableColumnModel colModel = table.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100);
        colModel.getColumn(1).setPreferredWidth(120);
        colModel.getColumn(2).setPreferredWidth(80);
        colModel.getColumn(3).setPreferredWidth(100);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // ──────────────────────────────────────────
    // HELPERS
    // ──────────────────────────────────────────
    private JPanel createCard(String headerText) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(new Color(0xE8EDF2));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel header = new JLabel(headerText);
        header.setFont(CustomUI.bold(14));
        header.setForeground(new Color(0x1E3A5F));
        card.add(header, BorderLayout.NORTH);
        return card;
    }

    private void styleTable(JTable table) {
        table.setFont(CustomUI.plain(12));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(0x3D5166));
        table.setSelectionBackground(new Color(43, 200, 163, 40));
        table.setSelectionForeground(new Color(0x1E3A5F));

        JTableHeader header = table.getTableHeader();
        header.setFont(CustomUI.bold(12));
        header.setForeground(new Color(0x6B8099));
        header.setBackground(new Color(0xF4F7FA));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE8EDF2)));
        header.setReorderingAllowed(false);

        // Center align số vé và tiền
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            String colName = table.getColumnName(i);
            if (colName.equals("Số Vé") || colName.equals("Tiền")) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Zebra rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(sel ? new Color(43, 200, 163, 40)
                        : (row % 2 == 0 ? Color.WHITE : new Color(0xF9FAFB)));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }
}