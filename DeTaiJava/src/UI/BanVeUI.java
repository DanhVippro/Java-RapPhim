package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import customerUI.CustomerUI;

public class BanVeUI extends JPanel {
    public BanVeUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(CustomerUI.SIDEBAR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBookingPage(), BorderLayout.CENTER);
    }

    // Header top
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Bán Vé");
        title.setFont(CustomerUI.bold(28));
        title.setForeground(CustomerUI.TEXT_LIGHT);
        header.add(title, BorderLayout.WEST);

        return header;
    }

    // Nội dung chính
    private JPanel buildBookingPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setOpaque(false);

        page.add(buildSelectionBanner(), BorderLayout.NORTH);
        page.add(buildBookingArea(), BorderLayout.CENTER);
        return page;
    }

    private JPanel buildSelectionBanner() {
        JPanel banner = createDarkCard();
        banner.setLayout(new GridLayout(1, 3, 16, 0));
        banner.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        banner.add(createLabeledCombo("Phim", new String[] {"Avatar 2", "Kẻ Cắp Giấc Mơ", "Spider-Man"}));
        banner.add(createLabeledCombo("Suất", new String[] {"19:00, Thứ 6, 21/6/2024", "14:00, Thứ 7, 22/6/2024"}));
        banner.add(createLabeledCombo("Phòng", new String[] {"Phòng chiếu 3", "Phòng chiếu 5"}));

        return banner;
    }

    private JPanel buildBookingArea() {
        JPanel area = new JPanel(new GridLayout(1, 2, 18, 0));
        area.setOpaque(false);

        area.add(buildSeatPanel());
        area.add(buildInfoPanel());

        return area;
    }

    private JPanel buildSeatPanel() {
        JPanel panel = createDarkCard();
        panel.setLayout(new BorderLayout(0, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JLabel section = new JLabel("MÀN HÌNH", JLabel.CENTER);
        section.setFont(CustomerUI.bold(12));
        section.setForeground(CustomerUI.TEXT_LIGHT);
        panel.add(section, BorderLayout.NORTH);

        JPanel seats = new JPanel(new GridLayout(7, 11, 10, 10));
        seats.setOpaque(false);

        String[] rows = {"A", "B", "C", "D", "E", "F", "G"};
        boolean[][] sold = {
            {false, false, true, true, false, false, false, false, false, false, false},
            {false, false, false, true, true, false, false, true, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, true, true, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, true, true},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false}
        };
        boolean[][] vip = {
            {false, false, false, false, false, false, false, false, false, false, true},
            {false, false, false, false, false, false, false, false, false, true, true},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false, false, false, false}
        };

        for (int r = 0; r < rows.length; r++) {
            for (int c = 1; c <= 11; c++) {
                String label = rows[r] + c;
                seats.add(createSeatButton(label, sold[r][c - 1], vip[r][c - 1]));
            }
        }

        panel.add(seats, BorderLayout.CENTER);
        panel.add(buildLegend(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 8));
        legend.setOpaque(false);

        legend.add(createLegendItem(new Color(0x3E5065), "Còn trống"));
        legend.add(createLegendItem(new Color(0x264359), "Đã bán"));
        legend.add(createLegendItem(CustomerUI.TEAL, "Đang chọn"));
        legend.add(createLegendItem(new Color(0x5B4DB8), "VIP"));

        return legend;
    }

    private JPanel createLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);

        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(12, 12));
        dot.setOpaque(true);
        dot.setBackground(color);
        dot.setBorder(BorderFactory.createLineBorder(new Color(0x223042)));

        JLabel label = new JLabel(text);
        label.setFont(CustomerUI.plain(11));
        label.setForeground(CustomerUI.TEXT_LIGHT);

        item.add(dot);
        item.add(label);
        return item;
    }

    private JPanel buildInfoPanel() {
        JPanel panel = createDarkCard();
        panel.setLayout(new BorderLayout(0, 22));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JLabel header = new JLabel("THÔNG TIN ĐẶT VÉ");
        header.setFont(CustomerUI.bold(14));
        header.setForeground(CustomerUI.TEXT_LIGHT);
        panel.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(9, 1, 10, 10));
        content.setOpaque(false);

        content.add(createInfoRow("Phim", "Avatar 2"));
        content.add(createInfoRow("Suất", "19:00"));
        content.add(createInfoRow("Phòng", "Phòng chiếu 3"));
        content.add(createInfoRow("Ghế", "A3, A4"));
        content.add(createInfoRow("Loại", "VIP"));

        content.add(createFormField("Tên khách hàng..."));
        content.add(createFormField("Số điện thoại..."));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel totalLabel = new JLabel("Tổng cộng");
        totalLabel.setFont(CustomerUI.bold(16));
        totalLabel.setForeground(CustomerUI.TEXT_LIGHT);
        JLabel totalValue = new JLabel("0 đ");
        totalValue.setFont(CustomerUI.bold(18));
        totalValue.setForeground(CustomerUI.TEAL);
        totalRow.add(totalLabel, BorderLayout.WEST);
        totalRow.add(totalValue, BorderLayout.EAST);
        content.add(totalRow);

        panel.add(content, BorderLayout.CENTER);

        JButton confirm = CustomerUI.createPrimaryButton("Xác nhận đặt vé");
        confirm.setPreferredSize(new Dimension(0, 42));
        panel.add(confirm, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInfoRow(String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(CustomerUI.plain(12));
        label.setForeground(CustomerUI.TEXT_LIGHT);

        JLabel value = new JLabel(valueText);
        value.setFont(CustomerUI.bold(13));
        value.setForeground(CustomerUI.TEXT_WHITE);

        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JPanel createFormField(String placeholder) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JTextField field = new JTextField(placeholder);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3A4C5E)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        field.setBackground(new Color(0x1A2A39));
        field.setForeground(CustomerUI.TEXT_LIGHT);
        field.setFont(CustomerUI.plain(12));
        field.setCaretColor(CustomerUI.TEXT_WHITE);

        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createLabeledCombo(String labelText, String[] options) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(CustomerUI.plain(12));
        label.setForeground(CustomerUI.TEXT_LIGHT);

        JComboBox<String> combo = new JComboBox<>(options);
        combo.setBackground(new Color(0x16212A));
        combo.setForeground(CustomerUI.TEXT_LIGHT);
        combo.setFont(CustomerUI.plain(12));
        combo.setBorder(BorderFactory.createLineBorder(new Color(0x2D3F4F)));

        panel.add(label, BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);
        return panel;
    }

    private JToggleButton createSeatButton(String label, boolean sold, boolean vip) {
        JToggleButton seat = new JToggleButton(label);
        seat.setFont(CustomerUI.plain(10));
        seat.setForeground(CustomerUI.TEXT_LIGHT);
        seat.setOpaque(true);
        seat.setFocusPainted(false);
        seat.setBorder(BorderFactory.createLineBorder(new Color(0x374B5C)));
        seat.setBackground(vip ? new Color(0x3B3B80) : new Color(0x263343));
        seat.setPreferredSize(new Dimension(34, 34));

        if (sold) {
            seat.setEnabled(false);
            seat.setBackground(new Color(0x233442));
            seat.setForeground(new Color(0x6D8DA6));
        } else {
            seat.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (seat.isSelected()) {
                        seat.setBackground(CustomerUI.TEAL);
                        seat.setForeground(Color.WHITE);
                    } else {
                        seat.setBackground(vip ? new Color(0x3B3B80) : new Color(0x263343));
                        seat.setForeground(CustomerUI.TEXT_LIGHT);
                    }
                }
            });
        }

        return seat;
    }

    private JPanel createDarkCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x192330));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(255, 255, 255, 18));
                g2.setStroke(new java.awt.BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        return card;
    }
}
