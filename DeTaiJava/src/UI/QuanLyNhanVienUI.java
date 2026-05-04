package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import DAO.NhanVienDAO;
import customUI.CustomUI;
import entity.NhanVien;

public class QuanLyNhanVienUI extends JPanel {

    public QuanLyNhanVienUI() {
        setLayout(new BorderLayout());

        Color textColor = Color.WHITE;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CustomUI.SIDEBAR_BG);
        ;

        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(textColor);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        centerPanel.setBackground(CustomUI.SIDEBAR_BG);

        JPanel formPanel = new JPanel(new GridLayout(2, 5, 15, 15));
        formPanel.setBackground(CustomUI.SIDEBAR_BG);
        ;

        JTextField txtMa = new JTextField();
        txtMa.setEditable(false);
        JTextField txtTen = new JTextField();
        JTextField txtSDT = new JTextField();

        JComboBox<String> cbChucVu = new JComboBox<>(
                new String[] { "Quản lý", "Thu ngân", "Nhân viên kỹ thuật" });

        JComboBox<String> cbTrangThai = new JComboBox<>(
                new String[] { "Đang làm", "Nghỉ" });

        JLabel[] labels = {
                new JLabel("Mã NV"),
                new JLabel("Tên NV"),
                new JLabel("SĐT"),
                new JLabel("Chức vụ"),
                new JLabel("Trạng thái")
        };

        for (JLabel lbl : labels) {
            lbl.setForeground(textColor);
            formPanel.add(lbl);
        }

        formPanel.add(txtMa);
        formPanel.add(txtTen);
        formPanel.add(txtSDT);
        formPanel.add(cbChucVu);
        formPanel.add(cbTrangThai);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(CustomUI.SIDEBAR_BG);
        ;

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");

        JTextField txtTim = new JTextField(15);
        JButton btnTim = new JButton("Tìm");

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(txtTim);
        buttonPanel.add(btnTim);

        String[] cols = {
                "Mã NV", "Tên nhân viên", "SĐT", "Chức vụ", "Trạng thái"
        };

        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setBackground(CustomUI.SIDEBAR_BG);
        ;
        table.setForeground(Color.WHITE);

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(CustomUI.SIDEBAR_BG);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách nhân viên"));
        btnThem.addActionListener(e -> {
            if (!validateData(txtTen, txtSDT))
                return;
            String tenChuan = chuanHoaTen(txtTen.getText());
            txtTen.setText(tenChuan);

            NhanVien nv = new NhanVien(
                    txtTen.getText(),
                    txtSDT.getText(),
                    cbChucVu.getSelectedItem().toString(),
                    cbTrangThai.getSelectedItem().toString());

            NhanVienDAO dao = new NhanVienDAO();
            int maNV = dao.insert(nv);

            if (maNV != -1) {
                model.addRow(new Object[] {
                        maNV,
                        nv.getHoTen(),
                        nv.getSoDT(),
                        nv.getChucVu(),
                        nv.getTrangThai()
                });

                txtMa.setText(String.valueOf(maNV));
                JOptionPane.showMessageDialog(null, "Thêm thành công!");
            }

        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMa.setText(model.getValueAt(row, 0).toString());
                txtTen.setText(model.getValueAt(row, 1).toString());
                txtSDT.setText(model.getValueAt(row, 2).toString());
                cbChucVu.setSelectedItem(model.getValueAt(row, 3));
                cbTrangThai.setSelectedItem(model.getValueAt(row, 4));
            }
        });

        btnSua.addActionListener(e -> {
            if (!validateData(txtTen, txtSDT))
                return;
            int row = table.getSelectedRow();

            if (row >= 0) {
                int maNV = Integer.parseInt(txtMa.getText());
                String tenChuan = chuanHoaTen(txtTen.getText());
                txtTen.setText(tenChuan);

                NhanVien nv = new NhanVien(
                        txtTen.getText(),
                        txtSDT.getText(),
                        cbChucVu.getSelectedItem().toString(),
                        cbTrangThai.getSelectedItem().toString());

                NhanVienDAO dao = new NhanVienDAO();

                if (dao.update(maNV, nv)) {
                    model.setValueAt(txtTen.getText(), row, 1);
                    model.setValueAt(txtSDT.getText(), row, 2);
                    model.setValueAt(cbChucVu.getSelectedItem(), row, 3);
                    model.setValueAt(cbTrangThai.getSelectedItem(), row, 4);
                    JOptionPane.showMessageDialog(null, "Cập nhật thành công!");
                }
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row >= 0) {
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Bạn có chắc chắn muốn xóa nhân viên này?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION)
                    return;

                int maNV = Integer.parseInt(model.getValueAt(row, 0).toString());

                NhanVienDAO dao = new NhanVienDAO();

                if (dao.delete(maNV)) {
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(null, "Xóa thành công!");

                }
            }
        });

        btnTim.addActionListener(e -> {
            String keyword = txtTim.getText().toLowerCase();

            for (int i = 0; i < model.getRowCount(); i++) {
                String ma = model.getValueAt(i, 0).toString().toLowerCase();
                String ten = model.getValueAt(i, 1).toString().toLowerCase();

                if (ma.contains(keyword) || ten.contains(keyword)) {
                    table.setRowSelectionInterval(i, i);
                    return;
                }
            }
        });

        NhanVienDAO dao = new NhanVienDAO();
        model.setRowCount(0);
        for (NhanVien nv : dao.getAll()) {
            model.addRow(new Object[] {
                    nv.getMaNV(),
                    nv.getHoTen(),
                    nv.getSoDT(),
                    nv.getChucVu(),
                    nv.getTrangThai()
            });
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(CustomUI.SIDEBAR_BG);
        ;
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

    }

    private boolean validateData(JTextField txtTen, JTextField txtSDT) {
        String ten = txtTen.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tên không được rỗng!");
            txtTen.requestFocus();
            return false;
        }

        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "SĐT không được rỗng!");
            txtSDT.requestFocus();
            return false;
        }

        if (!sdt.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "SĐT chỉ được chứa số!");
            txtSDT.requestFocus();
            return false;
        }

        return true;
    }

    private String chuanHoaTen(String ten) {
        ten = ten.trim().toLowerCase();
        String[] words = ten.split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String w : words) {
            sb.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1))
                    .append(" ");
        }

        return sb.toString().trim();
    }
}
