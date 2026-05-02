package model;

import java.util.ArrayList;
import java.util.List;

/**
 * BookingState – State dùng chung xuyên suốt 4 bước đặt vé.
 */
public class BookingState {

    // Bước 1 – Chọn phim / rạp
    public int rapIdx   = 0;
    public int phimIdx  = 0;
    public int suatIdx  = 0;
    public int phongIdx = 0;

    // Bước 2 – Ghế
    public final List<String>  seats    = new ArrayList<>();
    public final List<Boolean> seatsVip = new ArrayList<>();

    // Bước 3 – Bắp & nước
    public int[] snackQty = new int[CinemaData.SNACK_DATA.length];

    // Bước 4 – Thông tin khách hàng
    public String tenKhachHang = "";
    public String soDienThoai  = "";
    public String email        = "";

    // ── Computed ─────────────────────────────────────────────────────────────
    public long tienVe() {
        long vip    = seatsVip.stream().filter(b -> b).count();
        long thuong = seats.size() - vip;
        return thuong * CinemaData.GIA_THUONG + vip * CinemaData.GIA_VIP;
    }

    public long tienSnack() {
        long t = 0;
        for (int i = 0; i < snackQty.length; i++)
            t += (long) snackQty[i] * (int) CinemaData.SNACK_DATA[i][4];
        return t;
    }

    public long tongCong() { return tienVe() + tienSnack(); }

    public String gheDisplay() {
        return seats.isEmpty() ? "-" : String.join(", ", seats);
    }

    public String loaiGheDisplay() {
        long vip    = seatsVip.stream().filter(b -> b).count();
        long thuong = seats.size() - vip;
        if (vip > 0 && thuong > 0) return "Thường + VIP";
        if (vip > 0) return "VIP";
        return "Thường";
    }

    public String suatRawDisplay() {
        String[] s = CinemaData.SUAT_BY_PHIM[phimIdx];
        return suatIdx < s.length ? s[suatIdx] : "-";
    }

    public String gioBatDau() {
        String s = suatRawDisplay();
        return s.contains("  ") ? s.split("  ")[0].trim() : s;
    }

    public String gioKetThuc() {
        try {
            String[] p = gioBatDau().split(":");
            int end = Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1])
                    + CinemaData.PHIM_THOI_LUONG[phimIdx];
            return String.format("%02d:%02d", (end / 60) % 24, end % 60);
        } catch (Exception e) { return "--:--"; }
    }

    public String ngayChieu() {
        String s = suatRawDisplay();
        int i = s.indexOf(',');
        return i >= 0 ? s.substring(i + 1).trim() : "-";
    }

    public String thuDisplay() {
        String s = suatRawDisplay();
        int i1 = s.indexOf("  "), i2 = s.indexOf(',');
        return (i1 >= 0 && i2 > i1) ? s.substring(i1, i2).trim() : "";
    }

    public String snackSummary() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < snackQty.length; i++)
            if (snackQty[i] > 0) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(CinemaData.SNACK_DATA[i][1]).append(" x").append(snackQty[i]);
            }
        return sb.length() > 0 ? sb.toString() : "Không chọn";
    }

    public void resetSeats()  { seats.clear(); seatsVip.clear(); }
    public void resetSnack()  { snackQty = new int[CinemaData.SNACK_DATA.length]; }
}