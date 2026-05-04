package service;

import DAO.PhimDAO;
import java.sql.Date;
import java.util.List;

public class PhimService {
    private final PhimDAO dao = new PhimDAO();

    public List<Object[]> getAllPhim() {
        return dao.getAllPhim();
    }

    public List<Object[]> getPhimDangChieu() {
        return dao.getPhimDangChieu();
    }

    public boolean addPhim(String ten, String theLoai, int thoiLuong, Date ngayKC, String moTa, String trangThai, String poster) {
        return dao.addPhim(ten, theLoai, thoiLuong, ngayKC, moTa, trangThai, poster);
    }

    public boolean updatePhim(int ma, String ten, String theLoai, int thoiLuong, Date ngayKC, String moTa, String trangThai, String poster) {
        return dao.updatePhim(ma, ten, theLoai, thoiLuong, ngayKC, moTa, trangThai, poster);
    }

    public boolean deletePhim(int ma) {
        return dao.deletePhim(ma);
    }
}
