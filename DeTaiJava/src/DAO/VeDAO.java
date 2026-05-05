package DAO;

import config.DatabaseConnection;
import model.BookingState;
import model.CinemaData;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VeDAO {

    // ─── 1. Lưu vé ────────────────────────────────────────────────────────────
    public static boolean saveVe(BookingState state) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            int maSuat = resolveMaSuat(conn, state);
            if (maSuat < 0) {
                System.err.println("[VeDAO] Khong tim duoc maSuat: phimIdx="
                        + state.phimIdx + " phongIdx=" + state.phongIdx
                        + " suatIdx=" + state.suatIdx);
                return false;
            }

            int maPhong = resolveMapPhong(conn, maSuat);
            if (maPhong < 0) return false;

            String sql = "INSERT INTO Ve (maSuat, maGhe, maNV, giaVe, trangThai) "
                       + "VALUES (?, ?, ?, ?, N'\u0110\u00e3 b\u00e1n')";
            PreparedStatement ps = conn.prepareStatement(sql);

            for (int i = 0; i < state.seats.size(); i++) {
                String label  = state.seats.get(i);
                boolean isVip = state.seatsVip.get(i);

                int maGhe = resolveGhe(conn, maPhong, label);
                if (maGhe < 0) {
                    System.err.println("[VeDAO] Khong tim duoc maGhe: " + label + " phong=" + maPhong);
                    continue;
                }

                int giaVe = isVip ? CinemaData.GIA_VIP : CinemaData.GIA_THUONG;
                ps.setInt(1, maSuat);
                ps.setInt(2, maGhe);
                ps.setInt(3, 2);
                ps.setInt(4, giaVe);
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            ps.close();
            System.out.println("[VeDAO] Da luu " + results.length + " ve thanh cong.");
            return true;

        } catch (SQLException e) {
            System.err.println("[VeDAO] Loi luu ve: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ─── 2. Lấy ghế đã bán ───────────────────────────────────────────────────
    public static Set<String> getSoldSeats(int phimIdx, int phongIdx, int suatIdx) {
        Set<String> sold = new HashSet<>();
        try {
            Connection conn = DatabaseConnection.getConnection();

            BookingState tmp = new BookingState();
            tmp.phimIdx  = phimIdx;
            tmp.phongIdx = phongIdx;
            tmp.suatIdx  = suatIdx;

            int maSuat = resolveMaSuat(conn, tmp);
            if (maSuat < 0) return sold;

            String sql = "SELECT g.hangGhe, g.soGhe "
                       + "FROM Ve v JOIN Ghe g ON v.maGhe = g.maGhe "
                       + "WHERE v.maSuat = ? AND v.trangThai = N'\u0110\u00e3 b\u00e1n'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSuat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sold.add(rs.getString("hangGhe").trim() + rs.getInt("soGhe"));
            }
            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("[VeDAO] Loi getSoldSeats: " + e.getMessage());
        }
        return sold;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Tìm maSuat dựa vào maPhim + maPhong (số) + thứ tự suất.
     *
     * Dùng maPhong thực từ DB (tách số từ tên hiển thị "Phòng X  (...)"),
     * KHÔNG dùng tên phòng để tránh vấn đề encoding tiếng Việt.
     */
    private static int resolveMaSuat(Connection conn, BookingState state)
            throws SQLException {

        // maPhim = phimIdx + 1 (IDENTITY DB bắt đầu từ 1)
        int maPhim = state.phimIdx + 1;

        // Lấy số phòng từ chuỗi hiển thị, ví dụ "Phòng 3  (2D)" -> maPhong = 3
        String displayPhong = CinemaData.PHONG_BY_PHIM[state.phimIdx][state.phongIdx];
        int maPhong = extractMaPhong(displayPhong);

        System.out.println("[VeDAO] Tim: maPhim=" + maPhim
                + " maPhong=" + maPhong
                + " suatIdx=" + state.suatIdx
                + " (display='" + displayPhong + "')");

        // Query theo maPhim + maPhong (số nguyên, không phụ thuộc encoding)
        String sql = "SELECT sc.maSuat "
                   + "FROM SuatChieu sc "
                   + "WHERE sc.maPhim = ? AND sc.maPhong = ? "
                   + "ORDER BY sc.ngayChieu, sc.gioChieu";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, maPhim);
        ps.setInt(2, maPhong);
        ResultSet rs = ps.executeQuery();

        int idx = 0, result = -1;
        while (rs.next()) {
            System.out.println("[VeDAO]   -> suat #" + idx + " maSuat=" + rs.getInt("maSuat"));
            if (idx == state.suatIdx) {
                result = rs.getInt("maSuat");
                break;
            }
            idx++;
        }
        rs.close();
        ps.close();

        if (result < 0) {
            System.err.println("[VeDAO] KHONG TIM DUOC! maPhim=" + maPhim
                    + " maPhong=" + maPhong + " suatIdx=" + state.suatIdx
                    + " | Tong so suat tim duoc: " + idx);
        }
        return result;
    }

    /**
     * Tách maPhong (số) từ chuỗi hiển thị.
     * "Phòng 3  (2D)"   -> 3
     * "Phòng 5  (IMAX)" -> 5
     * "Phòng 12  (3D)"  -> 12
     */
    private static int extractMaPhong(String display) {
        // Lấy số đứng trước dấu "("
        Pattern p = Pattern.compile("(\\d+)\\s*\\(");
        Matcher m = p.matcher(display);
        if (m.find()) return Integer.parseInt(m.group(1));

        // Fallback: lấy số đầu tiên
        Pattern p2 = Pattern.compile("\\d+");
        Matcher m2 = p2.matcher(display);
        return m2.find() ? Integer.parseInt(m2.group()) : 0;
    }

    private static int resolveMapPhong(Connection conn, int maSuat)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT maPhong FROM SuatChieu WHERE maSuat = ?");
        ps.setInt(1, maSuat);
        ResultSet rs = ps.executeQuery();
        int result = rs.next() ? rs.getInt("maPhong") : -1;
        rs.close(); ps.close();
        return result;
    }

    private static int resolveGhe(Connection conn, int maPhong, String label)
            throws SQLException {
        if (label == null || label.length() < 2) return -1;
        char hang = label.charAt(0);
        int  so;
        try { so = Integer.parseInt(label.substring(1)); }
        catch (NumberFormatException e) { return -1; }

        PreparedStatement ps = conn.prepareStatement(
                "SELECT maGhe FROM Ghe WHERE maPhong=? AND hangGhe=? AND soGhe=?");
        ps.setInt(1, maPhong);
        ps.setString(2, String.valueOf(hang));
        ps.setInt(3, so);
        ResultSet rs = ps.executeQuery();
        int result = rs.next() ? rs.getInt("maGhe") : -1;
        rs.close(); ps.close();
        return result;
    }
}