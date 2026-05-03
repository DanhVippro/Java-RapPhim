package DAO;

import java.util.ArrayList;
import java.util.List;

public class PhimDAO {
    public int getSoPhimDangChieu() {
        String sql = "SELECT COUNT(*) FROM Phim WHERE trangThai = N'Đang chiếu'";
        return 0;
    }

    public List<Object[]> getTopPhimBanChay(int limit) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " p.tenPhim, COUNT(ct.maVe) as soVe " +
                "FROM Phim p JOIN SuatChieu sc ON p.maPhim = sc.maPhim " +
                "JOIN Ve v ON sc.maSC = v.maSC " +
                "JOIN ChiTietHoaDon ct ON v.maVe = ct.maVe " +
                "GROUP BY p.tenPhim ORDER BY soVe DESC";
        return result;
    }

}
