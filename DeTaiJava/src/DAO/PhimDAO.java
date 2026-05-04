package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;

public class PhimDAO {
    private Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    public int getSoPhimDangChieu() {
        String sql = "SELECT COUNT(*) FROM Phim WHERE trangThai = N'Đang chiếu'";
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
     * Lấy danh sách phim đang chiếu theo schema:
     * maPhim, tenPhim, theLoai, thoiLuong, ngayKhoiChieu, moTa, trangThai,
     * poster_path
     */
    public List<Object[]> getPhimDangChieu() {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT TOP (1000) [maPhim],[tenPhim],[theLoai],[thoiLuong]," +
                "[ngayKhoiChieu],[moTa],[trangThai],[poster_path] " +
                "FROM [MegadeCinema].[dbo].[Phim] " +
                "WHERE trangThai = N'Đang chiếu' " +
                "ORDER BY ngayKhoiChieu DESC";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[] {
                        rs.getInt("maPhim"),
                        rs.getString("tenPhim"),
                        rs.getString("theLoai"),
                        rs.getInt("thoiLuong"),
                        rs.getDate("ngayKhoiChieu"),
                        rs.getString("moTa"),
                        rs.getString("trangThai"),
                        rs.getString("poster_path")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Object[]> getTopPhimBanChay(int limit) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " p.tenPhim, COUNT(ct.maVe) as soVe " +
                "FROM Phim p JOIN SuatChieu sc ON p.maPhim = sc.maPhim " +
                "JOIN Ve v ON sc.maSC = v.maSC " +
                "JOIN ChiTietHoaDon ct ON v.maVe = ct.maVe " +
                "GROUP BY p.tenPhim ORDER BY soVe DESC";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[] {
                        rs.getString("tenPhim"),
                        rs.getInt("soVe")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}