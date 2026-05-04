package entity;

public class PhongChieu {
    private String maPhong;
    private String tenPhong;
    private int sucChua;

    public PhongChieu(String maPhong, String tenPhong, int sucChua) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.sucChua = sucChua;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maPhong == null) ? 0 : maPhong.hashCode());
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
        PhongChieu other = (PhongChieu) obj;
        if (maPhong == null) {
            if (other.maPhong != null)
                return false;
        } else if (!maPhong.equals(other.maPhong))
            return false;
        return true;
    }

}
