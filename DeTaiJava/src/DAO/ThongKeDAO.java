package DAO;

import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {
    public List<Double> getDoanhThu7Ngay() {
        List<Double> result = new ArrayList<>();
        String sql = "SELECT COALESCE(SUM(tongTien), 0) FROM HoaDon " +
                "WHERE ngayLap >= DATEADD(day, -6, CAST(GETDATE() AS DATE)) " +
                "GROUP BY CAST(ngayLap AS DATE) ORDER BY CAST(ngayLap AS DATE)";
        // Implement SQL query
        return result;
    }

    public List<String> getNgayTrongTuan() {
        List<String> result = new ArrayList<>();
        // Trả về tên các ngày trong tuần
        return result;
    }

    public List<Object[]> getHoatDongGanDay(int limit) {
        List<Object[]> result = new ArrayList<>();
        // Lấy từ bảng log hoặc từ hóa đơn, lịch sử đăng nhập
        return result;
    }

}
