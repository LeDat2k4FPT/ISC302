/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.ProductImageDTO;
import utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class ProductImageDAO {

    // Lấy ImageID tiếp theo bằng MAX + 1
    private int getNextImageID() throws SQLException, ClassNotFoundException {
        String sql = "SELECT ISNULL(MAX(ImageID), 0) + 1 FROM ProductImage";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 1; // fallback nếu bảng đang rỗng
    }

//    // Chèn ảnh mới
//    public void insertImage(ProductImageDTO dto) throws SQLException, ClassNotFoundException {
//        String getIDSql = "SELECT ISNULL(MAX(ImageID), 0) + 1 FROM ProductImage";
//        String insertSql = "INSERT INTO ProductImage (ImageID, ImageURL, ProductID) VALUES (?, ?, ?)";
//
//        try ( Connection conn = DBUtils.getConnection();  PreparedStatement psGetID = conn.prepareStatement(getIDSql);  ResultSet rs = psGetID.executeQuery()) {
//
//            int imageID = 1;
//            if (rs.next()) {
//                imageID = rs.getInt(1);
//            }
//
//            try ( PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
//                psInsert.setInt(1, imageID);
//                psInsert.setString(2, dto.getImageURL());
//                psInsert.setInt(3, dto.getProductID());
//                psInsert.executeUpdate();
//            }
//        }
//
//    }

// ✅ Chèn ảnh mới – KHÔNG chèn ImageID
    public void insertImage(ProductImageDTO dto) throws SQLException, ClassNotFoundException {
        String insertSql = "INSERT INTO ProductImage (ImageURL, ProductID) VALUES (?, ?)";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setString(1, dto.getImageURL());
            ps.setInt(2, dto.getProductID());
            ps.executeUpdate();
        }
    }

    // ✅ Xóa ảnh theo ProductID
    public void deleteByProductID(int productID) throws Exception {
        String sql = "DELETE FROM ProductImage WHERE ProductID = ?";
        try (Connection con = DBUtils.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productID);
            ps.executeUpdate();
        }
    }

}

// Các hàm bổ sung nếu cần sau này:
// - getImagesByProductID(int productId)
// - deleteImage(int imageID)
// - updateImage(int imageID, String newURL)

