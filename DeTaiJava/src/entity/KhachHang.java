package entity;

public class KhachHang {
    private String maKhachHang;
    private String ten;
    private String soDienThoai;
    private String email;

    public KhachHang(String maKhachHang, String ten, String soDienThoai, String email) {
        this.maKhachHang = maKhachHang;
        this.ten = ten;
        this.soDienThoai = soDienThoai;
        this.email = email;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maKhachHang == null) ? 0 : maKhachHang.hashCode());
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
        KhachHang other = (KhachHang) obj;
        if (maKhachHang == null) {
            if (other.maKhachHang != null)
                return false;
        } else if (!maKhachHang.equals(other.maKhachHang))
            return false;
        return true;
    }

}
