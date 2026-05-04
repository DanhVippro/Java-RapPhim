package entity;

/**
 * Entity ánh xạ bảng TaiKhoan – dùng để lưu thông tin user đang đăng nhập.
 */
public class TaiKhoan {

    private int maTK;
    private String tenDangNhap;
    private String vaiTro; // "ADMIN" | "NHANVIEN"
    private String hoTen;
    private String email;
    private String soDT;
    private boolean trangThai; // true = hoạt động

    public TaiKhoan() {
    }

    public TaiKhoan(int maTK, String tenDangNhap, String vaiTro,
            String hoTen, String email, String soDT, boolean trangThai) {
        this.maTK = maTK;
        this.tenDangNhap = tenDangNhap;
        this.vaiTro = vaiTro;
        this.hoTen = hoTen;
        this.email = email;
        this.soDT = soDT;
        this.trangThai = trangThai;
    }

    // Getters
    public int getMaTK() {
        return maTK;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getEmail() {
        return email;
    }

    public String getSoDT() {
        return soDT;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    // Setters
    public void setMaTK(int maTK) {
        this.maTK = maTK;
    }

    public void setTenDangNhap(String v) {
        this.tenDangNhap = v;
    }

    public void setVaiTro(String v) {
        this.vaiTro = v;
    }

    public void setHoTen(String v) {
        this.hoTen = v;
    }

    public void setEmail(String v) {
        this.email = v;
    }

    public void setSoDT(String v) {
        this.soDT = v;
    }

    public void setTrangThai(boolean v) {
        this.trangThai = v;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(vaiTro);
    }

    public boolean isNhanVien() {
        return "NHANVIEN".equals(vaiTro);
    }

    @Override
    public String toString() {
        return hoTen + " (" + vaiTro + ")";
    }

    public char[] getId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getId'");
    }

    public Object getSoDienThoai() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSoDienThoai'");
    }
}
