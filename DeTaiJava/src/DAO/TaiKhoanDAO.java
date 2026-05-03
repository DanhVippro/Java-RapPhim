package DAO;

import config.DatabaseConnection;
import entity.TaiKhoan;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý TaiKhoan: đăng nhập, thêm/sửa/xoá.
 */
public class TaiKhoanDAO {

    // ── Mã hoá mật khẩu SHA-256 ──────────────────────────────────────────────
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 không khả dụng", e);
        }
    }

    // ── ĐĂNG NHẬP ─────────────────────────────────────────────────────────────
    /**
     * Kiểm tra thông tin đăng nhập.
     * 
     * @return TaiKhoan nếu đúng, null nếu sai hoặc tài khoản bị khoá.
     */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhauRaw) {
        String sql = "SELECT maTK, tenDangNhap, vaiTro, hoTen, email, soDT, trangThai "
                + "FROM TaiKhoan "
                + "WHERE tenDangNhap = ? AND matKhau = ? AND trangThai = 1";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            ps.setString(2, sha256(matKhauRaw));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── LẤY TẤT CẢ ──────────────────────────────────────────────────────────
    public List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT maTK, tenDangNhap, vaiTro, hoTen, email, soDT, trangThai "
                + "FROM TaiKhoan ORDER BY maTK";
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── THÊM ─────────────────────────────────────────────────────────────────
    public boolean them(TaiKhoan tk, String matKhauRaw) {
        String sql = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro, hoTen, email, soDT) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, sha256(matKhauRaw));
            ps.setString(3, tk.getVaiTro());
            ps.setString(4, tk.getHoTen());
            ps.setString(5, tk.getEmail());
            ps.setString(6, tk.getSoDT());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── ĐỔI MẬT KHẨU ────────────────────────────────────────────────────────
    public boolean doiMatKhau(int maTK, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET matKhau = ? WHERE maTK = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sha256(matKhauMoi));
            ps.setInt(2, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── KHOÁ / MỞ TÀI KHOẢN ─────────────────────────────────────────────────
    public boolean setTrangThai(int maTK, boolean trangThai) {
        String sql = "UPDATE TaiKhoan SET trangThai = ? WHERE maTK = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);
            ps.setInt(2, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── HELPER ───────────────────────────────────────────────────────────────
    private TaiKhoan mapRow(ResultSet rs) throws SQLException {
        return new TaiKhoan(
                rs.getInt("maTK"),
                rs.getString("tenDangNhap"),
                rs.getString("vaiTro"),
                rs.getString("hoTen"),
                rs.getString("email"),
                rs.getString("soDT"),
                rs.getBoolean("trangThai"));
    }

    public int themVaTraVeMa(TaiKhoan tk, String matKhauRaw) {
        String sql = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro, hoTen, email, soDT) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, sha256(matKhauRaw));
            ps.setString(3, tk.getVaiTro());
            ps.setString(4, tk.getHoTen());
            ps.setString(5, tk.getEmail());
            ps.setString(6, tk.getSoDT());

            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
