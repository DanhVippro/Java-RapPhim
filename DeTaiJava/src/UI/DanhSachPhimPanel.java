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

    public DanhSachPhimPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = BanVeHelper.darkCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("DANH SÁCH PHIM");
        title.setFont(customUI.CustomUI.bold(20));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton btnReload = BanVeHelper.ghostBtn("Làm mới 🔄");
        btnReload.addActionListener(e -> loadData());
        header.add(btnReload, BorderLayout.EAST);
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
        t.getColumnModel().getColumn(0).setPreferredWidth(50);
        t.getColumnModel().getColumn(1).setPreferredWidth(250);
        t.getColumnModel().getColumn(6).setPreferredWidth(100);

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