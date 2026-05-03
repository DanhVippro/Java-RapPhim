-- ============================================================
--  MEGADE Cinema – SQL Server Schema + Dữ liệu mẫu
-- ============================================================

USE master;
GO

IF DB_ID('MegadeCinema') IS NOT NULL
    DROP DATABASE MegadeCinema;
GO
CREATE DATABASE MegadeCinema;
GO
USE MegadeCinema;
GO

-- ─────────────────────────────────────────────────────────────
-- 1. TÀI KHOẢN (đăng nhập + phân quyền)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE TaiKhoan (
    maTK       INT IDENTITY(1,1) PRIMARY KEY,
    tenDangNhap NVARCHAR(50)  NOT NULL UNIQUE,
    matKhau    NVARCHAR(256) NOT NULL,   -- SHA-256 hex
    vaiTro     NVARCHAR(20)  NOT NULL    -- 'ADMIN' | 'NHANVIEN'
        CHECK (vaiTro IN ('ADMIN','NHANVIEN')),
    hoTen      NVARCHAR(100) NOT NULL,
    email      NVARCHAR(100),
    soDT       NVARCHAR(15),
    trangThai  BIT NOT NULL DEFAULT 1    -- 1=hoạt động, 0=bị khoá
);
GO

-- ─────────────────────────────────────────────────────────────
-- 2. NHÂN VIÊN
-- ─────────────────────────────────────────────────────────────
CREATE TABLE NhanVien (
    maNV       INT IDENTITY(1,1) PRIMARY KEY,
    maTK       INT REFERENCES TaiKhoan(maTK),
    hoTen      NVARCHAR(100) NOT NULL,
    soDT       NVARCHAR(15),
    chucVu     NVARCHAR(50)  NOT NULL
        CHECK (chucVu IN (N'Quản lý', N'Thu ngân', N'Nhân viên kỹ thuật')),
    trangThai  NVARCHAR(20)  NOT NULL DEFAULT N'Đang làm'
        CHECK (trangThai IN (N'Đang làm', N'Nghỉ'))
);
GO

-- ─────────────────────────────────────────────────────────────
-- 3. PHIM
-- ─────────────────────────────────────────────────────────────
CREATE TABLE Phim (
    maPhim     INT IDENTITY(1,1) PRIMARY KEY,
    tenPhim    NVARCHAR(200) NOT NULL,
    theLoai    NVARCHAR(100),
    thoiLuong  INT,          -- phút
    ngayKhoiChieu DATE,
    moTa       NVARCHAR(MAX),
    trangThai  NVARCHAR(20) NOT NULL DEFAULT N'Đang chiếu'
        CHECK (trangThai IN (N'Đang chiếu', N'Sắp chiếu', N'Ngừng chiếu'))
);
GO

-- ─────────────────────────────────────────────────────────────
-- 4. PHÒNG CHIẾU
-- ─────────────────────────────────────────────────────────────
CREATE TABLE PhongChieu (
    maPhong    INT IDENTITY(1,1) PRIMARY KEY,
    tenPhong   NVARCHAR(100) NOT NULL,
    loaiPhong  NVARCHAR(20)  NOT NULL CHECK (loaiPhong IN ('2D','3D','IMAX')),
    soGhe      INT NOT NULL
);
GO

-- ─────────────────────────────────────────────────────────────
-- 5. SUẤT CHIẾU
-- ─────────────────────────────────────────────────────────────
CREATE TABLE SuatChieu (
    maSuat     INT IDENTITY(1,1) PRIMARY KEY,
    maPhim     INT NOT NULL REFERENCES Phim(maPhim),
    maPhong    INT NOT NULL REFERENCES PhongChieu(maPhong),
    ngayChieu  DATE NOT NULL,
    gioChieu   TIME NOT NULL,
    giaVe      INT  NOT NULL DEFAULT 90000
);
GO

-- ─────────────────────────────────────────────────────────────
-- 6. GHẾ
-- ─────────────────────────────────────────────────────────────
CREATE TABLE Ghe (
    maGhe      INT IDENTITY(1,1) PRIMARY KEY,
    maPhong    INT NOT NULL REFERENCES PhongChieu(maPhong),
    hangGhe    CHAR(1) NOT NULL,   -- A, B, C ...
    soGhe      INT NOT NULL,
    loaiGhe    NVARCHAR(20) NOT NULL DEFAULT N'Thường'
        CHECK (loaiGhe IN (N'Thường', N'VIP'))
);
GO

-- ─────────────────────────────────────────────────────────────
-- 7. VÉ (HOÁ ĐƠN VÉ)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE Ve (
    maVe       INT IDENTITY(1,1) PRIMARY KEY,
    maSuat     INT NOT NULL REFERENCES SuatChieu(maSuat),
    maGhe      INT NOT NULL REFERENCES Ghe(maGhe),
    maNV       INT NOT NULL REFERENCES NhanVien(maNV),
    ngayBan    DATETIME NOT NULL DEFAULT GETDATE(),
    giaVe      INT NOT NULL,
    trangThai  NVARCHAR(20) NOT NULL DEFAULT N'Đã bán'
        CHECK (trangThai IN (N'Đã bán', N'Đã huỷ'))
);
GO

-- ─────────────────────────────────────────────────────────────
-- 8. ĐỒ ĂN / ĐỒ UỐNG (Snack)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE Snack (
    maSnack    INT IDENTITY(1,1) PRIMARY KEY,
    tenSnack   NVARCHAR(100) NOT NULL,
    moTa       NVARCHAR(200),
    gia        INT NOT NULL,
    loai       NVARCHAR(20) NOT NULL CHECK (loai IN (N'Bắp', N'Nước', N'Combo'))
);
GO

-- ─────────────────────────────────────────────────────────────
-- 9. CHI TIẾT HOÁ ĐƠN SNACK
-- ─────────────────────────────────────────────────────────────
CREATE TABLE HoaDonSnack (
    maHD       INT IDENTITY(1,1) PRIMARY KEY,
    maVe       INT NOT NULL REFERENCES Ve(maVe),
    maSnack    INT NOT NULL REFERENCES Snack(maSnack),
    soLuong    INT NOT NULL DEFAULT 1,
    thanhTien  INT NOT NULL
);
GO

-- ============================================================
--  DỮ LIỆU MẪU
-- ============================================================

-- Tài khoản (mật khẩu = SHA-256 của "admin123" / "nv123")
-- SHA-256("admin123") = 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a
-- SHA-256("nv123")    = e3ee1f3d6e6b61ea76c8c3e46c8fc02aede98e0c3e04af65e99da11f7d4ee018
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro, hoTen, email, soDT) VALUES
(N'admin',   N'240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a', N'ADMIN',    N'Nguyễn Quản Trị',  N'admin@megade.vn',  N'0901000001'),
(N'nv01',    N'e3ee1f3d6e6b61ea76c8c3e46c8fc02aede98e0c3e04af65e99da11f7d4ee018', N'NHANVIEN', N'Trần Thị Lan',      N'lan@megade.vn',     N'0901000002'),
(N'nv02',    N'e3ee1f3d6e6b61ea76c8c3e46c8fc02aede98e0c3e04af65e99da11f7d4ee018', N'NHANVIEN', N'Lê Văn Hùng',       N'hung@megade.vn',    N'0901000003');
GO

INSERT INTO NhanVien (maTK, hoTen, soDT, chucVu, trangThai) VALUES
(1, N'Nguyễn Quản Trị',  N'0901000001', N'Quản lý',             N'Đang làm'),
(2, N'Trần Thị Lan',      N'0901000002', N'Thu ngân',             N'Đang làm'),
(3, N'Lê Văn Hùng',       N'0901000003', N'Nhân viên kỹ thuật',  N'Đang làm');
GO

INSERT INTO Phim (tenPhim, theLoai, thoiLuong, ngayKhoiChieu, moTa, trangThai) VALUES
(N'Avatar 2',          N'Hành động, Phiêu lưu', 192, '2024-01-15', N'Phần tiếp theo của Avatar',          N'Đang chiếu'),
(N'Kẻ Cắp Giấc Mơ',   N'Tâm lý, Hồi hộp',     148, '2024-03-20', N'Bộ phim về thế giới giấc mơ',        N'Đang chiếu'),
(N'Spider-Man',        N'Siêu anh hùng',        130, '2024-04-05', N'Người Nhện phiêu lưu đa vũ trụ',    N'Đang chiếu');
GO

INSERT INTO PhongChieu (tenPhong, loaiPhong, soGhe) VALUES
(N'Phòng chiếu 1', '2D',   80),
(N'Phòng chiếu 2', '3D',   60),
(N'Phòng chiếu 3', '2D',   80),
(N'Phòng chiếu 4', '3D',   60),
(N'Phòng chiếu 5', 'IMAX', 120),
(N'Phòng chiếu 6', 'IMAX', 120);
GO

INSERT INTO SuatChieu (maPhim, maPhong, ngayChieu, gioChieu, giaVe) VALUES
(1, 3, '2024-06-21', '19:00', 90000),
(1, 5, '2024-06-22', '14:00', 130000),
(1, 5, '2024-06-22', '21:30', 130000),
(2, 1, '2024-06-21', '15:00', 90000),
(2, 4, '2024-06-21', '20:00', 110000),
(3, 2, '2024-06-22', '10:00', 110000),
(3, 2, '2024-06-22', '16:30', 110000),
(3, 6, '2024-06-23', '22:00', 130000);
GO

-- Ghế phòng 3 (80 ghế: A1-H10, hàng H là VIP)
DECLARE @p INT = 3, @r INT, @s INT;
DECLARE @hang CHAR(1);
DECLARE @rows TABLE (r INT, h CHAR(1));
INSERT INTO @rows VALUES (1,'A'),(2,'B'),(3,'C'),(4,'D'),(5,'E'),(6,'F'),(7,'G'),(8,'H');
DECLARE rc CURSOR FOR SELECT r, h FROM @rows;
OPEN rc; FETCH NEXT FROM rc INTO @r, @hang;
WHILE @@FETCH_STATUS = 0 BEGIN
    SET @s = 1;
    WHILE @s <= 10 BEGIN
        INSERT INTO Ghe (maPhong, hangGhe, soGhe, loaiGhe)
        VALUES (@p, @hang, @s, CASE WHEN @hang='H' THEN N'VIP' ELSE N'Thường' END);
        SET @s = @s + 1;
    END
    FETCH NEXT FROM rc INTO @r, @hang;
END
CLOSE rc; DEALLOCATE rc;
GO

INSERT INTO Snack (tenSnack, moTa, gia, loai) VALUES
(N'Bắp Nhỏ',   N'50g | Bơ / Caramel',              30000, N'Bắp'),
(N'Bắp Vừa',   N'80g | Bơ / Caramel',              45000, N'Bắp'),
(N'Bắp Lớn',   N'120g | Bơ / Caramel',             60000, N'Bắp'),
(N'Nước Nhỏ',  N'250ml | Cola / Sprite / Fanta',   25000, N'Nước'),
(N'Nước Vừa',  N'400ml | Cola / Sprite / Fanta',   35000, N'Nước'),
(N'Nước Lớn',  N'550ml | Cola / Sprite / Fanta',   45000, N'Nước'),
(N'Combo 1',   N'Bắp Vừa + Nước Vừa',              70000, N'Combo'),
(N'Combo 2',   N'Bắp Lớn + 2 Nước Vừa',           110000, N'Combo');
GO

PRINT N'✅  Database MegadeCinema đã được tạo thành công!';
GO
-- ============================================================
--  FIX mật khẩu – chạy file này trong SSMS
--  (chỉ cần chạy 1 lần sau khi đã tạo database)
-- ============================================================

USE MegadeCinema;
GO

-- SHA-256 đúng của "admin123"
UPDATE TaiKhoan
SET matKhau = '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9'
WHERE tenDangNhap = 'admin';

-- SHA-256 đúng của "nv123"
UPDATE TaiKhoan
SET matKhau = '70ae1c421727d20eb63385ae5763f2d798dc4c0bd66663bdf0f13e7512f5767f'
WHERE tenDangNhap IN ('nv01', 'nv02');

-- Kiểm tra lại
SELECT tenDangNhap, LEFT(matKhau,20) + '...' AS matKhau_preview, vaiTro, trangThai
FROM TaiKhoan;

PRINT N'✅ Cập nhật mật khẩu thành công!';
GO

USE MegadeCinema;
GO

-- Thêm cột poster_path vào bảng Phim
ALTER TABLE Phim ADD poster_path NVARCHAR(255);
GO

-- Cập nhật đường dẫn poster cho các phim hiện có
UPDATE Phim SET poster_path = 'avatar2.jpg' WHERE tenPhim = N'Avatar 2';
UPDATE Phim SET poster_path = 'ke_cap_giac_mo.jpg' WHERE tenPhim = N'Kẻ Cắp Giấc Mơ';
UPDATE Phim SET poster_path = 'spiderman.jpg' WHERE tenPhim = N'Spider-Man';
GO

-- Kiểm tra kết quả
SELECT maPhim, tenPhim, poster_path FROM Phim;
GO