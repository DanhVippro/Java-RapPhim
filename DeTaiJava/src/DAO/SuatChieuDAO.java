package DAO;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuatChieuDAO {
    private Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    public List<Object[]> getLichChieuHomNay() {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT sc.maSC, p.tenPhim, ph.tenPhong, sc.gioChieu, " +
                "(SELECT COUNT(*) FROM Ve WHERE maSC = sc.maSC AND trangThai = N'Còn trống') as trong, " +
                "ph.sucChua as tong, sc.giaVe " +
                "FROM SuatChieu sc " +
                "JOIN Phim p ON sc.maPhim = p.maPhim " +
                "JOIN Phong ph ON sc.maPhong = ph.maPhong " +
                "WHERE CAST(sc.ngayChieu AS DATE) = CAST(GETDATE() AS DATE) " +
                "ORDER BY sc.gioChieu";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getInt("maSC"),
                    rs.getString("tenPhim"),
                    rs.getString("tenPhong"),
                    rs.getTime("gioChieu").toString().substring(0, 5), // Lấy HH:mm
                    rs.getInt("trong"),
                    rs.getInt("tong"),
                    rs.getInt("giaVe")
                });
            }
        } catch (SQLException e) {
            System.err.println("[SuatChieuDAO] Error: " + e.getMessage());
        }
        return result;
    }
}
