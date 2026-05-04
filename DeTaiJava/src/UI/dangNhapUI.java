package UI;

import customUI.CustomUI;
import DAO.TaiKhoanDAO;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class dangNhapUI extends JPanel {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JLabel lblError;

    private final TaiKhoanDAO dao = new TaiKhoanDAO();

    // Callback để báo lên JFrame cha khi đăng nhập thành công
    public interface LoginCallback {
        void onLoginSuccess(TaiKhoan taiKhoan);
    }

    private LoginCallback callback;

    public void setLoginCallback(LoginCallback cb) {
        this.callback = cb;
    }

    // ─────────────────────────────────────────────────────────────────────────
    public dangNhapUI() {
        setLayout(new BorderLayout());
        setBackground(CustomUI.BG_MAIN);
        add(buildCenter(), BorderLayout.CENTER);
    }

    // ── Vùng giữa: chia đôi trái/phải ───────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridLayout(1, 2));
        center.setOpaque(false);
        center.add(buildLeftPanel());
        center.add(buildRightPanel());
        return center;
    }

    // ── Bên trái: banner gradient + logo ─────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient nền
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x3B4A6B),
                        getWidth(), getHeight(), new Color(0x2A3A5C));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Vòng tròn trang trí
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-60, -60, 300, 300);
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fillOval(getWidth() - 120, getHeight() - 120, 250, 250);
                g2.dispose();
            }
        };
        p.setLayout(new GridBagLayout());
        p.setOpaque(false);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        // Icon
        JLabel ico = new JLabel("🎬");
        ico.setFont(new Font("SansSerif", Font.PLAIN, 72));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tên app
        JLabel title = new JLabel("MEGADE");
        title.setFont(CustomUI.bold(38));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Cinema Management");
        sub.setFont(CustomUI.plain(16));
        sub.setForeground(new Color(255, 255, 255, 180));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Mô tả
        JLabel desc = new JLabel("<html><center>"
                + "Hệ thống quản lý rạp chiếu phim<br/>"
                + "toàn diện & chuyên nghiệp"
                + "</center></html>");
        desc.setFont(CustomUI.plain(13));
        desc.setForeground(new Color(255, 255, 255, 140));
        desc.setHorizontalAlignment(SwingConstants.CENTER);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(ico);
        inner.add(Box.createVerticalStrut(12));
        inner.add(title);
        inner.add(Box.createVerticalStrut(6));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(24));
        inner.add(sep);
        inner.add(Box.createVerticalStrut(20));
        inner.add(desc);

        p.add(inner);
        return p;
    }

    // ── Bên phải: form đăng nhập ─────────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(CustomUI.BG_MAIN);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fill(new RoundRectangle2D.Float(3, 5, getWidth() - 4, getHeight() - 4, 20, 20));
                g2.setColor(CustomUI.BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 4, 20, 20));
                g2.setColor(CustomUI.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 4, getHeight() - 5, 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 44, 40, 44));
        card.setPreferredSize(new Dimension(400, 460));

        // Tiêu đề form
        JLabel heading = new JLabel("Đăng nhập");
        heading.setFont(CustomUI.bold(26));
        heading.setForeground(CustomUI.TEXT_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hint = new JLabel("Nhập thông tin tài khoản của bạn");
        hint.setFont(CustomUI.plain(13));
        hint.setForeground(CustomUI.TEXT_LIGHT);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Trường tên đăng nhập
        JLabel lblUser = fieldLabel("Tên đăng nhập");
        txtUser = CustomUI.createTextField("Nhập tên đăng nhập...");
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Trường mật khẩu
        JLabel lblPass = fieldLabel("Mật khẩu");
        txtPass = buildPasswordField();
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Dòng lỗi
        lblError = new JLabel(" ");
        lblError.setFont(CustomUI.plain(12));
        lblError.setForeground(CustomUI.DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nút đăng nhập
        JButton btnLogin = buildLoginButton();
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        // Footer hint
        JLabel footer = new JLabel("Admin: admin / admin123  |  NV: nv01 / nv123");
        footer.setFont(CustomUI.plain(11));
        footer.setForeground(CustomUI.TEXT_LIGHT);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Enter = login
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    doLogin();
            }
        };
        txtUser.addKeyListener(enterKey);
        txtPass.addKeyListener(enterKey);

        card.add(heading);
        card.add(Box.createVerticalStrut(6));
        card.add(hint);
        card.add(Box.createVerticalStrut(30));
        card.add(lblUser);
        card.add(Box.createVerticalStrut(6));
        card.add(txtUser);
        card.add(Box.createVerticalStrut(18));
        card.add(lblPass);
        card.add(Box.createVerticalStrut(6));
        card.add(txtPass);
        card.add(Box.createVerticalStrut(10));
        card.add(lblError);
        card.add(Box.createVerticalStrut(20));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(20));
        card.add(footer);

        outer.add(card);
        return outer;
    }

    // ── Helpers UI ───────────────────────────────────────────────────────────
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CustomUI.bold(13));
        l.setForeground(CustomUI.TEXT_MID);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPasswordField buildPasswordField() {
        JPasswordField pf = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CustomUI.BG_WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(isFocusOwner() ? CustomUI.PRIMARY : CustomUI.BORDER2);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        pf.setFont(CustomUI.plain(14));
        pf.setForeground(CustomUI.TEXT_DARK);
        pf.setOpaque(false);
        pf.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pf.setEchoChar('●');
        return pf;
    }

    private JButton buildLoginButton() {
        JButton btn = new JButton("Đăng nhập") {
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
                g2.setColor(hov ? CustomUI.PRIMARY_DARK : CustomUI.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setFont(CustomUI.bold(15));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText();
                g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> doLogin());
        return btn;
    }

    // ── Xử lý đăng nhập ──────────────────────────────────────────────────────
    private void doLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("⚠  Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        TaiKhoan tk = dao.dangNhap(user, pass);

        if (tk == null) {
            lblError.setText("✗  Sai tên đăng nhập hoặc mật khẩu!");
            txtPass.setText("");
        } else {
            lblError.setText(" ");
            if (callback != null)
                callback.onLoginSuccess(tk);
        }
    }
}
