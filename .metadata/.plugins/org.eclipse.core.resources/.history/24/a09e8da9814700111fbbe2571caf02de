package UI;

import java.awt.*;
import javax.swing.*;
import customUI.CustomUI;
import model.BookingState;
import model.CinemaData;

public class BookingInfoPanel extends JPanel {

    public interface RoomChangeListener {
        void onRoomChanged(int roomKey);
    }

    private final BookingState state;
    private final Runnable onConfirm;
    private final RoomChangeListener onRoomChange;

    private JTextArea lblGhe;
    private JLabel lblLoai, lblTong, posterLabel;
    private JComboBox<String> comboRap, comboPhim, comboSuat, comboPhong;
    private JTextField fieldTen, fieldPhone, fieldEmail;

    public BookingInfoPanel(BookingState state, Runnable onConfirm, RoomChangeListener onRoomChange) {
        this.state = state;
        this.onConfirm = onConfirm;
        this.onRoomChange = onRoomChange;
        
        setOpaque(false);
        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(buildInner());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildInner() {
        JPanel card = BanVeHelper.darkCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Section: Chọn phim
        content.add(sectionLabel("🎬  CHỌN PHIM & SUẤT CHIẾU"));
        content.add(vgap(10));
        content.add(buildSelectionSection());
        content.add(vgap(16));
        content.add(divider());
        
        // Section: Thông tin ghế
        content.add(vgap(12));
        content.add(sectionLabel("🪑  THÔNG TIN GHẾ"));
        lblGhe = new JTextArea("-");
        lblGhe.setEditable(false);
        lblGhe.setLineWrap(true);
        lblGhe.setOpaque(false);
        lblGhe.setForeground(Color.WHITE);
        lblLoai = boldVal("-");
        content.add(seatRow("Ghế đã chọn", lblGhe));
        content.add(infoRow("Loại ghế", lblLoai));
        
        // Section: Tổng tiền & Xác nhận
        content.add(vgap(16));
        lblTong = new JLabel("0 đ");
        lblTong.setFont(CustomUI.bold(20));
        lblTong.setForeground(new Color(0x00B8D4));
        content.add(lblTong);
        
        JButton btnConfirm = CustomUI.createPrimaryButton("Xác nhận → Chọn bắp & nước");
        btnConfirm.addActionListener(e -> handleConfirm());
        content.add(vgap(10));
        content.add(btnConfirm);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSelectionSection() {
        JPanel wrap = new JPanel(new BorderLayout(12, 0));
        wrap.setOpaque(false);

        posterLabel = new JLabel(buildPosterIcon(0, 80, 115));
        wrap.add(posterLabel, BorderLayout.WEST);

        JPanel combos = new JPanel(new GridLayout(2, 2, 10, 10));
        combos.setOpaque(false);

        comboPhim = styledCombo(CinemaData.PHIM_LIST);
        comboPhim.addActionListener(e -> {
            state.phimIdx = comboPhim.getSelectedIndex();
            rebuildPhongCombo(state.phimIdx);
            posterLabel.setIcon(buildPosterIcon(state.phimIdx, 80, 115));
        });

        comboPhong = styledCombo(CinemaData.PHONG_BY_PHIM[0]);
        comboPhong.addActionListener(e -> handlePhongChange());

        combos.add(wrapCombo("Phim", comboPhim));
        combos.add(wrapCombo("Phòng chiếu", comboPhong));
        wrap.add(combos, BorderLayout.CENTER);
        return wrap;
    }

    private void handlePhongChange() {
        state.phongIdx = Math.max(0, comboPhong.getSelectedIndex());
        state.seats.clear();
        state.seatsVip.clear();
        refreshSeatInfo();
        if (onRoomChange != null) {
            onRoomChange.onRoomChanged(getCurrentRoomKey());
        }
    }

    private void rebuildPhongCombo(int phimIdx) {
        comboPhong.setModel(new DefaultComboBoxModel<>(CinemaData.PHONG_BY_PHIM[phimIdx]));
        handlePhongChange();
    }

    public int getCurrentRoomKey() {
        return CinemaData.PHONG_ROOM_KEY[state.phimIdx][state.phongIdx];
    }

    public void refreshSeatInfo() {
        if (state.seats.isEmpty()) {
            lblGhe.setText("-");
            lblTong.setText("0 đ");
        } else {
            lblGhe.setText(state.gheDisplay());
            lblTong.setText(BanVeHelper.formatVND(state.tienVe()));
        }
    }

    private void handleConfirm() {
        if (state.seats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ghế!");
            return;
        }
        onConfirm.run();
    }

    // --- Các Helper UI (divider, vgap, styledCombo...) bạn giữ nguyên như code cũ ---
    private Component vgap(int h) { return Box.createVerticalStrut(h); }
    private JLabel sectionLabel(String text) { 
        JLabel l = new JLabel(text); 
        l.setForeground(new Color(0x90CAF9)); 
        return l; 
    }
    private JPanel divider() { 
        JPanel d = new JPanel(); 
        d.setBackground(new Color(0x2D3F4F)); 
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); 
        return d; 
    }
    private <T> JComboBox<T> styledCombo(T[] items) { return new JComboBox<>(items); }
    private JPanel wrapCombo(String label, JComboBox<?> combo) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(combo, BorderLayout.CENTER);
        p.setOpaque(false);
        return p;
    }
    private JLabel boldVal(String text) { return new JLabel(text); }
    private JPanel infoRow(String label, JLabel val) { return new JPanel(); } // Dummy
    private JPanel seatRow(String label, JTextArea area) { 
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(label), BorderLayout.WEST);
        p.add(area, BorderLayout.CENTER);
        p.setOpaque(false);
        return p;
    }
    private ImageIcon buildPosterIcon(int idx, int w, int h) { return new ImageIcon(); }
}