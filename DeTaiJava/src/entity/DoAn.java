package entity;

/**
 * DoAn – Entity đồ ăn / nước uống trong rạp.
 */
public class DoAn {
    private int    maDoAn;
    private String ten;
    private String moTa;
    private int    gia;
    private String loai;       // "BAP", "NUOC", "COMBO", "KHAI_VI"
    private String imagePath;  // Đường dẫn ảnh trong resources/

    public DoAn() {}

    public DoAn(int maDoAn, String ten, String moTa, int gia, String loai, String imagePath) {
        this.maDoAn    = maDoAn;
        this.ten       = ten;
        this.moTa      = moTa;
        this.gia       = gia;
        this.loai      = loai;
        this.imagePath = imagePath;
    }

    public int    getMaDoAn()    { return maDoAn; }
    public String getTen()       { return ten; }
    public String getMoTa()      { return moTa; }
    public int    getGia()       { return gia; }
    public String getLoai()      { return loai; }
    public String getImagePath() { return imagePath; }

    public String getGiaDisplay() {
        return String.format("%,d đ", gia).replace(',', '.');
    }

    public boolean isCombo() { return "COMBO".equals(loai); }
}
