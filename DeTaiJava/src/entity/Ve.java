package entity;

public class Ve {
    private String maVe;
    private DatVe datVe;
    private Ghe ghe;
    private double gia;

    public Ve(String maVe, DatVe datVe, Ghe ghe, double gia) {
        this.maVe = maVe;
        this.datVe = datVe;
        this.ghe = ghe;
        this.gia = gia;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public DatVe datVe() {
        return datVe;
    }

    public void setMaDatVe(DatVe datVe) {
        this.datVe = datVe;
    }

    public Ghe ghe() {
        return ghe;
    }

    public void setMaGhe(Ghe ghe) {
        this.ghe = ghe;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((maVe == null) ? 0 : maVe.hashCode());
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
        Ve other = (Ve) obj;
        if (maVe == null) {
            if (other.maVe != null)
                return false;
        } else if (!maVe.equals(other.maVe))
            return false;
        return true;
    }

}
