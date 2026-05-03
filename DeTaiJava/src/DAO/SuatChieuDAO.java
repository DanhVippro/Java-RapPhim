package DAO;

import java.util.ArrayList;
import java.util.List;

public class SuatChieuDAO {
    public List<Object[]> getLichChieuHomNay() {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT sc.maSC, p.tenPhim, ph.tenPhong, sc.gioChieu, " +
                "(SELECT COUNT(*) FROM Ve WHERE maSC = sc.maSC AND trangThai = N'Còn trống') as trong, " +
                "ph.sucChua as tong, sc.giaVe " +
                "FROM SuatChieu sc " +
                "JOIN Phim p ON sc.maPhim = p.maPhim " +
                "JOIN Phong ph ON sc.maPhong = ph.maPhong " +
                "WHERE CAST(sc.ngayChieu AS DATE) = CAST(GETDATE() AS DATE)";
        return result;
    }

}
