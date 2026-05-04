package DAO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import config.DatabaseConnection;

public class ThongKeDAO {

    // ─────────────────────────────────────────────────────────────
    // 1. Thống kê vé hôm nay
    // ─────────────────────────────────────────────────────────────
    public int getTongVeHomNay() {
        String sql = "SELECT COUNT(*) FROM Ve WHERE CAST(ngayBan AS DATE) = CAST(GETDATE() AS DATE) AND trangThai = N'Đã bán'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────────
    // 2. Thống kê vé hôm qua
    // ─────────────────────────────────────────────────────────────
    public int getTongVeHomQua() {
        String sql = "SELECT COUNT(*) FROM Ve WHERE CAST(ngayBan AS DATE) = CAST(DATEADD(day, -1, GETDATE()) AS DATE) AND trangThai = N'Đã bán'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────────
    // 3. Doanh thu tháng này
    // ─────────────────────────────────────────────────────────────
    public double getDoanhThuThangNay() {
        String sql = "SELECT SUM(giaVe) FROM Ve WHERE MONTH(ngayBan) = MONTH(GETDATE()) AND YEAR(ngayBan) = YEAR(GETDATE()) AND trangThai = N'Đã bán'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────────
    // 4. Doanh thu 7 ngày gần nhất
    // ─────────────────────────────────────────────────────────────
    public List<Double> getDoanhThu7Ngay() {
        List<Double> result = new ArrayList<>();
        String sql = "SELECT " +
                "ISNULL(SUM(v.giaVe), 0) as doanhThu " +
                "FROM (SELECT DATEADD(day, -n, CAST(GETDATE() AS DATE)) as ngay " +
                "      FROM (VALUES (0),(1),(2),(3),(4),(5),(6)) as nums(n)) dates " +
                "LEFT JOIN Ve v ON CAST(v.ngayBan AS DATE) = dates.ngay AND v.trangThai = N'Đã bán' " +
                "GROUP BY dates.ngay " +
                "ORDER BY dates.ngay ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getDouble("doanhThu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // 5. Tên các ngày trong tuần (7 ngày gần nhất)
    // ─────────────────────────────────────────────────────────────
    public List<String> getNgayTrongTuan() {
        List<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        Calendar cal = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            Calendar day = Calendar.getInstance();
            day.add(Calendar.DAY_OF_MONTH, -i);
            result.add(sdf.format(day.getTime()));
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // 6. Doanh thu theo thể loại phim (tháng này)
    // ─────────────────────────────────────────────────────────────
    public List<Object[]> getDoanhThuTheoTheLoai() {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT " +
                "p.theLoai, " +
                "ISNULL(SUM(v.giaVe), 0) as doanhThu " +
                "FROM Phim p " +
                "LEFT JOIN SuatChieu sc ON p.maPhim = sc.maPhim " +
                "LEFT JOIN Ve v ON sc.maSuat = v.maSuat " +
                "   AND v.trangThai = N'Đã bán' " +
                "   AND MONTH(v.ngayBan) = MONTH(GETDATE()) " +
                "   AND YEAR(v.ngayBan) = YEAR(GETDATE()) " +
                "WHERE p.trangThai = N'Đang chiếu' " +
                "GROUP BY p.theLoai " +
                "HAVING ISNULL(SUM(v.giaVe), 0) > 0 " +
                "ORDER BY doanhThu DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[2];
                row[0] = rs.getString("theLoai");
                row[1] = rs.getDouble("doanhThu");
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // 7. Hoạt động bán vé gần đây
    // ─────────────────────────────────────────────────────────────
    public List<Object[]> getHoatDongGanDay(int limit) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT TOP (?) " +
                "   'KH_' + CAST(v.maVe AS VARCHAR) as khachHang, " +
                "   p.tenPhim, " +
                "   v.giaVe, " +
                "   v.ngayBan " +
                "FROM Ve v " +
                "JOIN SuatChieu sc ON v.maSuat = sc.maSuat " +
                "JOIN Phim p ON sc.maPhim = p.maPhim " +
                "WHERE v.trangThai = N'Đã bán' " +
                "ORDER BY v.ngayBan DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("khachHang");
                row[1] = rs.getString("tenPhim");
                row[2] = rs.getDouble("giaVe");
                row[3] = rs.getTimestamp("ngayBan");
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // 8. Doanh thu theo ngày (cho biểu đồ chi tiết)
    // ─────────────────────────────────────────────────────────────
    public double getDoanhThuTheoNgay(int nam, int thang, int ngay) {
        String sql = "SELECT ISNULL(SUM(giaVe), 0) FROM Ve " +
                "WHERE YEAR(ngayBan) = ? AND MONTH(ngayBan) = ? AND DAY(ngayBan) = ? " +
                "AND trangThai = N'Đã bán'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nam);
            stmt.setInt(2, thang);
            stmt.setInt(3, ngay);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────────
    // 9. Doanh thu theo tháng trong năm
    // ─────────────────────────────────────────────────────────────
    public List<Double> getDoanhThuTheoThang(int nam) {
        List<Double> result = new ArrayList<>();
        String sql = "SELECT MONTH(ngayBan) as thang, ISNULL(SUM(giaVe), 0) as doanhThu " +
                "FROM Ve " +
                "WHERE YEAR(ngayBan) = ? AND trangThai = N'Đã bán' " +
                "GROUP BY MONTH(ngayBan) " +
                "ORDER BY thang";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nam);
            ResultSet rs = stmt.executeQuery();
            double[] monthly = new double[12];
            while (rs.next()) {
                int thang = rs.getInt("thang");
                monthly[thang - 1] = rs.getDouble("doanhThu");
            }
            for (double d : monthly) {
                result.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────
    // 10. Tổng số vé theo phim (cho thống kê top phim)
    // ─────────────────────────────────────────────────────────────
    public int getTongVeTheoPhim(int maPhim) {
        String sql = "SELECT COUNT(*) FROM Ve v " +
                "JOIN SuatChieu sc ON v.maSuat = sc.maSuat " +
                "WHERE sc.maPhim = ? AND v.trangThai = N'Đã bán'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maPhim);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}