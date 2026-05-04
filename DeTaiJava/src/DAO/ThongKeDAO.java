package DAO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import config.DatabaseConnection;

public class ThongKeDAO {
    private Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    /**
     * Doanh thu 7 ngày gần nhất (mỗi phần tử = 1 ngày)
     * Trả về đủ 7 phần tử, ngày nào không có doanh thu thì = 0.0
     */
    public List<Double> getDoanhThu7Ngay() {
        List<Double> result = new ArrayList<>();
        // Khởi tạo 7 ngày = 0
        for (int i = 0; i < 7; i++)
            result.add(0.0);

        String sql = "SELECT CAST(ngayLap AS DATE) as ngay, COALESCE(SUM(tongTien), 0) as doanhThu " +
                "FROM HoaDon " +
                "WHERE ngayLap >= DATEADD(day, -6, CAST(GETDATE() AS DATE)) " +
                "GROUP BY CAST(ngayLap AS DATE) " +
                "ORDER BY CAST(ngayLap AS DATE)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            // Map dữ liệu vào đúng vị trí (0 = 6 ngày trước, 6 = hôm nay)
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            while (rs.next()) {
                java.sql.Date ngay = rs.getDate("ngay");
                double dt = rs.getDouble("doanhThu");
                Calendar c = Calendar.getInstance();
                c.setTime(ngay);
                // Tính khoảng cách ngày so với hôm nay
                long diff = (today.getTimeInMillis() - c.getTimeInMillis()) / (1000 * 60 * 60 * 24);
                int idx = (int) (6 - diff);
                if (idx >= 0 && idx < 7)
                    result.set(idx, dt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Tên 7 ngày trong tuần (ví dụ: T2, T3, ... CN) tương ứng với 7 ngày gần nhất
     */
    public List<String> getNgayTrongTuan() {
        List<String> result = new ArrayList<>();
        String[] tenNgay = { "CN", "T2", "T3", "T4", "T5", "T6", "T7" };
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            int dow = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0=CN, 1=T2,...
            result.add(tenNgay[dow] + "\n" + sdf.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    /**
     * Tổng doanh thu tháng này
     */
    public double getDoanhThuThangNay() {
        String sql = "SELECT COALESCE(SUM(tongTien), 0) FROM HoaDon " +
                "WHERE MONTH(ngayLap) = MONTH(GETDATE()) AND YEAR(ngayLap) = YEAR(GETDATE())";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Tổng vé bán hôm nay
     */
    public int getTongVeHomNay() {
        String sql = "SELECT COUNT(*) FROM Ve v " +
                "JOIN SuatChieu sc ON v.maSC = sc.maSC " +
                "WHERE CAST(sc.ngayChieu AS DATE) = CAST(GETDATE() AS DATE)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Tổng vé hôm qua (để so sánh %)
     */
    public int getTongVeHomQua() {
        String sql = "SELECT COUNT(*) FROM Ve v " +
                "JOIN SuatChieu sc ON v.maSC = sc.maSC " +
                "WHERE CAST(sc.ngayChieu AS DATE) = CAST(DATEADD(day,-1,GETDATE()) AS DATE)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Hoạt động gần đây: [tenKhach, tenPhim, tongTien, ngayLap]
     */
    public List<Object[]> getHoatDongGanDay(int limit) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " " +
                "hd.tenKhach, p.tenPhim, hd.tongTien, hd.ngayLap " +
                "FROM HoaDon hd " +
                "JOIN ChiTietHoaDon ct ON hd.maHD = ct.maHD " +
                "JOIN Ve v ON ct.maVe = v.maVe " +
                "JOIN SuatChieu sc ON v.maSC = sc.maSC " +
                "JOIN Phim p ON sc.maPhim = p.maPhim " +
                "ORDER BY hd.ngayLap DESC";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[] {
                        rs.getString("tenKhach"),
                        rs.getString("tenPhim"),
                        rs.getDouble("tongTien"),
                        rs.getTimestamp("ngayLap")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Thống kê doanh thu theo thể loại phim
     * Trả về: [theLoai, tongDoanhThu]
     */
    public List<Object[]> getDoanhThuTheoTheLoai() {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT p.theLoai, COALESCE(SUM(hd.tongTien), 0) as tongDT " +
                "FROM Phim p " +
                "JOIN SuatChieu sc ON p.maPhim = sc.maPhim " +
                "JOIN Ve v ON sc.maSC = v.maSC " +
                "JOIN ChiTietHoaDon ct ON v.maVe = ct.maVe " +
                "JOIN HoaDon hd ON ct.maHD = hd.maHD " +
                "WHERE MONTH(hd.ngayLap) = MONTH(GETDATE()) " +
                "GROUP BY p.theLoai ORDER BY tongDT DESC";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[] {
                        rs.getString("theLoai"),
                        rs.getDouble("tongDT")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}