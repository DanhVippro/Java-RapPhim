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
                "FROM [Phim] " +
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
        String sql = "SELECT TOP " + limit + " p.tenPhim, COUNT(v.maVe) as soVe " +
                "FROM Phim p JOIN SuatChieu sc ON p.maPhim = sc.maPhim " +
                "JOIN Ve v ON sc.maSuat = v.maSuat " +
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

    public List<Object[]> getAllPhim() {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT [maPhim],[tenPhim],[theLoai],[thoiLuong]," +
                "[ngayKhoiChieu],[moTa],[trangThai],[poster_path] " +
                "FROM [Phim] ORDER BY maPhim DESC";
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

    public boolean addPhim(String ten, String theLoai, int thoiLuong, Date ngayKC, String moTa, String trangThai, String poster) {
        String sql = "INSERT INTO Phim (tenPhim, theLoai, thoiLuong, ngayKhoiChieu, moTa, trangThai, poster_path) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ten);
            ps.setString(2, theLoai);
            ps.setInt(3, thoiLuong);
            ps.setDate(4, ngayKC);
            ps.setString(5, moTa);
            ps.setString(6, trangThai);
            ps.setString(7, poster);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePhim(int ma, String ten, String theLoai, int thoiLuong, Date ngayKC, String moTa, String trangThai, String poster) {
        String sql = "UPDATE Phim SET tenPhim=?, theLoai=?, thoiLuong=?, ngayKhoiChieu=?, moTa=?, trangThai=?, poster_path=? WHERE maPhim=?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ten);
            ps.setString(2, theLoai);
            ps.setInt(3, thoiLuong);
            ps.setDate(4, ngayKC);
            ps.setString(5, moTa);
            ps.setString(6, trangThai);
            ps.setString(7, poster);
            ps.setInt(8, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePhim(int ma) {
        String sql = "DELETE FROM Phim WHERE maPhim=?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}