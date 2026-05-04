package model;

import java.awt.BorderLayout;
/**
 * CinemaData – Toàn bộ dữ liệu tĩnh.
 */
public final class CinemaData {

    public static final String[] RAP_LIST = {
            "CGV Vincom Center",
            "CGV Crescent Mall",
            "Lotte Cinema Nowzone",
            "Galaxy Cinema Nguyễn Du",
            "BHD Star Bitexco"
    };

    public static final String[] RAP_DIA_CHI = {
            "72 Lê Thánh Tôn, Q.1, TP.HCM",
            "101 Tôn Dật Tiên, Q.7, TP.HCM",
            "235-239 Nguyễn Văn Cừ, Q.5, TP.HCM",
            "116 Nguyễn Du, Q.1, TP.HCM",
            "19-25 Nguyễn Huệ, Q.1, TP.HCM"
    };

    public static final String[] PHIM_LIST = {
            "Avengers", "Conan", "Doraemon", "X-Men", "Spider-Man", "Bố Già"
    };
    public static final String[] PHIM_THE_LOAI = {
            "Hành động", "Trinh thám", "Hoạt hình", "Viễn tưởng", "Viễn tưởng", "Gia đình"
    };
    public static final int[] PHIM_THOI_LUONG = { 120, 100, 125, 100, 148, 130 };

    public static final String[][] SUAT_BY_PHIM = {
            { "18:00  Thứ 2, 21/4/2026", "20:30  Thứ 2, 21/4/2026", "22:00  Thứ 3, 22/4/2026" },
            { "15:00  Thứ 3, 22/4/2026", "19:00  Thứ 3, 22/4/2026", "21:30  Thứ 4, 23/4/2026" },
            { "10:00  Thứ 4, 23/4/2026", "13:00  Thứ 4, 23/4/2026", "16:30  Thứ 5, 24/4/2026" },
            { "17:00  Thứ 5, 24/4/2026", "20:00  Thứ 5, 24/4/2026" },
            { "14:00  Thứ 6, 25/4/2026", "18:30  Thứ 6, 25/4/2026", "21:00  Thứ 7, 26/4/2026" },
            { "11:00  Thứ 7, 26/4/2026", "15:30  Thứ 7, 26/4/2026", "19:00  CN, 27/4/2026" }
    };

    public static final String[][] PHONG_BY_PHIM = {
            { "Phòng 1  (2D)", "Phòng 5  (IMAX)" },
            { "Phòng 2  (3D)", "Phòng 4  (2D)" },
            { "Phòng 3  (2D)", "Phòng 6  (IMAX)" },
            { "Phòng 7  (IMAX)", "Phòng 8  (4DX)" },
            { "Phòng 2  (3D)", "Phòng 9  (Dolby)" },
            { "Phòng 1  (2D)", "Phòng 3  (3D)" }
    };

    public static final int[][] POSTER_GRAD = {
            { 0x1A1A2E, 0xE94560 },
            { 0x0D2137, 0x00C6FB },
            { 0x0F3460, 0xFFC300 },
            { 0x1B0036, 0x8A2BE2 },
            { 0x7B0028, 0xFF4E6A },
            { 0x1A3A1A, 0x74D680 }
    };

    public static final int GIA_THUONG = 90_000;
    public static final int GIA_VIP = 130_000;

    public static final String[] SEAT_ROWS = { "A", "B", "C", "D", "E", "F", "G" };
    public static final int SEAT_COLS = 11;

    public static final boolean[][] SOLD = {
            { false, false, true, true, false, false, false, false, false, false, false },
            { false, false, false, true, true, false, false, true, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, false, true, true, false, false, false, false, false },
            { false, false, false, false, false, false, false, false, false, true, true },
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false }
    };
    public static final boolean[][] VIP_SEATS = {
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false },
            { false, false, false, true, true, true, true, true, false, false, false },
            { false, false, false, true, true, true, true, true, false, false, false },
            { false, false, false, true, true, true, true, true, false, false, false },
            { false, false, false, false, false, false, false, false, false, false, false }
    };

    // { icon_fallback, tên, mô tả, giá hiển thị, giá int, màu hex, image_path }
    public static final Object[][] SNACK_DATA = {
        { "🍿", "Bắp Ngô",      "50g  |  Bơ / Caramel",          "30.000 đ",  30_000, 0xF59E0B, "/resources/foods/bong_ngo.jpg"        },
        { "🌭", "Hotdog",        "Xúc xích nướng giòn tan",        "35.000 đ",  35_000, 0xEF4444, "/resources/foods/hot_dog.jpg"          },
        { "🍟", "Khoai Tây",     "Giòn thơm  |  Sốt Mayo / BBQ",  "40.000 đ",  40_000, 0xFBBF24, "/resources/foods/khoai_tay_chien.png"  },
        { "🥤", "Coca-Cola",     "330ml  |  Lạnh sảng khoái",      "25.000 đ",  25_000, 0xEF4444, "/resources/foods/cocacola.jpg"        },
        { "🥤", "Pepsi",         "330ml  |  Vị ngọt đặc trưng",    "25.000 đ",  25_000, 0x3B82F6, "/resources/foods/pepsi.jpg"           },
        { "💧", "Nước Suối",     "500ml  |  Thanh mát",            "15.000 đ",  15_000, 0x06B6D4, "/resources/foods/nuoc_suoi.jpg"       },
        { "🎬", "Combo 1",       "Bắp Ngô + Coca hoặc Pepsi",      "50.000 đ",  50_000, 0x8B5CF6, null                                   },
        { "🎉", "Combo 2",       "Bắp Ngô + Hotdog + Nước Suối",   "75.000 đ",  75_000, 0xEC4899, null                                   },
    };
    // Map mỗi phòng → 1 roomKey (để SeatMapPanel biết load kiểu ghế)
    public static final int[][] PHONG_ROOM_KEY = {
            { 0, 1 }, // Avengers
            { 1, 2 }, // Conan
            { 2, 0 }, // Doraemon
            { 1, 2 }, // X-Men
            { 0, 2 }, // Spider-Man
            { 1, 0 } // Bố Già
    };
public static final String[] POSTER_PATH = {
    "/resources/list_film/avengers.jpg",
    "/resources/list_film/conan.jpg",
    "/resources/list_film/doraemon.jpg",
    "/resources/list_film/xmen.jpg",
    "/resources/list_film/spiderman.jpg",
    "/resources/list_film/bogia.jpg"
};
    private CinemaData() {}
}