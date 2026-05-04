package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;
import entity.NhanVien;

public class NhanVienDAO {
    public int insert(NhanVien nv) {
    int id = -1;

    try {
        Connection conn = DatabaseConnection.getConnection();

        String sql = "INSERT INTO NhanVien(hoTen, soDT, chucVu, trangThai) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

        ps.setString(1, nv.getHoTen());
        ps.setString(2, nv.getSoDT());
        ps.setString(3, nv.getChucVu());
        ps.setString(4, nv.getTrangThai());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            id = rs.getInt(1);
        }

        conn.close();

    } catch (Exception e) {
        e.printStackTrace();
    }

    return id;
}
public boolean update(int maNV, NhanVien nv) {
    try {
        Connection conn =  DatabaseConnection.getConnection();

        String sql = "UPDATE NhanVien SET hoTen=?, soDT=?, chucVu=?, trangThai=? WHERE maNV=?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, nv.getHoTen());
        ps.setString(2, nv.getSoDT());
        ps.setString(3, nv.getChucVu());
        ps.setString(4, nv.getTrangThai());
        ps.setInt(5, maNV);

        ps.executeUpdate();
        conn.close();

        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean delete(int maNV) {
    try {
        Connection conn = DatabaseConnection.getConnection();

        String sql = "DELETE FROM NhanVien WHERE maNV=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, maNV);

        ps.executeUpdate();
        conn.close();

        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
public List<NhanVien> getAll() {
    List<NhanVien> list = new ArrayList<>();

    try {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM NhanVien";

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            NhanVien nv = new NhanVien(
                    rs.getString("hoTen"),
                    rs.getString("soDT"),
                    rs.getString("chucVu"),
                    rs.getString("trangThai")
            );
            nv.setMaNV(rs.getInt("maNV")); // nhớ thêm field này trong entity

            list.add(nv);
        }

        conn.close();

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
}
