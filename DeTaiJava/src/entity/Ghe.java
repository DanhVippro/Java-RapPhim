package entity;

public class Ghe {
    private String maGhe;
    private PhongChieu phong;
    private String soGhe; // A1, B2,...
    private String loaiGhe;

    public Ghe(String maGhe, PhongChieu phong, String soGhe, String loaiGhe) {
        this.maGhe = maGhe;
        this.phong = phong;
        this.soGhe = soGhe;
        this.loaiGhe = loaiGhe;
    }

    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public PhongChieu phong() {
        return phong;
    }

    public void setMaPhong(PhongChieu phong) {
        this.phong = phong;
    }

    public String getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(String soGhe) {
        this.soGhe = soGhe;
    }

    public String getLoaiGhe() {
        return loaiGhe;
    }

    public void setLoaiGhe(String loaiGhe) {
        this.loaiGhe = loaiGhe;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maGhe == null) ? 0 : maGhe.hashCode());
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
        Ghe other = (Ghe) obj;
        if (maGhe == null) {
            if (other.maGhe != null)
                return false;
        } else if (!maGhe.equals(other.maGhe))
            return false;
        return true;
    }

}
