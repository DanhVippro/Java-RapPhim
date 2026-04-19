package entity;

import java.time.LocalDateTime;

public class DatVe {
    private String maDatVe;
    private KhachHang khachHang;
    private String maSuatChieu;
    private LocalDateTime thoiDiemDat;
    private double tongTien;
    private String trangThai; // Đã thanh toán / Chưa / Hủy

    public DatVe(String maDatVe, KhachHang khachHang, String maSuatChieu, LocalDateTime thoiDiemDat, double tongTien,
            String trangThai) {
        this.maDatVe = maDatVe;
        this.khachHang = khachHang;
        this.maSuatChieu = maSuatChieu;
        this.thoiDiemDat = thoiDiemDat;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getMaDatVe() {
        return maDatVe;
    }

    public void setMaDatVe(String maDatVe) {
        this.maDatVe = maDatVe;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setMaKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public String getMaSuatChieu() {
        return maSuatChieu;
    }

    public void setMaSuatChieu(String maSuatChieu) {
        this.maSuatChieu = maSuatChieu;
    }

    public LocalDateTime getThoiDiemDat() {
        return thoiDiemDat;
    }

    public void setThoiDiemDat(LocalDateTime thoiDiemDat) {
        this.thoiDiemDat = thoiDiemDat;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maDatVe == null) ? 0 : maDatVe.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DatVe other = (DatVe) obj;
        if (maDatVe == null) {
            if (other.maDatVe != null)
                return false;
        } else if (!maDatVe.equals(other.maDatVe))
            return false;
        return true;
    }

}
