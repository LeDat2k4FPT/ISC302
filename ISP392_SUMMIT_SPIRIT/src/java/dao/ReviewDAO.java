package dao;

import dto.ReviewDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

public class ReviewDAO {

    public List<ReviewDTO> getReviewsByProductID(int productID) throws Exception {
        List<ReviewDTO> list = new ArrayList<>();
        String sql = "SELECT r.ReviewID, r.Rating, r.Comment, r.ReviewDate, r.UserID, r.ProductID, a.FullName "
                + "FROM Review r JOIN Account a ON r.UserID = a.UserID "
                + "WHERE r.ProductID = ?";

        try (
                 Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ReviewDTO review = new ReviewDTO(
                        rs.getInt("ReviewID"),
                        rs.getInt("Rating"),
                        rs.getString("Comment"),
                        rs.getDate("ReviewDate"),
                        rs.getInt("UserID"),
                        rs.getInt("ProductID"),
                        rs.getString("FullName")
                );
                list.add(review);
            }
            rs.close();
        }
        return list;
    }

    public int getReviewCountByUser(int userID) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM Review WHERE UserID = ?";
        try ( Connection con = DBUtils.getConnection();  PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void insertReview(int userId, int productId, int rating, String comment) throws Exception {
        String sql = "INSERT INTO Review (Rating, Comment, ReviewDate, UserID, ProductID) VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, userId);
            ps.setInt(4, productId);
            ps.executeUpdate();
        }
    }

    public void deleteExistingReview(int userId, int productId) throws Exception {
        String sql = "DELETE FROM Review WHERE UserID = ? AND ProductID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    public int countReviewsByProduct(int productId) throws Exception {
        String sql = "SELECT COUNT(*) FROM Review WHERE ProductID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public double averageRatingByProduct(int productId) throws Exception {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) FROM Review WHERE ProductID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    // ✅ Hàm mới: upsertReview – thêm hoặc cập nhật đánh giá
    public void upsertReview(int userId, int productId, int rating, String comment) throws Exception {
        String sql = "MERGE INTO Review AS target "
                + "USING (SELECT ? AS UserID, ? AS ProductID) AS source "
                + "ON target.UserID = source.UserID AND target.ProductID = source.ProductID "
                + "WHEN MATCHED THEN "
                + "    UPDATE SET Rating = ?, Comment = ?, ReviewDate = GETDATE() "
                + "WHEN NOT MATCHED THEN "
                + "    INSERT (UserID, ProductID, Rating, Comment, ReviewDate) "
                + "    VALUES (?, ?, ?, ?, GETDATE());";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            ps.setInt(5, userId);
            ps.setInt(6, productId);
            ps.setInt(7, rating);
            ps.setString(8, comment);
            ps.executeUpdate();
        }
    }
}
