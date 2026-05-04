package DAO;

import config.DatabaseConnection;
import model.BookingState;
import model.CinemaData;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * VeDAO – thao tác với bảng Ve trong MegadeCinema.
 *
 *  Hai việc chính:
 *  1. saveVe()          : lưu từng ghế đã chọn thành 1 bản ghi Ve
 *  2. getSoldSeats()    : trả về set nhãn ghế đã bán theo (maPhim, phongIdx, suatIdx)
 */
public class VeDAO {

    // ─── 1. Lưu vé ────────────────────────────────────────────────────────────
    /**
     * Lưu tất cả ghế trong BookingState thành các bản ghi Ve.
     *
     * Logic map:
     *   • phimIdx  → maPhim  (index + 1, khớp IDENTITY bảng Phim)
     *   • phongIdx → maPhong (lấy qua PHONG_ROOM_KEY rồi tìm SuatChieu)
     *   • suatIdx  → maSuat  (tìm theo maPhim + maPhong + thứ tự suất)
     *   • ghế      → maGhe   (tìm theo maPhong + hangGhe + soGhe)
     *   • maNV     = 2 (mặc định thu ngân, thay bằng ID nhân viên đang login nếu cần)
     *
     * @return true nếu tất cả bản ghi được lưu thành công
     */
    public static boolean saveVe(BookingState state) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // ── Xác định maSuat ──────────────────────────────────────────────
            int maSuat = resolveMaSuat(conn, state);
            if (maSuat < 0) {
                System.err.println("[VeDAO] Không tìm được maSuat cho phimIdx="
                        + state.phimIdx + " phongIdx=" + state.phongIdx
                        + " suatIdx=" + state.suatIdx);
                return false;
            }

            // ── Xác định maPhong ─────────────────────────────────────────────
            int maPhong = resolveMapPhong(conn, maSuat);
            if (maPhong < 0) return false;

            // ── Insert từng ghế ──────────────────────────────────────────────
            String sql = "INSERT INTO Ve (maSuat, maGhe, maNV, giaVe, trangThai) "
                       + "VALUES (?, ?, ?, ?, N'Đã bán')";

            PreparedStatement ps = conn.prepareStatement(sql);

            for (int i = 0; i < state.seats.size(); i++) {
                String label  = state.seats.get(i);           // e.g. "C5"
                boolean isVip = state.seatsVip.get(i);

                int maGhe = resolveGhe(conn, maPhong, label);
                if (maGhe < 0) {
                    System.err.println("[VeDAO] Không tìm được maGhe: " + label
                            + " trong phòng " + maPhong);
                    continue;
                }

                int giaVe = isVip ? CinemaData.GIA_VIP : CinemaData.GIA_THUONG;

                ps.setInt(1, maSuat);
                ps.setInt(2, maGhe);
                ps.setInt(3, 2);       // maNV mặc định = 2 (Thu ngân)
                ps.setInt(4, giaVe);
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            ps.close();

            System.out.println("[VeDAO] Đã lưu " + results.length + " vé.");
            return true;

        } catch (SQLException e) {
            System.err.println("[VeDAO] Lỗi lưu vé: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ─── 2. Lấy danh sách ghế đã bán ─────────────────────────────────────────
    /**
     * Trả về Set nhãn ghế đã bán (vd: "A3", "B7") theo phimIdx + phongIdx + suatIdx.
     * SeatMapPanel gọi hàm này để tô màu ghế đã bán thực từ DB.
     */
    public static Set<String> getSoldSeats(int phimIdx, int phongIdx, int suatIdx) {
        Set<String> sold = new HashSet<>();
        try {
            Connection conn = DatabaseConnection.getConnection();

            // Tạo BookingState giả để dùng resolveMaSuat
            model.BookingState tmp = new model.BookingState();
            tmp.phimIdx  = phimIdx;
            tmp.phongIdx = phongIdx;
            tmp.suatIdx  = suatIdx;

            int maSuat = resolveMaSuat(conn, tmp);
            if (maSuat < 0) return sold;

            int maPhong = resolveMapPhong(conn, maSuat);
            if (maPhong < 0) return sold;

            // Query ghế đã bán (chưa huỷ)
            String sql = "SELECT g.hangGhe, g.soGhe "
                       + "FROM Ve v JOIN Ghe g ON v.maGhe = g.maGhe "
                       + "WHERE v.maSuat = ? AND v.trangThai = N'Đã bán'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSuat);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String label = rs.getString("hangGhe").trim()
                             + rs.getInt("soGhe");    // e.g. "C5"
                sold.add(label);
            }
            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("[VeDAO] Lỗi lấy sold seats: " + e.getMessage());
        }
        return sold;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Tìm maSuat: match theo maPhim (phimIdx+1) + maPhong + thứ tự suất (suatIdx).
     * Nếu DB có nhiều suất cho phim + phòng đó, lấy theo thứ tự gioChieu ASC.
     */
    private static int resolveMaSuat(Connection conn, BookingState state)
            throws SQLException {

        int maPhim  = state.phimIdx + 1;   // IDENTITY bắt đầu từ 1

        // Tìm maPhong theo tên phòng hiển thị, vd "Phòng chiếu 3  (2D)"
        String tenPhong = extractTenPhong(
                CinemaData.PHONG_BY_PHIM[state.phimIdx][state.phongIdx]);

        // Lấy danh sách suất của phim + phòng, sắp theo giờ tăng dần
        String sql = "SELECT sc.maSuat "
                   + "FROM SuatChieu sc "
                   + "JOIN PhongChieu pc ON sc.maPhong = pc.maPhong "
                   + "WHERE sc.maPhim = ? AND pc.tenPhong = ? "
                   + "ORDER BY sc.ngayChieu, sc.gioChieu";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, maPhim);
        ps.setString(2, tenPhong);
        ResultSet rs = ps.executeQuery();

        int idx = 0, result = -1;
        while (rs.next()) {
            if (idx == state.suatIdx) {
                result = rs.getInt("maSuat");
                break;
            }
            idx++;
        }
        rs.close();
        ps.close();
        return result;
    }

    /** Lấy maPhong từ maSuat */
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

    /**
     * Tìm maGhe theo maPhong + nhãn ghế (vd "C5" → hangGhe='C', soGhe=5).
     */
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

    /**
     * Tách tên phòng từ chuỗi hiển thị.
     * "Phòng 1  (2D)"  →  "Phòng chiếu 1"
     * "Phòng 5  (IMAX)"→  "Phòng chiếu 5"
     */
    private static String extractTenPhong(String display) {
        // display dạng: "Phòng 1  (2D)"
        String num = display.replaceAll("[^0-9]", "").trim();
        return "Phòng chiếu " + num;
    }
}