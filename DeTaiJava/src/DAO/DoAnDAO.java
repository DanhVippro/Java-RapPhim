package DAO;

import config.DatabaseConnection;
import entity.DoAn;
import model.CinemaData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DoAnDAO – Truy vấn đồ ăn từ cơ sở dữ liệu.
 *
 * Nếu bảng DoAn chưa tồn tại hoặc bị lỗi kết nối,
 * tự động fallback về dữ liệu tĩnh từ CinemaData.SNACK_DATA.
 */
public class DoAnDAO {

    private Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    /**
     * Lấy toàn bộ danh sách đồ ăn từ DB.
     * Schema bảng DoAn:
     * maDoAn INT PK, ten NVARCHAR(100), moTa NVARCHAR(255),
     * gia INT, loai NVARCHAR(20), imagePath NVARCHAR(255)
     */
    public List<DoAn> getAllDoAn() {
        List<DoAn> list = new ArrayList<>();
        String sql = "SELECT maDoAn, ten, moTa, gia, loai, imagePath FROM DoAn ORDER BY loai, maDoAn";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new DoAn(
                        rs.getInt("maDoAn"),
                        rs.getString("ten"),
                        rs.getString("moTa"),
                        rs.getInt("gia"),
                        rs.getString("loai"),
                        rs.getString("imagePath")));
            }
            System.out.println("[DoAnDAO] Loaded " + list.size() + " items from DB.");
        } catch (SQLException e) {
            System.out.println("[DoAnDAO] DB not available, using static fallback. (" + e.getMessage() + ")");
        }

        // Fallback về CinemaData nếu DB rỗng hoặc lỗi
        if (list.isEmpty()) {
            list = getStaticFallback();
        }
        return list;
    }

    /**
     * Tạo bảng DoAn nếu chưa có (chạy một lần khi khởi động).
     */
    public void createOrderTableIfNotExists() {
        String ddl = """
                    IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='OrderItems' AND xtype='U')
                    CREATE TABLE OrderItems (
                        id INT IDENTITY(1,1) PRIMARY KEY,
                        doan_id INT,
                        ten NVARCHAR(255),
                        so_luong INT,
                        gia BIGINT,
                        created_at DATETIME DEFAULT GETDATE()
                    )
                """;

        try (Connection conn = getConnection();
                Statement st = conn.createStatement()) {

            st.execute(ddl);
            System.out.println("[DoAnDAO] Table OrderItems ready.");

        } catch (SQLException e) {
            System.out.println("[DoAnDAO] Cannot create OrderItems: " + e.getMessage());
        }
    }

    public void createTableIfNotExists() {
        String ddl = """
                IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='DoAn' AND xtype='U')
                CREATE TABLE DoAn (
                    maDoAn    INT IDENTITY(1,1) PRIMARY KEY,
                    ten       NVARCHAR(100)  NOT NULL,
                    moTa      NVARCHAR(255),
                    gia       INT            NOT NULL,
                    loai      NVARCHAR(20)   DEFAULT 'KHAC',
                    imagePath NVARCHAR(255)
                )
                """;
        String insert = """
                IF NOT EXISTS (SELECT 1 FROM DoAn)
                BEGIN
                    INSERT INTO DoAn (ten, moTa, gia, loai, imagePath) VALUES
                    (N'Bắp Ngô',    N'50g | Bơ / Caramel',         30000, 'BAP',    '/resources/foods/bong_ngo.jpg'),
                    (N'Hotdog',     N'Xúc xích nướng giòn tan',    35000, 'KHAI_VI','/resources/foods/hot_dog.jpg'),
                    (N'Khoai Tây',  N'Giòn thơm | Sốt Mayo / BBQ', 40000, 'KHAI_VI','/resources/foods/khoai_tay_chien.png'),
                    (N'Coca-Cola',  N'330ml | Lạnh sảng khoái',    25000, 'NUOC',   '/resources/foods/cocacola.jpg'),
                    (N'Pepsi',      N'330ml | Vị ngọt đặc trưng',  25000, 'NUOC',   '/resources/foods/pepsi.jpg'),
                    (N'Nước Suối',  N'500ml | Thanh mát',          15000, 'NUOC',   '/resources/foods/nuoc_suoi.jpg'),
                    (N'Combo 1',    N'Bắp Ngô + Coca hoặc Pepsi',  50000, 'COMBO',  NULL),
                    (N'Combo 2',    N'Bắp Ngô + Hotdog + Nước Suối',75000,'COMBO',  NULL)
                END
                """;
        try (Connection conn = getConnection();
                Statement st = conn.createStatement()) {
            st.execute(ddl);
            st.execute(insert);
            System.out.println("[DoAnDAO] Table DoAn ready.");
        } catch (SQLException e) {
            System.out.println("[DoAnDAO] Could not init table: " + e.getMessage());
        }
    }

    /** Dữ liệu tĩnh fallback từ CinemaData */
    private List<DoAn> getStaticFallback() {
        List<DoAn> list = new ArrayList<>();
        Object[][] data = CinemaData.SNACK_DATA;
        for (int i = 0; i < data.length; i++) {
            String name = (String) data[i][1];
            String desc = (String) data[i][2];
            int price = (int) data[i][4];
            Object path = data[i][6];
            String loai = name.startsWith("Combo") ? "COMBO"
                    : ((String) data[i][0]).contains("🥤") || ((String) data[i][0]).contains("💧") ? "NUOC"
                            : name.contains("Bắp") ? "BAP" : "KHAI_VI";
            list.add(new DoAn(i + 1, name, desc, price, loai,
                    path != null ? (String) path : null));
        }
        return list;
    }

    public void insertOrderItem(int id, String ten, int qty, long gia) {
        String sql = "INSERT INTO OrderItems (doan_id, ten, so_luong, gia) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, ten);
            ps.setInt(3, qty);
            ps.setLong(4, gia);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
