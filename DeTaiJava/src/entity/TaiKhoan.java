package entity;

public class TaiKhoan {
    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maTaiKhoan == null) ? 0 : maTaiKhoan.hashCode());
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
        TaiKhoan other = (TaiKhoan) obj;
        if (maTaiKhoan == null) {
            if (other.maTaiKhoan != null)
                return false;
        } else if (!maTaiKhoan.equals(other.maTaiKhoan))
            return false;
        return true;
    }

}