package UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import customUI.CustomUI;

/**
 * BanVeHelper – Utility dùng chung cho tất cả panel bán vé.
 */
public final class BanVeHelper {

    // ── Màu ghế (SINGLE SOURCE OF TRUTH – legend = nút ghế) ──────────────────
    public static final Color SEAT_EMPTY = new Color(0x3E5065);
    public static final Color SEAT_SOLD = new Color(0x2A3F52);
    public static final Color SEAT_SELECTED = new Color(0x0098C0);
    public static final Color SEAT_VIP = new Color(0x5B4DB8);

    // ── Màu accent ────────────────────────────────────────────────────────────
    public static final Color ACCENT = new Color(0x00B8D4);
    public static final Color BG_FIELD = new Color(0x1A2A39);
    public static final Color BG_CARD = new Color(0x192330);
    public static final Color DIVIDER = new Color(0x2D3F4F);
    public static final Color BG_MAIN = null;

    private BanVeHelper() {
    }

    // ── Card bo góc tối ───────────────────────────────────────────────────────
    public static JPanel darkCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 14));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        return card;
    }

    // ── Step card bo góc nhạt hơn ─────────────────────────────────────────────
    public static JPanel stepCard(Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        return card;
    }

    // ── Nút primary (teal) ───────────────────────────────────────────────────
    public static JButton primaryBtn(String text) {
        return CustomUI.createPrimaryButton(text);
    }

    // ── Nút ghost (outline) ───────────────────────────────────────────────────
    public static JButton ghostBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(CustomUI.plain(12));
        b.setForeground(CustomUI.TEXT_LIGHT);
        b.setBackground(new Color(0x1A2A39));
        b.setBorder(BorderFactory.createLineBorder(new Color(0x3A4C5E)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    // ── Link button (chỉ text) ────────────────────────────────────────────────
    public static JButton linkBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(CustomUI.bold(12));
        b.setForeground(ACCENT);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── TextField có placeholder ──────────────────────────────────────────────
    public static JTextField placeholderField(String placeholder) {
        JTextField f = new JTextField();
        f.setText(placeholder);
        f.setForeground(new Color(0x607D8B));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(CustomUI.TEXT_WHITE);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isBlank()) {
                    f.setText(placeholder);
                    f.setForeground(new Color(0x607D8B));
                }
            }
        });
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3A4C5E)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        f.setBackground(BG_FIELD);
        f.setFont(CustomUI.plain(12));
        f.setCaretColor(CustomUI.TEXT_WHITE);
        return f;
    }

    // ── ComboBox style tối ────────────────────────────────────────────────────
    public static <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setBackground(new Color(0x16212A));
        c.setForeground(CustomUI.TEXT_LIGHT);
        c.setFont(CustomUI.plain(12));
        c.setBorder(BorderFactory.createLineBorder(new Color(0x2D3F4F)));
        return c;
    }

    // ── Label section ─────────────────────────────────────────────────────────
    public static JLabel sectionLabel(String icon, String text) {
        JLabel l = new JLabel(icon + "  " + text);
        l.setFont(CustomUI.bold(12));
        l.setForeground(new Color(0x7EB8D4));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ── Row label/value ───────────────────────────────────────────────────────
    public static JPanel infoRow(String label, String value) {
        JLabel v = new JLabel(value);
        v.setFont(CustomUI.bold(13));
        v.setForeground(CustomUI.TEXT_WHITE);
        return infoRow(label, v);
    }

    public static JPanel infoRow(String label, JLabel valLabel) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(CustomUI.plain(12));
        l.setForeground(CustomUI.TEXT_LIGHT);
        l.setPreferredSize(new Dimension(120, 20));
        row.add(l, BorderLayout.WEST);
        row.add(valLabel, BorderLayout.CENTER);
        return row;
    }

    // ── Divider ngang ─────────────────────────────────────────────────────────
    public static Component divider() {
        JPanel d = new JPanel();
        d.setBackground(DIVIDER);
        d.setOpaque(true);
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setPreferredSize(new Dimension(0, 1));
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        return d;
    }

    // ── Vertical gap ─────────────────────────────────────────────────────────
    public static Component vgap(int h) {
        return Box.createVerticalStrut(h);
    }

    // ── Format VND ───────────────────────────────────────────────────────────
    public static String formatVND(long amount) {
        return String.format("%,d đ", amount).replace(',', '.');
    }

    // ── Nút stepper +/− ──────────────────────────────────────────────────────
    public static JButton stepperBtn(String t, int size) {
        JButton b = new JButton(t);
        b.setPreferredSize(new Dimension(size, size));
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setForeground(CustomUI.TEXT_WHITE);
        b.setBackground(new Color(0x243447));
        b.setOpaque(true);
        b.setBorder(BorderFactory.createLineBorder(new Color(0x3A5070)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}