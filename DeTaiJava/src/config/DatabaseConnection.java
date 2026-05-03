package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=MegadeCinema;"
            + "encrypt=true;trustServerCertificate=true";

    private static final String USER = "sa";
    private static final String PASSWORD = "123456";
    // Sửa lại user/password cho phù hợp với

    private static Connection instance = null;

    public static Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Kết nối SQL Server thành công.");
            }
        } catch (Exception e) {
            throw new RuntimeException("[DB] Lỗi: " + e.getMessage(), e);
        }
       
        return instance;
    }

    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}