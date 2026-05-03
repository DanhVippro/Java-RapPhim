package DAO;

public class HoaDonDAO {
    public int getTongVeHomNay() {
        String sql = "SELECT COUNT(*) FROM ChiTietHoaDon ct " +
                "JOIN HoaDon h ON ct.maHD = h.maHD " +
                "WHERE CAST(h.ngayLap AS DATE) = CAST(GETDATE() AS DATE)";
        return 0;
    }

    public double getDoanhThuHomNay() {
        String sql = "SELECT COALESCE(SUM(tongTien), 0) FROM HoaDon " +
                "WHERE CAST(ngayLap AS DATE) = CAST(GETDATE() AS DATE)";
        return 0.0;
    }

}
