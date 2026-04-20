CREATE DATABASE cinema_db;
GO

USE cinema_db;
GO
CREATE TABLE TaiKhoan (
    maTaiKhoan VARCHAR(20) PRIMARY KEY,
    tenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    matKhau VARCHAR(255) NOT NULL
);
CREATE TABLE KhachHang (
    maKhachHang VARCHAR(20) PRIMARY KEY,
    ten NVARCHAR(100),
    soDienThoai VARCHAR(15),
    email VARCHAR(100)
);
CREATE TABLE PhongChieu (
    maPhong VARCHAR(20) PRIMARY KEY,
    tenPhong NVARCHAR(100),
    sucChua INT
);
CREATE TABLE Phim (
    maPhim VARCHAR(20) PRIMARY KEY,
    tenPhim NVARCHAR(200),
    theLoai NVARCHAR(100),
    thoiLuong INT,
    moTa NVARCHAR(MAX),
    hinhAnh VARCHAR(255),
    trangThai NVARCHAR(50)
);
CREATE TABLE Ghe (
    maGhe VARCHAR(20) PRIMARY KEY,
    maPhong VARCHAR(20),
    soGhe VARCHAR(10),
    loaiGhe VARCHAR(20),

    FOREIGN KEY (maPhong) REFERENCES PhongChieu(maPhong)
);
CREATE TABLE SuatChieu (
    maSuatChieu VARCHAR(20) PRIMARY KEY,
    maPhim VARCHAR(20),
    maPhong VARCHAR(20),
    thoiGianBatDau DATETIME,
    giaVe FLOAT,

    FOREIGN KEY (maPhim) REFERENCES Phim(maPhim),
    FOREIGN KEY (maPhong) REFERENCES PhongChieu(maPhong)
);
CREATE TABLE DatVe (
    maDatVe VARCHAR(20) PRIMARY KEY,
    maKhachHang VARCHAR(20),
    maSuatChieu VARCHAR(20),
    thoiDiemDat DATETIME,
    tongTien FLOAT,
    trangThai NVARCHAR(50),

    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maSuatChieu) REFERENCES SuatChieu(maSuatChieu)
);
CREATE TABLE Ve (
    maVe VARCHAR(20) PRIMARY KEY,
    maDatVe VARCHAR(20),
    maGhe VARCHAR(20),
    gia FLOAT,

    FOREIGN KEY (maDatVe) REFERENCES DatVe(maDatVe),
    FOREIGN KEY (maGhe) REFERENCES Ghe(maGhe)
);


ALTER TABLE Ve
ADD CONSTRAINT unique_seat_showtime UNIQUE (maGhe, maDatVe);

INSERT INTO TaiKhoan VALUES
('TK001', 'admin', '123456'),
('TK002', 'staff1', '123456');

INSERT INTO KhachHang VALUES
('KH001', N'Nguy?n V�n A', '0901111111', 'a@gmail.com'),
('KH002', N'Tr?n Th? B', '0902222222', 'b@gmail.com'),
('KH003', N'Tr?n Th�nh Danh', '0123456789', 'danh@gmail.com'),
('KH004', N'Tr?n Th? D', '0902222222', 'd@gmail.com');

INSERT INTO PhongChieu VALUES
('P001', N'Ph?ng 1', 50),
('P002', N'Ph?ng 2', 40),
('P003', N'Ph?ng 3', 60),
('P004', N'Ph?ng 4', 40),
('P005', N'Ph?ng 5', 50),
('P006', N'Ph?ng 6', 60);

INSERT INTO Ghe VALUES
('G001', 'P001', 'A1', 'normal'),
('G002', 'P001', 'A2', 'vip'),
('G003', 'P001', 'A3', 'normal'),
('G004', 'P002', 'B1', 'normal'),
('G005', 'P002', 'B2', 'vip'),
('G006', 'P003', 'A5', 'normal'),
('G007', 'P003', 'A4', 'vip'),
('G008', 'P003', 'A7', 'normal'),
('G009', 'P004', 'B4', 'normal'),
('G010', 'P004', 'B6', 'vip');
INSERT INTO Phim VALUES
('PH001', N'Avengers', N'H�nh �?ng', 120, N'Si�u anh h�ng c?u th? gi?i', '/res/img/avengers.jpg', N'�ang chi?u'),
('PH002', N'Conan', N'Trinh th�m', 100, N'Th�m t? l?ng danh', '/res/img/conan.jpg', N'S?p chi?u'),
('PH003', N'Doreamon', N'Ho?t h?nh', 125, N'Ch� m�o m�y �?n t? t��ng lai', '/res/img/doreamon.jpg', N'S?p chi?u'),
('PH004', N'Conan 2', N'Trinh th�m', 100, N'Th�m t? l?ng danh 2', '/res/img/conan2.jpg', N'S?p chi?u'),
('PH005', N'X Men', N'Vi?n t�?ng', 100, N'Bi?t �?i si�u anh h�ng', '/res/img/xmen.jpg', N'S?p chi?u'),
('PH006', N'B? gi�', N'Gia �?nh', 100, N'B? gi� Tr?n Th�nh', '/res/img/bogia.jpg', N'�ang chi?u'),
('PH007', N'Spider Man: Go Home', N'Vi?n t�?ng', 100, N'Si�u nh�n nh?n nh?n', '/res/img/spiderman.jpg', N'�ang chi?u'),
('PH008', N'Super Man', N'Vi?n t�?ng', 100, N'Si�u anh h�ng SuperMan', '/res/img/superman.jpg', N'�ang chi?u'),
('PH009', N'Breaking Bad', N'T�m l?', 100, N'T?i ph�m bi?n ch?t', '/res/img/breakingbad.jpg', N'S?p chi?u');
INSERT INTO SuatChieu VALUES
('SC001', 'PH001', 'P001', '2026-04-21 18:00:00', 75000),
('SC002', 'PH002', 'P002', '2026-04-21 20:00:00', 90000),
('SC003', 'PH003', 'P003', '2026-04-21 18:00:00', 75000),
('SC004', 'PH004', 'P004', '2026-04-21 20:00:00', 90000),
('SC005', 'PH005', 'P005', '2026-04-21 18:00:00', 75000),
('SC006', 'PH006', 'P006', '2026-04-21 20:00:00', 90000);

INSERT INTO DatVe VALUES
('DV001', 'KH001', 'SC001', GETDATE(), 150000, N'�? thanh to�n'),
('DV002', 'KH002', 'SC002', GETDATE(), 90000, N'Ch�a thanh to�n'),
('DV006', 'KH002', 'SC002', GETDATE(), 90000, N'Ch�a thanh to�n'),
('DV003', 'KH003', 'SC002', GETDATE(), 150000, N'�? thanh to�n'),
('DV004', 'KH004', 'SC001', GETDATE(), 200000, N'Ch�a thanh to�n'),
('DV005', 'KH004', 'SC003', GETDATE(), 150000, N'Ch�a thanh to�n');
INSERT INTO Ve VALUES
('V001', 'DV001', 'G001', 75000),
('V002', 'DV001', 'G002', 75000),
('V003', 'DV002', 'G004', 90000),
('V004', 'DV004', 'G005', 75000),
('V005', 'DV005', 'G003', 75000),
('V006', 'DV006', 'G006', 90000);

