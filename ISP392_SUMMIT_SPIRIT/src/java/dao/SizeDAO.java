package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

public class SizeDAO {

    public int getOrInsertSize(String sizeName) throws SQLException, ClassNotFoundException {
        if (sizeName == null || sizeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Size name cannot be null or empty");
        }
        sizeName = sizeName.trim();

        String selectSql = "SELECT SizeID FROM Size WHERE LOWER(SizeName) = LOWER(?)";
        String insertSql = "INSERT INTO Size (SizeName) VALUES (?)";

        try ( Connection conn = DBUtils.getConnection()) {
            // Check tồn tại
            try ( PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, sizeName);
                try ( ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("SizeID");
                    }
                }
            }

            // Chưa tồn tại → Insert
            try ( PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, sizeName);
                ps.executeUpdate();
                try ( ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        throw new SQLException("Failed to insert or retrieve new SizeID.");
    }

    public String getSizeNameById(int sizeID) throws SQLException, ClassNotFoundException {
        String sql = "SELECT SizeName FROM Size WHERE SizeID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sizeID);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("SizeName");
                }
            }
        }
        return null;
    }

    public List<String> getAllSizes() throws SQLException, ClassNotFoundException {
        List<String> sizes = new ArrayList<>();
        String sql = "SELECT SizeName FROM Size";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sizes.add(rs.getString("SizeName"));
            }
        }
        return sizes;
    }
}
