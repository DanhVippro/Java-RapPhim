package entity;

import java.time.LocalDateTime;

public class SuatChieu {
    private String maSuatChieu;
    private Phim phim;
    private PhongChieu phong;
    private LocalDateTime thoiGianBatDau;
    private double giaVe;

    public SuatChieu(String maSuatChieu, Phim phim, PhongChieu phong, LocalDateTime thoiGianBatDau, double giaVe) {
        this.maSuatChieu = maSuatChieu;
        this.phim = phim;
        this.phong = phong;
        this.thoiGianBatDau = thoiGianBatDau;
        this.giaVe = giaVe;
    }

    public String getMaSuatChieu() {
        return maSuatChieu;
    }

    public void setMaSuatChieu(String maSuatChieu) {
        this.maSuatChieu = maSuatChieu;
    }

    public Phim phim() {
        return phim;
    }

    public void setMaPhim(Phim phim) {
        this.phim = phim;
    }

    public PhongChieu phong() {
        return phong;
    }

    public void setMaPhong(PhongChieu phong) {
        this.phong = phong;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public double getGiaVe() {
        return giaVe;
    }

    public void setGiaVe(double giaVe) {
        this.giaVe = giaVe;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maSuatChieu == null) ? 0 : maSuatChieu.hashCode());
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
        SuatChieu other = (SuatChieu) obj;
        if (maSuatChieu == null) {
            if (other.maSuatChieu != null)
                return false;
        } else if (!maSuatChieu.equals(other.maSuatChieu))
            return false;
        return true;
    }

}
