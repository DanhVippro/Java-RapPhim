package entity;

public class NhanVien {
      private int maNV; 
    private String hoTen;
    private String soDT;
    private String chucVu;
    private String trangThai;

    public NhanVien(String hoTen, String soDT, String chucVu, String trangThai) {
        this.hoTen = hoTen;
        this.soDT = soDT;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
    }
     public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }
    public String getHoTen() {
        return hoTen;
    }
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    public String getSoDT() {
        return soDT;
    }
    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }
    public String getChucVu() {
        return chucVu;
    }
    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }
    public String getTrangThai() {
        return trangThai;
    }
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    @Override
    public String toString() {
        return "NhanVien [maNV=" + maNV + ", hoTen=" + hoTen + ", soDT=" + soDT + ", chucVu=" + chucVu + ", trangThai="
                + trangThai + "]";
    }
   
    

    
}
