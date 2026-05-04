package UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import customUI.CustomUI;
import model.BookingState;
import model.CinemaData;

public class ConfirmDialog extends JDialog {
    private JComboBox<String> paymentMethod;
    private JLabel qrLabel;

    public ConfirmDialog(Window parent, BookingState state) {
        super(parent, "Xác Nhận Đặt Vé", ModalityType.APPLICATION_MODAL);
        setUndecorated(false);
        setResizable(false);
        setSize(620, 580);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(0x111D2A));

        setContentPane(buildContent(state));
    }

    private JPanel buildContent(BookingState state) {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(0x111D2A));
        root.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ── Header màu xanh ───────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x0D1F2D));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel icon = new JLabel("🎟️", JLabel.LEFT);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JPanel titleWrap = new JPanel(new BorderLayout(0, 2));
        titleWrap.setOpaque(false);
        JLabel title = new JLabel("XÁC NHẬN ĐẶT VÉ");
        title.setFont(CustomUI.bold(16));
        title.setForeground(new Color(0x00B8D4));

        String rap = CinemaData.RAP_LIST[state.rapIdx];
        JLabel rapLbl = new JLabel(rap + "  •  " + CinemaData.RAP_DIA_CHI[state.rapIdx]);
        rapLbl.setFont(CustomUI.plain(11));
        rapLbl.setForeground(CustomUI.TEXT_LIGHT);

        titleWrap.add(title, BorderLayout.NORTH);
        titleWrap.add(rapLbl, BorderLayout.CENTER);
        header.add(icon, BorderLayout.WEST);
        header.add(titleWrap, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // ── Body ──────────────────────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(new Color(0x111D2A));
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Thời gian block
        JPanel timeBlock = buildTimeBlock(state);
        body.add(timeBlock);
        body.add(Box.createVerticalStrut(14));
        body.add(divider());
        body.add(Box.createVerticalStrut(12));
        // ── Thanh toán ───────────────────────────────────────
        JPanel payRow = new JPanel(new BorderLayout(8, 0));
        payRow.setOpaque(false);
        payRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel payLbl = new JLabel("💳  Thanh toán");
        payLbl.setFont(CustomUI.plain(12));
        payLbl.setForeground(CustomUI.TEXT_LIGHT);

        paymentMethod = new JComboBox<>(new String[] {
                "Tiền mặt",
                "Chuyển khoản QR"
        });
        paymentMethod.setFont(CustomUI.plain(12));

        payRow.add(payLbl, BorderLayout.WEST);
        payRow.add(paymentMethod, BorderLayout.CENTER);

        body.add(payRow);
        body.add(Box.createVerticalStrut(12));
        qrLabel = new JLabel();
        qrLabel.setHorizontalAlignment(JLabel.CENTER);
        qrLabel.setVisible(false);

        body.add(qrLabel);
        body.add(Box.createVerticalStrut(12));
        // Chi tiết vé
        body.add(infoRow("🎬  Phim", CinemaData.PHIM_LIST[state.phimIdx]));
        body.add(Box.createVerticalStrut(8));
        body.add(infoRow("🏛️  Phòng chiếu", CinemaData.PHONG_BY_PHIM[state.phimIdx][state.phongIdx]));
        body.add(Box.createVerticalStrut(8));
        body.add(infoRow("🪑  Ghế", state.gheDisplay() + "  (" + state.loaiGheDisplay() + ")"));
        body.add(Box.createVerticalStrut(8));
        body.add(infoRow("👤  Khách hàng", nameOrGuest(state.tenKhachHang)));
        body.add(Box.createVerticalStrut(8));
        body.add(infoRow("📱  Điện thoại", emptyOrDash(state.soDienThoai)));
        body.add(Box.createVerticalStrut(8));
        body.add(infoRow("📧  Email", emptyOrDash(state.email)));
        body.add(Box.createVerticalStrut(10));

        // Combo chọn (nếu có)
        String snack = state.snackSummary();
        body.add(infoRow("🍿  Bắp & Nước", snack != null ? snack : "Không chọn"));
        body.add(Box.createVerticalStrut(12));
        body.add(divider());
        body.add(Box.createVerticalStrut(12));

        // Tổng tiền
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel tl = new JLabel("TỔNG THANH TOÁN");
        tl.setFont(CustomUI.bold(13));
        tl.setForeground(CustomUI.TEXT_LIGHT);
        JLabel tv = new JLabel(BanVeHelper.formatVND(state.tongCong()));
        tv.setFont(CustomUI.bold(22));
        tv.setForeground(new Color(0x00B8D4));
        totalRow.add(tl, BorderLayout.WEST);
        totalRow.add(tv, BorderLayout.EAST);
        body.add(totalRow);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(scroll, BorderLayout.CENTER);

        // ── Footer: Xác nhận / Đóng ───────────────────────────────────────────
        JPanel footer = new JPanel(new GridLayout(1, 2, 12, 0));
        footer.setBackground(new Color(0x0D1F2D));
        footer.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(CustomUI.bold(13));
        btnClose.setForeground(CustomUI.TEXT_LIGHT);
        btnClose.setBackground(new Color(0x243447));
        btnClose.setOpaque(true);
        btnClose.setBorder(BorderFactory.createLineBorder(new Color(0x3A5070)));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        JButton btnPay = CustomUI.createPrimaryButton("✓  XÁC NHẬN & THANH TOÁN");
        btnPay.setFont(CustomUI.bold(13));
        btnPay.addActionListener(e -> {
            dispose();
            JOptionPane.showMessageDialog(null,
                    "🎉  Đặt vé thành công!\nVé điện tử đã được gửi tới:\n" + emptyOrDash(state.email),
                    "Đặt Vé Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        footer.add(btnClose);
        footer.add(btnPay);
        root.add(footer, BorderLayout.SOUTH);

        paymentMethod.addActionListener(e -> {
            String selected = (String) paymentMethod.getSelectedItem();

            if ("Chuyển khoản QR".equals(selected)) {
                showQR();
                qrLabel.setVisible(true);
            } else {
                qrLabel.setVisible(false);
            }

            body.revalidate();
            body.repaint();
        });
        return root;
    }

    // ── Khối thời gian nổi bật ───────────────────────────────────────────────
    private JPanel buildTimeBlock(BookingState state) {
        JPanel block = new JPanel(new GridLayout(1, 3, 0, 0));
        block.setOpaque(true);
        block.setBackground(new Color(0x0A1929));
        block.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x00B8D4, false), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        block.add(timeCell("GIỜ BẮT ĐẦU", state.gioBatDau()));
        block.add(timeCell("GIỜ KẾT THÚC", state.gioKetThuc()));
        block.add(dateCell(state));

        return block;
    }

    private JPanel timeCell(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label, JLabel.CENTER);
        l.setFont(CustomUI.plain(10));
        l.setForeground(CustomUI.TEXT_LIGHT);
        JLabel v = new JLabel(value, JLabel.CENTER);
        v.setFont(CustomUI.bold(24));
        v.setForeground(new Color(0x00B8D4));
        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JPanel dateCell(BookingState state) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel("NGÀY CHIẾU", JLabel.CENTER);
        l.setFont(CustomUI.plain(10));
        l.setForeground(CustomUI.TEXT_LIGHT);

        // "Thứ X, dd/MM/yyyy"
        String suat = state.suatRawDisplay();
        String ngay = state.ngayChieu();
        String thu = state.thuDisplay();

        JPanel dateWrap = new JPanel(new BorderLayout(0, 2));
        dateWrap.setOpaque(false);
        JLabel thuLbl = new JLabel(thu, JLabel.CENTER);
        thuLbl.setFont(CustomUI.bold(13));
        thuLbl.setForeground(CustomUI.TEXT_WHITE);
        JLabel ngayLbl = new JLabel(ngay, JLabel.CENTER);
        ngayLbl.setFont(CustomUI.plain(12));
        ngayLbl.setForeground(CustomUI.TEXT_LIGHT);
        dateWrap.add(thuLbl, BorderLayout.NORTH);
        dateWrap.add(ngayLbl, BorderLayout.CENTER);

        p.add(l, BorderLayout.NORTH);
        p.add(dateWrap, BorderLayout.CENTER);
        return p;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel l = new JLabel(label);
        l.setFont(CustomUI.plain(12));
        l.setForeground(CustomUI.TEXT_LIGHT);
        l.setPreferredSize(new Dimension(150, 20));
        JLabel v = new JLabel(value);
        v.setFont(CustomUI.bold(12));
        v.setForeground(CustomUI.TEXT_WHITE);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.CENTER);
        return row;
    }

    private void showQR() {
        try {
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/resources/qr.png"));

            Image img = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
            qrLabel.setIcon(new ImageIcon(img));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy QR!");
            e.printStackTrace();
        }
    }

    private JPanel divider() {
        JPanel d = new JPanel();
        d.setOpaque(true);
        d.setBackground(new Color(0x2D3F4F));
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setPreferredSize(new Dimension(0, 1));
        return d;
    }

    private String emptyOrDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private String nameOrGuest(String s) {
        return (s == null || s.isBlank()) ? "Khách vãng lai" : s;
    }
}