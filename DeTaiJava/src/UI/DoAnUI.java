package UI;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import customUI.CustomUI;
import DAO.DoAnDAO;
import entity.DoAn;
import model.BookingState;

public class DoAnUI extends JPanel {

    private static final int COLS = 4;

    private final BookingState state; // null nếu mở độc lập
    private List<DoAn> items;
    private int[] qty;
    private JLabel[] qtyLabels;
    private JLabel lblTotal;

    /** Constructor độc lập (từ sidebar) */
    public DoAnUI() {
        this(null);
    }

    /** Constructor tích hợp (từ SnackPanel/BanVeUI) */
    public DoAnUI(BookingState state) {
        this.state = state;

        // Load từ DB (hoặc fallback)
        DoAnDAO dao = new DoAnDAO();
        dao.createTableIfNotExists();
        this.items = dao.getAllDoAn();
        this.qty = new int[items.size()];
        this.qtyLabels = new JLabel[items.size()];

        // Nếu có state, khôi phục số lượng đã chọn trước đó
        if (state != null && state.snackQty != null) {
            int len = Math.min(qty.length, state.snackQty.length);
            System.arraycopy(state.snackQty, 0, qty, 0, len);
        }

        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildGrid(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        refreshTotal();
    }

    // ── Header ──────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 58));

        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 2));
        texts.setOpaque(false);
        JLabel title = new JLabel("Đồ Ăn & Nước");
        title.setFont(CustomUI.bold(26));
        title.setForeground(CustomUI.TEXT_DARK);
        JLabel sub = new JLabel(state != null
                ? "Chọn món yêu thích để thêm vào đơn đặt vé"
                : "Chọn món yêu thích để thêm vào đơn hàng");
        sub.setFont(CustomUI.plain(12));
        sub.setForeground(CustomUI.TEXT_MID);
        texts.add(title);
        texts.add(sub);
        header.add(texts, BorderLayout.WEST);

        JButton btnReset = makeBtn("Đặt lại", CustomUI.BG_WHITE, CustomUI.BORDER2);
        btnReset.addActionListener(e -> resetAll());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        right.setOpaque(false);
        right.add(btnReset);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Grid đồ ăn ─────────────────────────────────────────────────────────
    private JScrollPane buildGrid() {
        int n = items.size();
        int rows = (int) Math.ceil((double) n / COLS);

        JPanel grid = new JPanel(new GridLayout(rows, COLS, 14, 14));
        grid.setOpaque(false);
        for (int i = 0; i < n; i++)
            grid.add(buildCard(i));
        for (int i = n; i < rows * COLS; i++) {
            JPanel ph = new JPanel();
            ph.setOpaque(false);
            grid.add(ph);
        }

        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(14);
        return sp;
    }

    private JPanel buildCard(int idx) {
        DoAn item = items.get(idx);
        Color accent = accentColor(item);
        boolean isCombo = item.isCombo();

        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 24, 24);

                g2.setColor(CustomUI.BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 24, 24);

                g2.setColor(isCombo ? accent : CustomUI.BORDER);
                g2.setStroke(new BasicStroke(isCombo ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 24, 24);

                if (isCombo) {
                    // Badge "COMBO"
                    g2.setColor(accent);
                    g2.fillRoundRect(getWidth() - 58, 0, 56, 22, 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.setFont(CustomUI.bold(10));
                    g2.drawString("COMBO", getWidth() - 52, 15);
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 12, 14));

        // Ảnh
        JPanel imgWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        imgWrapper.setOpaque(false);
        imgWrapper.add(buildImageLabel(item, accent));
        card.add(imgWrapper, BorderLayout.NORTH);

        // Tên / mô tả / giá
        JPanel texts = new JPanel(new GridLayout(3, 1, 0, 3));
        texts.setOpaque(false);
        texts.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JLabel nl = new JLabel(item.getTen(), JLabel.CENTER);
        nl.setFont(CustomUI.bold(12));
        nl.setForeground(isCombo ? accent : CustomUI.TEXT_DARK);

        JLabel dl = new JLabel(item.getMoTa(), JLabel.CENTER);
        dl.setFont(CustomUI.plain(10));
        dl.setForeground(CustomUI.TEXT_MID);

        JLabel pl = new JLabel(item.getGiaDisplay(), JLabel.CENTER);
        pl.setFont(CustomUI.bold(12));
        pl.setForeground(accent);

        texts.add(nl);
        texts.add(dl);
        texts.add(pl);
        card.add(texts, BorderLayout.CENTER);

        // Stepper — dùng ASCII + / - để tránh lỗi font
        JPanel stepper = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        stepper.setOpaque(false);

        JButton minus = stepperBtn("-", accent);
        qtyLabels[idx] = new JLabel(String.valueOf(qty[idx]), JLabel.CENTER);
        qtyLabels[idx].setFont(CustomUI.bold(14));
        qtyLabels[idx].setForeground(CustomUI.TEXT_DARK);
        qtyLabels[idx].setPreferredSize(new Dimension(28, 28));
        JButton plus = stepperBtn("+", accent);

        int fi = idx;
        minus.addActionListener(e -> {
            if (qty[fi] > 0) {
                qty[fi]--;
                syncState(fi);
                qtyLabels[fi].setText(String.valueOf(qty[fi]));
                refreshTotal();
            }
        });
        plus.addActionListener(e -> {
            qty[fi]++;
            syncState(fi);
            qtyLabels[fi].setText(String.valueOf(qty[fi]));
            refreshTotal();
        });

        stepper.add(minus);
        stepper.add(qtyLabels[idx]);
        stepper.add(plus);
        card.add(stepper, BorderLayout.SOUTH);
        return card;
    }

    // ── Ảnh đồ ăn ─────────────────────────────────────────────────────────
    private JLabel buildImageLabel(DoAn item, Color accent) {
        String path = item.getImagePath();

        // Thử load ảnh PNG/JPG
        if (path != null) {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                try {
                    ImageIcon raw = new ImageIcon(url);
                    Image scaled = raw.getImage().getScaledInstance(72, 72, Image.SCALE_SMOOTH);
                    JLabel lbl = new JLabel(new ImageIcon(scaled)) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setClip(new Ellipse2D.Float(0, 0, 72, 72));
                            g2.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, null);
                            g2.dispose();
                        }
                    };
                    lbl.setPreferredSize(new Dimension(72, 72));
                    lbl.setOpaque(false);
                    return lbl;
                } catch (Exception ignored) {
                }
            }
        }

        // Fallback đẹp: vẽ icon chữ cái + nền tròn màu
        String initial = item.isCombo() ? "C"
                : item.getTen().isEmpty() ? "?" : String.valueOf(item.getTen().charAt(0));
        JLabel lbl = new JLabel(initial, JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Nền gradient
                GradientPaint gp = new GradientPaint(0, 0, accent.darker().darker(),
                        72, 72, accent.darker());
                g2.setPaint(gp);
                g2.fillOval(0, 0, 72, 72);
                g2.setColor(accent.brighter());
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, 1, 70, 70);
                // Chữ cái đầu tên món
                g2.setColor(Color.WHITE);
                g2.setFont(CustomUI.bold(26));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(item.getTen().substring(0, Math.min(2, item.getTen().length())),
                        (72 - fm.stringWidth(item.getTen().substring(0, Math.min(2, item.getTen().length())))) / 2,
                        (72 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        lbl.setPreferredSize(new Dimension(72, 72));
        lbl.setOpaque(false);
        return lbl;
    }

    // ── Footer tổng tiền ───────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(0, 8));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x1E3048)),
                BorderFactory.createEmptyBorder(14, 0, 0, 0)));

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel lbl = new JLabel("TỔNG ĐỒ ĂN:");
        lbl.setFont(CustomUI.bold(13));
        lbl.setForeground(CustomUI.TEXT_MID);

        lblTotal = new JLabel("0 đ");
        lblTotal.setFont(CustomUI.bold(22));
        lblTotal.setForeground(CustomUI.PRIMARY);

        row.add(lbl, BorderLayout.WEST);
        row.add(lblTotal, BorderLayout.EAST);
        footer.add(row, BorderLayout.CENTER);

        JLabel note = new JLabel("* Đặt đồ ăn tại quầy trước suất chiếu 30 phút", JLabel.RIGHT);
        note.setFont(CustomUI.plain(10));
        note.setForeground(new Color(0x4A6278));
        footer.add(note, BorderLayout.SOUTH);

        // Khoảng trống an toàn để không bị bot che khuất
        footer.add(Box.createVerticalStrut(40), BorderLayout.AFTER_LAST_LINE);

        return footer;
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    /** Ghi số lượng vào BookingState nếu đang ở chế độ tích hợp */
    private void syncState(int idx) {
        if (state != null && idx < state.snackQty.length)
            state.snackQty[idx] = qty[idx];
    }

    private void resetAll() {
        for (int i = 0; i < qty.length; i++) {
            qty[i] = 0;
            syncState(i);
            if (qtyLabels[i] != null)
                qtyLabels[i].setText("0");
        }
        refreshTotal();
    }

    private void refreshTotal() {
        long total = 0;
        for (int i = 0; i < qty.length; i++)
            total += (long) qty[i] * items.get(i).getGia();
        if (lblTotal != null)
            lblTotal.setText(BanVeHelper.formatVND(total));
    }

    private Color accentColor(DoAn item) {
        return switch (item.getLoai()) {
            case "BAP" -> new Color(0xF59E0B);
            case "NUOC" -> new Color(0x06B6D4);
            case "COMBO" -> new Color(0x8B5CF6);
            case "KHAI_VI" -> new Color(0xEF4444);
            default -> new Color(0x64748B);
        };
    }

    private JButton makeBtn(String text, Color bg, Color border) {
        JButton b = new JButton(text);
        b.setFont(CustomUI.plain(12));
        b.setForeground(CustomUI.TEXT_DARK);
        b.setBackground(bg);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Stepper button - đơn giản, chắc chắn hiển thị đúng text */
    private JButton stepperBtn(String text, Color accent) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawOval(1, 1, getWidth() - 2, getHeight() - 2);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(30, 30));
        b.setFont(new Font("Arial", Font.BOLD, 18));
        b.setForeground(CustomUI.TEXT_DARK);
        b.setBackground(CustomUI.BG_MAIN);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorder(null);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
