package customerUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class CustomerUI {

    // ─── Màu nền ──────────────────────────────────────────────────────────────
    public static final Color BG_MAIN = new Color(0xEEF2F7); // nền tổng xám xanh nhạt
    public static final Color BG_WHITE = new Color(0xFFFFFF); // card trắng
    public static final Color BG_ROW_ALT = new Color(0xF8FAFC); // hàng bảng xen kẽ

    // ─── Sidebar tối (như ảnh) ────────────────────────────────────────────────
    public static final Color SIDEBAR_BG = new Color(0x1B2B3A); // nền sidebar
    public static final Color SIDEBAR_ACT = new Color(0x2BC8A3); // teal active

    // ─── Teal chủ đạo ────────────────────────────────────────────────────────
    public static final Color TEAL = new Color(0x2BC8A3);
    public static final Color TEAL_DARK = new Color(0x1E9E80);
    public static final Color TEAL_LIGHT = new Color(0xD0F5ED);

    // ─── Stat card 3 sắc độ teal như ảnh ─────────────────────────────────────
    public static final Color CARD_1 = new Color(0x2BBAA8); // xanh đậm (Tổng vé)
    public static final Color CARD_2 = new Color(0x32C5AF); // xanh vừa (Doanh thu)
    public static final Color CARD_3 = new Color(0x3DD0BA); // xanh nhạt (Phim chiếu)

    // ─── Chữ ──────────────────────────────────────────────────────────────────
    public static final Color TEXT_DARK = new Color(0x1C2D3E); // chữ tối chính
    public static final Color TEXT_MID = new Color(0x4A6278); // chữ phụ
    public static final Color TEXT_LIGHT = new Color(0x8FA5BB); // chữ mờ
    public static final Color TEXT_WHITE = Color.WHITE;

    // ─── Border ───────────────────────────────────────────────────────────────
    public static final Color BORDER = new Color(0xDDE5EE);
    public static final Color BORDER2 = new Color(0xC8D5E2);

    // ─── Trạng thái ──────────────────────────────────────────────────────────
    public static final Color SUCCESS = new Color(0x22C55E);
    public static final Color WARNING = new Color(0xF59E0B);
    public static final Color DANGER = new Color(0xEF4444);
    public static final Color INFO = new Color(0x3B82F6);

    // ─── Font ─────────────────────────────────────────────────────────────────
    public static Font bold(int s) {
        return new Font("SansSerif", Font.BOLD, s);
    }

    public static Font plain(int s) {
        return new Font("SansSerif", Font.PLAIN, s);
    }

    public static Font mono(int s) {
        return new Font("Monospaced", Font.PLAIN, s);
    }

    public static Font fontTitle(float s) {
        return bold((int) s);
    }

    public static Font fontBody(float s) {
        return plain((int) s);
    }

    public static Font fontMono(float s) {
        return mono((int) s);
    }

    public static Font fontMedium(float s) {
        return plain((int) s);
    }

    // ─── applyTheme ───────────────────────────────────────────────────────────
    public static void applyTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        UIManager.put("Panel.background", BG_MAIN);
        UIManager.put("Frame.background", BG_MAIN);
        UIManager.put("Button.background", BG_WHITE);
        UIManager.put("Button.foreground", TEXT_DARK);
        UIManager.put("TextField.background", BG_WHITE);
        UIManager.put("TextField.foreground", TEXT_DARK);
        UIManager.put("TextField.caretForeground", TEAL);
        UIManager.put("Table.background", BG_WHITE);
        UIManager.put("Table.foreground", TEXT_DARK);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.selectionBackground", TEAL_LIGHT);
        UIManager.put("Table.selectionForeground", TEXT_DARK);
        UIManager.put("TableHeader.background", new Color(0xF1F5F9));
        UIManager.put("TableHeader.foreground", TEXT_MID);
        UIManager.put("ScrollBar.background", BG_MAIN);
        UIManager.put("ScrollBar.thumb", BORDER2);
        UIManager.put("Label.foreground", TEXT_DARK);
        UIManager.put("OptionPane.background", BG_WHITE);
    }

    // ─── Card trắng bo góc + shadow ──────────────────────────────────────────
    public static JPanel createCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 14));
                g2.fill(new RoundRectangle2D.Float(1, 3, getWidth() - 2, getHeight() - 2, 14, 14));
                g2.setColor(BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 3, 14, 14));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(new RoundRectangle2D.Float(0.4f, 0.4f, getWidth() - 3, getHeight() - 4, 14, 14));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return p;
    }

    // ─── Stat card màu teal (chữ trắng, trang trí vòng tròn) ────────────────
    public static JPanel createStatCard(String label, String value, String sub, Color bg) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fill(new RoundRectangle2D.Float(2, 5, getWidth() - 2, getHeight() - 3, 14, 14));
                // nền teal gradient
                GradientPaint gp = new GradientPaint(0, 0, bg.brighter(), getWidth(), getHeight(), bg.darker());
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 3, 14, 14));
                // vòng trang trí
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(getWidth() - 55, -15, 75, 75);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(getWidth() - 35, getHeight() - 35, 65, 65);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.setPreferredSize(new Dimension(185, 112));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(plain(10));
        lbl.setForeground(new Color(255, 255, 255, 185));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel val = new JLabel(value);
        val.setFont(bold(27));
        val.setForeground(TEXT_WHITE);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        val.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));

        JLabel s = new JLabel(sub);
        s.setFont(plain(11));
        s.setForeground(new Color(255, 255, 255, 165));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(val);
        card.add(s);
        return card;
    }

    // ─── Nút primary teal ────────────────────────────────────────────────────
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
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
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? TEAL_DARK : TEAL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(bold(13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 36));
        return btn;
    }

    // ─── Nút secondary outline ────────────────────────────────────────────────
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
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
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? TEAL_LIGHT : BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BORDER2);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.setFont(getFont());
                g2.setColor(TEXT_MID);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(plain(13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 36));
        return btn;
    }

    // ─── TextField search ─────────────────────────────────────────────────────
    public static JTextField createTextField(String ph) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(isFocusOwner() ? TEAL : BORDER2);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g2.setFont(plain(13));
                    g2.setColor(TEXT_LIGHT);
                    g2.drawString(ph, 12, getHeight() / 2 + 5);
                }
                g2.dispose();
            }
        };
        tf.setFont(plain(13));
        tf.setForeground(TEXT_DARK);
        tf.setBackground(new Color(0, 0, 0, 0));
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        tf.setPreferredSize(new Dimension(220, 36));
        return tf;
    }

    // ─── Nav item sidebar (text sáng trên nền tối) ───────────────────────────
    public static JPanel createNavItem(String icon, String label, boolean active) {
        JPanel p = new JPanel() {
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
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    g2.setColor(new Color(43, 200, 163, 30));
                    g2.fill(new RoundRectangle2D.Float(6, 2, getWidth() - 12, getHeight() - 4, 10, 10));
                    g2.setColor(SIDEBAR_ACT);
                    g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(4, 9, 4, getHeight() - 9);
                } else if (hov) {
                    g2.setColor(new Color(255, 255, 255, 8));
                    g2.fill(new RoundRectangle2D.Float(6, 2, getWidth() - 12, getHeight() - 4, 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 10));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel ic = new JLabel(icon);
        ic.setFont(plain(15));
        ic.setForeground(active ? SIDEBAR_ACT : new Color(0x90A8BF));

        JLabel lb = new JLabel(label);
        lb.setFont(active ? bold(13) : plain(13));
        lb.setForeground(active ? Color.WHITE : new Color(0x90A8BF));

        p.add(ic);
        p.add(lb);
        return p;
    }

    // ─── Mini bar chart màu teal ──────────────────────────────────────────────
    public static JPanel createMiniBarChart(int[] values, String[] labels, Color barColor) {
        return new JPanel() {
            {
                setOpaque(false);
                setPreferredSize(new Dimension(300, 140));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int n = values.length, pad = 6, gap = 5;
                int barW = (getWidth() - pad * 2 - gap * (n - 1)) / n;
                int maxV = 0;
                for (int v : values)
                    maxV = Math.max(maxV, v);
                int chartH = getHeight() - 24;
                // grid
                g2.setStroke(new BasicStroke(0.5f));
                for (int i = 1; i <= 4; i++) {
                    int y = chartH - chartH * i / 4;
                    g2.setColor(BORDER);
                    g2.drawLine(pad, y, getWidth() - pad, y);
                }
                for (int i = 0; i < n; i++) {
                    int bh = (int) ((double) values[i] / maxV * chartH);
                    int bx = pad + i * (barW + gap), by = chartH - bh;
                    GradientPaint gp = new GradientPaint(bx, by, barColor,
                            bx, chartH, new Color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), 90));
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(bx, by, barW, bh, 4, 4));
                    g2.setFont(plain(9));
                    g2.setColor(TEXT_LIGHT);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(labels[i], bx + (barW - fm.stringWidth(labels[i])) / 2, getHeight() - 5);
                }
                g2.dispose();
            }
        };
    }

    // ─── Section title ────────────────────────────────────────────────────────
    public static JLabel createSectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(bold(16));
        l.setForeground(TEXT_DARK);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }

    // ─── Logo ─────────────────────────────────────────────────────────────────
    public static JPanel createLogo() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        JLabel ico = new JLabel("🎬");
        ico.setFont(plain(20));
        JLabel name = new JLabel("datronsy");
        name.setFont(bold(15));
        name.setForeground(SIDEBAR_ACT);
        p.add(ico);
        p.add(name);
        return p;
    }

    // ─── Divider ──────────────────────────────────────────────────────────────
    public static JSeparator createDivider() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(0xFFFFFF, false)); // tối trên sidebar
        s.setBackground(new Color(0x2D4055));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    public static JPanel row(Component... cs) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        for (Component c : cs)
            p.add(c);
        return p;
    }

    public static JPanel col(Component... cs) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        for (Component c : cs)
            p.add(c);
        return p;
    }
}