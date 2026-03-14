/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class DBUtils {

    private static final String DB_NAME = "ISP392";
    private static final String DB_USER_NAME = "sa";
    private static final String DB_PASSWORD = "12345";

public static Connection getConnection() {
    Connection conn = null;
    try {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=" + DB_NAME + ";"
                + "encrypt=true;"
                + "trustServerCertificate=true;";
        conn = DriverManager.getConnection(url, DB_USER_NAME, DB_PASSWORD);
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
    }
    return conn;
}
}
