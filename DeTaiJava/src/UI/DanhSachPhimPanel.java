package UI;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import service.PhimService;

public class DanhSachPhimPanel extends JPanel {
    private final PhimService service = new PhimService();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> comboFilter;
    private TableRowSorter<DefaultTableModel> sorter;

    public DanhSachPhimPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = BanVeHelper.darkCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout(20, 10));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("DANH SÁCH PHIM");
        title.setFont(customUI.CustomUI.bold(20));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // Search & Filter Panel
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        txtSearch = BanVeHelper.placeholderField("Tìm tên phim...");
        txtSearch.setPreferredSize(new Dimension(180, 38));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        comboFilter = BanVeHelper.styledCombo(new String[]{"Tất cả trạng thái", "Đang chiếu", "Sắp chiếu", "Ngừng chiếu"});
        comboFilter.setPreferredSize(new Dimension(180, 38));
        comboFilter.setForeground(Color.WHITE);
        comboFilter.setFont(customUI.CustomUI.plain(13));
        comboFilter.setBorder(BorderFactory.createLineBorder(new Color(0x3A4C5E), 1, true));
        comboFilter.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▼");
                btn.setForeground(Color.WHITE);
                btn.setBackground(new Color(0x16212A));
                btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(false);
                return btn;
            }
        });
        comboFilter.addActionListener(e -> filter());

        JButton btnReload = customUI.CustomUI.createPrimaryButton("Làm mới");
        btnReload.setPreferredSize(new Dimension(100, 38));
        btnReload.addActionListener(e -> {
            txtSearch.setText("Tìm tên phim...");
            comboFilter.setSelectedIndex(0);
            loadData();
        });

        JLabel lblSearch = new JLabel("Tìm:");
        lblSearch.setForeground(new Color(0x90CAF9));
        lblSearch.setFont(customUI.CustomUI.bold(13));
        
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setForeground(new Color(0x90CAF9));
        lblStatus.setFont(customUI.CustomUI.bold(13));
        
        actions.add(lblSearch);
        actions.add(txtSearch);
        actions.add(Box.createHorizontalStrut(10));
        actions.add(lblStatus);
        actions.add(comboFilter);
        actions.add(Box.createHorizontalStrut(10));
        actions.add(btnReload);

        header.add(actions, BorderLayout.CENTER);
        card.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã", "Tên Phim", "Thể Loại", "Thời Lượng", "Ngày KC", "Trạng Thái", "Thao Tác"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only button column is "editable"
            }
        };

        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        styleTable(table);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
        loadData();
    }

    private void styleTable(JTable t) {
        t.setBackground(BanVeHelper.BG_CARD);
        t.setForeground(Color.WHITE);
        t.setRowHeight(40);
        t.setFont(customUI.CustomUI.plain(13));
        t.getTableHeader().setBackground(new Color(0x243447));
        t.getTableHeader().setForeground(customUI.CustomUI.PRIMARY);
        t.getTableHeader().setFont(customUI.CustomUI.bold(13));
        t.setSelectionBackground(new Color(43, 200, 163, 40));
        t.setSelectionForeground(customUI.CustomUI.PRIMARY);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));

        // Column widths
        t.getColumnModel().getColumn(0).setPreferredWidth(50);   // Mã
        t.getColumnModel().getColumn(1).setPreferredWidth(280);  // Tên Phim
        t.getColumnModel().getColumn(2).setPreferredWidth(220);  // Thể Loại
        t.getColumnModel().getColumn(3).setPreferredWidth(100);  // Thời Lượng
        t.getColumnModel().getColumn(4).setPreferredWidth(120);  // Ngày KC
        t.getColumnModel().getColumn(5).setPreferredWidth(120);  // Trạng Thái
        t.getColumnModel().getColumn(6).setPreferredWidth(80);   // Thao Tác

        // Center align data cho các cột ngắn, Left align cho chữ dài
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); // Lề trái 15px cho thoáng

        for (int i = 0; i < t.getColumnCount() - 1; i++) {
            if (i == 1 || i == 2) {
                t.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            } else {
                t.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Action column with buttons
        t.getColumnModel().getColumn(6).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                p.setOpaque(false);
                JButton btnDel = new JButton("🗑");
                btnDel.setForeground(Color.RED);
                btnDel.setOpaque(false);
                btnDel.setContentAreaFilled(false);
                btnDel.setBorderPainted(false);
                p.add(btnDel);
                return p;
            }
        });
        
        t.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = t.rowAtPoint(e.getPoint());
                int col = t.columnAtPoint(e.getPoint());
                if (col == 6) {
                    int maPhim = (int) model.getValueAt(row, 0);
                    handleDelete(maPhim);
                }
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        List<Object[]> data = service.getAllPhim();
        for (Object[] row : data) {
            model.addRow(new Object[]{
                row[0], row[1], row[2], row[3], row[4], row[6], "Xóa"
            });
        }
    }

    private void filter() {
        String text = txtSearch.getText();
        if (text.equals("Tìm tên phim...")) text = "";
        
        String status = (String) comboFilter.getSelectedItem();
        
        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();
        
        // Lọc theo tên (cột 1)
        if (!text.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + text, 1));
        }
        
        // Lọc theo trạng thái (cột 5)
        if (!status.equals("Tất cả trạng thái")) {
            filters.add(RowFilter.regexFilter("^" + status + "$", 5));
        }
        
        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void handleDelete(int ma) {
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa phim mã " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (service.deletePhim(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        }
    }
}