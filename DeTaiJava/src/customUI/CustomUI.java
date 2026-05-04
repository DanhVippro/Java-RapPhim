package customUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class CustomUI {

    // ─── Background (pastel nhẹ, sáng) ─────────────────────────
    public static final Color BG_MAIN = new Color(0xF9FAFF); // trắng pha xanh rất nhẹ
    public static final Color BG_WHITE = new Color(0xFFFFFF); // trắng tinh
    public static final Color BG_ROW_ALT = new Color(0xF0F4FF); // tím nhạt xen kẽ

    // ─── Sidebar (tím pastel) ────────────────────────────
    public static final Color SIDEBAR_BG = new Color(0x2D2A5E); // tím đậm dễ thương
    public static final Color SIDEBAR_ACT = new Color(0x6C63FF); // tím xanh sáng

    // ─── Primary (xanh dương logo) ─────────────────────
    public static final Color PRIMARY = new Color(0x5B8DEF); // xanh dương pastel
    public static final Color PRIMARY_DARK = new Color(0x4A7BD9);
    public static final Color PRIMARY_LIGHT = new Color(0x8AADF5);

    // ─── Accent (hồng cam / màu chữ Cinema) ──────────────────
    public static final Color ACCENT = new Color(0xF5A0A8); // hồng san hô nhẹ
    public static final Color ACCENT_DARK = new Color(0xE88996);
    public static final Color ACCENT_LIGHT = new Color(0xFFC4CC);

    // ─── Card stats (theo logo: xanh, hồng, tím) ─────────────
    public static final Color CARD_1 = PRIMARY; // xanh
    public static final Color CARD_2 = ACCENT; // hồng
    public static final Color CARD_3 = new Color(0xC5A3FF); // tím lavender

    // ─── Text (đậm hơn một chút cho dễ đọc trên nền sáng) ───
    public static final Color TEXT_DARK = new Color(0x2D2A5E); // tím than
    public static final Color TEXT_MID = new Color(0x6B6B8D);
    public static final Color TEXT_LIGHT = new Color(0xA8A8C5);
    public static final Color TEXT_WHITE = Color.WHITE;

    // ─── Border (nhẹ nhàng) ─────────────────────────────
    public static final Color BORDER = new Color(0xE2E6F0);
    public static final Color BORDER2 = new Color(0xCFD5E6);

    // ─── Trạng thái (pastel hơn) ──────────────────────────────
    public static final Color SUCCESS = new Color(0x9FD9B5); // xanh mint pastel
    public static final Color WARNING = new Color(0xFFDB9E); // cam pastel
    public static final Color DANGER = new Color(0xFFB3BA); // hồng đỏ nhẹ
    public static final Color INFO = new Color(0x8AADF5); // xanh pastel

    // ─── Font ─────────────────────────────────────────────────
    public static Font bold(int s) {
        return new Font("Segoe UI", Font.BOLD, s);
    }

    public static Font plain(int s) {
        return new Font("Segoe UI", Font.PLAIN, s);
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

    // ─── applyTheme ───────────────────────────────────────────
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
        UIManager.put("TextField.caretForeground", PRIMARY);
        UIManager.put("Table.background", BG_WHITE);
        UIManager.put("Table.foreground", TEXT_DARK);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.selectionBackground", PRIMARY_LIGHT);
        UIManager.put("Table.selectionForeground", TEXT_WHITE);
        UIManager.put("TableHeader.background", new Color(0xEDF2FF));
        UIManager.put("TableHeader.foreground", TEXT_MID);
        UIManager.put("ScrollBar.background", BG_MAIN);
        UIManager.put("ScrollBar.thumb", BORDER2);
        UIManager.put("Label.foreground", TEXT_DARK);
        UIManager.put("OptionPane.background", BG_WHITE);
    }

    // ─── Card trắng bo góc + shadow nhẹ ──────────────────────────
    public static JPanel createCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fill(new RoundRectangle2D.Float(1, 3, getWidth() - 2, getHeight() - 2, 16, 16));
                g2.setColor(BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 3, 16, 16));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(new RoundRectangle2D.Float(0.4f, 0.4f, getWidth() - 3, getHeight() - 4, 16, 16));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return p;
    }

    // ─── Stat card theo màu logo ─────────
    public static JPanel createStatCard(String label, String value, String sub, Color bg) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 30, 20));
                g2.fill(new RoundRectangle2D.Float(2, 5, getWidth() - 2, getHeight() - 3, 16, 16));
                GradientPaint gp = new GradientPaint(0, 0, bg.brighter(), getWidth(), getHeight(), bg.darker());
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 3, 16, 16));
                // vòng trang trí pastel
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(getWidth() - 55, -15, 75, 75);
                g2.setColor(new Color(255, 255, 255, 15));
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
        lbl.setForeground(new Color(255, 255, 255, 200));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel val = new JLabel(value);
        val.setFont(bold(27));
        val.setForeground(TEXT_WHITE);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        val.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));

        JLabel s = new JLabel(sub);
        s.setFont(plain(11));
        s.setForeground(new Color(255, 255, 255, 180));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(val);
        card.add(s);
        return card;
    }

    // ─── Nút primary (xanh logo) ───────────────────────────────────
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
                g2.setColor(hov ? PRIMARY_DARK : PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
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

    // ─── Nút outline (màu tím) ────────────────────────────────
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
                g2.setColor(hov ? new Color(0x6C63FF, 40, getHorizontalAlignment()) : BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(SIDEBAR_ACT);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.setFont(getFont());
                g2.setColor(SIDEBAR_ACT);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
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

    // ─── TextField search ─────────────────────────────────────
    public static JTextField createTextField(String ph) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(isFocusOwner() ? PRIMARY : BORDER2);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
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

    // ─── Nav item sidebar (tím thanh lịch) ─────────────────────
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
                    g2.setColor(new Color(108, 99, 255, 30));
                    g2.fill(new RoundRectangle2D.Float(6, 2, getWidth() - 12, getHeight() - 4, 12, 12));
                    g2.setColor(SIDEBAR_ACT);
                    g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(4, 9, 4, getHeight() - 9);
                } else if (hov) {
                    g2.setColor(new Color(108, 99, 255, 15));
                    g2.fill(new RoundRectangle2D.Float(6, 2, getWidth() - 12, getHeight() - 4, 12, 12));
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
        ic.setForeground(active ? SIDEBAR_ACT : new Color(0xA8A8C5));

        JLabel lb = new JLabel(label);
        lb.setFont(active ? bold(13) : plain(13));
        lb.setForeground(active ? Color.WHITE : new Color(0xC5C5E0));

        p.add(ic);
        p.add(lb);
        return p;
    }

    // ─── Mini bar chart màu pastel ────────────────────────────
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
                            bx, chartH, new Color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), 70));
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(bx, by, barW, bh, 6, 6));
                    g2.setFont(plain(9));
                    g2.setColor(TEXT_MID);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(labels[i], bx + (barW - fm.stringWidth(labels[i])) / 2, getHeight() - 5);
                }
                g2.dispose();
            }
        };
    }

    // ─── Section title ────────────────────────────────────────
    public static JLabel createSectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(bold(16));
        l.setForeground(TEXT_DARK);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }

    // ─── Logo (theo ảnh MeGeDe Cinema) ─────────────────────────
    public static JPanel createLogo() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel logoMain = new JLabel("MeGeDe");
        logoMain.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoMain.setForeground(PRIMARY);
        logoMain.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cinemaSub = new JLabel("CINEMA");
        cinemaSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cinemaSub.setForeground(ACCENT);
        cinemaSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        cinemaSub.setBorder(BorderFactory.createEmptyBorder(-5, 0, 5, 0));

        JLabel slogan = new JLabel("XEM PHIM · CƯỜI CHẬT · TRẢI NGHIỆM CỰC ĐÃ");
        slogan.setFont(plain(9));
        slogan.setForeground(TEXT_LIGHT);
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(logoMain);
        p.add(cinemaSub);
        p.add(slogan);
        return p;
    }

    // ─── Divider ──────────────────────────────────────────────
    public static JSeparator createDivider() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setBackground(BORDER);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    // ─── Helpers ──────────────────────────────────────────────
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