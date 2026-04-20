package customerUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class CustomerUI {

    // ─── Màu nền tối ──────────────────────────────────────────────────────────────
    public static final Color BG_MAIN = new Color(0x1B2B3A); // nền chính tối
    public static final Color BG_CARD = new Color(0x192330); // card tối
    public static final Color SIDEBAR_BG = new Color(0x1B2B3A); // nền sidebar

    // ─── Teal chủ đạo ────────────────────────────────────────────────────────
    public static final Color TEAL = new Color(0x2BC8A3);

    // ─── Chữ ──────────────────────────────────────────────────────────────────
    public static final Color TEXT_LIGHT = new Color(0x8FA5BB); // chữ sáng
    public static final Color TEXT_WHITE = Color.WHITE;

    // ─── Font ─────────────────────────────────────────────────────────────────
    public static Font bold(int s) {
        return new Font("SansSerif", Font.BOLD, s);
    }

    public static Font plain(int s) {
        return new Font("SansSerif", Font.PLAIN, s);
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
                g2.setColor(hov ? TEAL.darker() : TEAL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(bold(14));
        btn.setPreferredSize(new Dimension(0, 42));
        return btn;
    }
}